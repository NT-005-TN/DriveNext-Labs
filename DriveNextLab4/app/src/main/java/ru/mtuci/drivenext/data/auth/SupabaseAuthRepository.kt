package ru.mtuci.drivenext.data.auth

import android.content.Context
import ru.mtuci.drivenext.BuildConfig
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.mtuci.drivenext.data.connectivity.NetworkMonitor
import ru.mtuci.drivenext.domain.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SupabaseAuthRepository(context: Context, private val store: SecureStore) : AuthRepository {
    private val context = context.applicationContext
    private val lock = Mutex()
    private val base = "https://owkzoimnjojfuoxzufnz.supabase.co"
    // Публичный клиентский ключ: доступ к данным ограничен RLS, не этим ключом.
    private val publicKey = "sb_publishable_xs_-HbeUup8rjXg5264fNA_yjm4OfRZ"
    private val redirect = "drivenextlab" + BuildConfig.APPLICATION_ID.substringAfterLast("lab") + "://auth/callback"
    override fun hasNetwork() = NetworkMonitor(context).hasInternetConnection()
    private suspend fun <T> io(block: () -> T): T = withContext(Dispatchers.IO) {
        lock.withLock { if (!hasNetwork()) throw NoInternetException(); block() }
    }

    private class ApiError(val status: Int, val code: String, message: String) : Exception(message)

    // Запросы выполняются на IO-потоке, с тайм-аутами и без вывода токенов в логи.
    private fun request(path: String, method: String = "GET", body: ByteArray? = null,
                        token: String? = null, mime: String = "application/json", upsert: Boolean = false): String {
        val connection = URL(base + path).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.connectTimeout = 15000
            connection.readTimeout = 20000
            connection.instanceFollowRedirects = false
            connection.setRequestProperty("apikey", publicKey)
            connection.setRequestProperty("Content-Type", mime)
            token?.let { connection.setRequestProperty("Authorization", "Bearer $it") }
            if (upsert) {
                connection.setRequestProperty("x-upsert", "true")
                connection.setRequestProperty("Prefer", "resolution=merge-duplicates,return=minimal")
            }
            if (body != null) { connection.doOutput = true; connection.outputStream.use { it.write(body) } }
            val status = connection.responseCode
            val response = (if (status in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (status !in 200..299) {
                val json = runCatching { JSONObject(response) }.getOrDefault(JSONObject())
                val code = json.optString("error_code", json.optString("code"))
                val message = when (code) {
                    "invalid_credentials" -> "Неверный email или пароль."
                    "email_not_confirmed" -> "Подтвердите адрес по письму Supabase, затем повторите вход или нажмите «Далее»."
                    "user_already_exists", "email_exists" -> "Такой пользователь уже существует. Войдите в аккаунт."
                    "over_email_send_rate_limit", "over_request_rate_limit" -> "Слишком много запросов. Подождите и повторите попытку."
                    else -> json.optString("msg", json.optString("message", json.optString("error_description", "Ошибка сервера ($status). Повторите попытку."))).take(500)
                }
                throw ApiError(status, code, message)
            }
            return response
        } catch (e: java.io.IOException) {
            if (!hasNetwork()) throw NoInternetException()
            throw AuthException("Не удалось связаться с сервером. Проверьте сеть и повторите попытку.")
        } finally { connection.disconnect() }
    }
    private fun jsonRequest(path: String, data: JSONObject, token: String? = null, method: String = "POST") =
        JSONObject(request(path, method, data.toString().toByteArray(), token).ifBlank { "{}" })
    private fun session() = store.get("session")?.let { runCatching { JSONObject(it) }.getOrNull() }
    private fun saveSession(json: JSONObject): Account {
        if (json.optString("access_token").isBlank() || json.optString("refresh_token").isBlank()) throw AuthException("Сервер не выдал сессию. Подтвердите email и войдите снова.")
        json.put("expires_at", System.currentTimeMillis() / 1000 + json.optLong("expires_in", 3600))
        store.put("session", json.toString())
        return account(json.getJSONObject("user"))
    }
    private fun account(user: JSONObject) = Account(user.getString("id"), user.optString("email"))
    private fun activeSession(): JSONObject {
        var data = session() ?: throw AuthException("Войдите в аккаунт.")
        if (data.optLong("expires_at") <= System.currentTimeMillis() / 1000 + 60) {
            try {
                data = jsonRequest("/auth/v1/token?grant_type=refresh_token", JSONObject().put("refresh_token", data.getString("refresh_token")))
                saveSession(data)
            } catch (e: ApiError) {
                if (e.status in listOf(400, 401, 403)) store.remove("session")
                throw e
            }
        }
        return data
    }
    private fun login(email: String, password: String) = saveSession(jsonRequest("/auth/v1/token?grant_type=password", JSONObject().put("email", email).put("password", password)))
    override suspend fun signIn(email: String, password: String) = io { login(email, password) }
    override suspend fun restore(): Account? = io {
        if (session() == null) return@io null
        try { account(JSONObject(request("/auth/v1/user", token = activeSession().getString("access_token")))) }
        catch (e: ApiError) {
            if (e.status in listOf(400, 401, 403)) { store.remove("session"); null } else throw e
        }
    }
    override suspend fun register(data: Registration): Account = io {
        val pending = store.get("pending_registration")
        val user = if (pending == data.email) {
            login(data.email, data.password)
        } else {
            val result = jsonRequest("/auth/v1/signup", JSONObject().put("email", data.email).put("password", data.password))
            store.put("pending_registration", data.email)
            if (result.optString("access_token").isBlank()) throw AuthException("Отправлено письмо подтверждения. Подтвердите email, вернитесь сюда и нажмите «Далее» ещё раз. Данные формы сохранены.")
            saveSession(result)
        }
        val token = activeSession().getString("access_token")
        val license = upload(user.id, "license", data.licensePhoto, token)
        val passport = upload(user.id, "passport", data.passportPhoto, token)
        val avatar = data.profilePhoto?.let { upload(user.id, "avatar", it, token) }
        val profile = JSONObject().put("id", user.id).put("surname", data.surname).put("name", data.name)
            .put("patronymic", data.patronymic).put("gender", data.gender)
            .put("birth_date", LocalDate.parse(data.birthDate, DateTimeFormatter.ofPattern("MM/dd/uuuu")).toString())
            .put("license_number", data.license)
            .put("issue_date", LocalDate.parse(data.issueDate, DateTimeFormatter.ofPattern("dd/MM/uuuu")).toString())
            .put("license_photo", license).put("passport_photo", passport).put("profile_photo", avatar ?: JSONObject.NULL)
        request("/rest/v1/profiles?on_conflict=id", "POST", profile.toString().toByteArray(), token, upsert = true)
        store.remove("pending_registration")
        user
    }
    private fun upload(userId: String, kind: String, source: String, token: String, bucket: String = "registration-documents"): String {
        val uri = Uri.parse(source)
        val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
        if (mime !in listOf("image/jpeg", "image/png", "image/webp")) throw AuthException("Выберите фото JPEG, PNG или WebP.")
        val bytes = try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val output = java.io.ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                while (output.size() <= 10 * 1024 * 1024) {
                    val count = input.read(buffer)
                    if (count == -1) break
                    output.write(buffer, 0, count)
                }
                output.toByteArray()
            }
        } catch (_: Exception) { throw AuthException("Нет доступа к фото. Выберите его снова.") }
        if (bytes == null || bytes.isEmpty()) throw AuthException("Не удалось прочитать фото. Выберите его снова.")
        if (bytes.size > 10 * 1024 * 1024) throw AuthException("Фото должно быть не больше 10 МБ.")
        val path = "$userId/$kind"
        request("/storage/v1/object/$bucket/$path", "POST", bytes, token, mime, true)
        return path
    }

    suspend fun call(path: String, method: String = "GET", data: JSONObject? = null): String = io {
        request(path, method, data?.toString()?.toByteArray(), activeSession().getString("access_token"), upsert = method == "POST" && path.startsWith("/rest/v1/") && !path.contains("/rpc/"))
    }
    suspend fun ownerId(): String = io { activeSession().getJSONObject("user").getString("id") }
    suspend fun photo(bucket: String, path: String, source: String): String = io {
        val session = activeSession()
        upload(session.getJSONObject("user").getString("id"), path, source, session.getString("access_token"), bucket)
    }
    suspend fun image(bucket: String, path: String): android.graphics.Bitmap? = io {
        val response = jsonRequest("/storage/v1/object/sign/" + bucket + "/" + path,
            JSONObject().put("expiresIn", 60), activeSession().getString("access_token"))
        val signed = response.getString("signedURL")
        val url = if (signed.startsWith("/storage/v1/")) base + signed else base + "/storage/v1" + signed
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 15000; connection.readTimeout = 20000
            connection.inputStream.use { android.graphics.BitmapFactory.decodeStream(it) }
        } finally { connection.disconnect() }
    }
    private fun random() = Base64.encodeToString(ByteArray(32).also { SecureRandom().nextBytes(it) }, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    private fun startPkce(recovery: Boolean): Pair<String, String> {
        val verifier = random()
        val state = random()
        store.put("oauth", JSONObject().put("verifier", verifier).put("state", state).put("recovery", recovery).put("time", System.currentTimeMillis()).toString())
        val challenge = Base64.encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray()), Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        return "$redirect?state=$state" to challenge
    }
    override suspend fun googleUrl(): String = io {
        val settings = JSONObject(request("/auth/v1/settings"))
        if (settings.optJSONObject("external")?.optBoolean("google") != true) throw AuthException("Google OAuth ещё не включён в Supabase. Нужны настройки Google Cloud и провайдера Google.")
        val (callback, challenge) = startPkce(false)
        Uri.parse("$base/auth/v1/authorize").buildUpon().appendQueryParameter("provider", "google")
            .appendQueryParameter("redirect_to", callback).appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("code_challenge_method", "s256").build().toString()
    }
    override suspend fun completeOAuth(callback: String): Account = io {
        val uri = Uri.parse(callback)
        if (uri.scheme != Uri.parse(redirect).scheme || uri.host != "auth" || uri.path != "/callback") throw AuthException("Неверный адрес возврата.")
        val pending = store.get("oauth")?.let(::JSONObject) ?: throw AuthException("Начните вход заново.")
        if (uri.getQueryParameter("state") != pending.getString("state") || System.currentTimeMillis() - pending.getLong("time") > 60 * 60 * 1000) throw AuthException("Попытка входа устарела. Начните заново.")
        if (uri.getQueryParameter("error") != null) { store.remove("oauth"); throw AuthException("Вход отменён или отклонён провайдером. Попробуйте ещё раз.") }
        val code = uri.getQueryParameter("code") ?: throw AuthException("Сервер не вернул код входа.")
        val result = jsonRequest("/auth/v1/token?grant_type=pkce", JSONObject().put("auth_code", code).put("code_verifier", pending.getString("verifier")))
        val user = saveSession(result)
        if (pending.optBoolean("recovery")) store.put("recovery", "true")
        store.remove("oauth")
        user
    }
    override suspend fun sendRecovery(email: String) = io {
        val (callback, challenge) = startPkce(true)
        jsonRequest("/auth/v1/recover?redirect_to=${Uri.encode(callback)}", JSONObject().put("email", email).put("code_challenge", challenge).put("code_challenge_method", "s256"))
        Unit
    }
    override suspend fun updatePassword(password: String) = io {
        jsonRequest("/auth/v1/user", JSONObject().put("password", password), activeSession().getString("access_token"), "PUT")
        store.remove("recovery")
    }
    override suspend fun signOut() = withContext(Dispatchers.IO) {
        lock.withLock {
            val token = session()?.optString("access_token")
            // Локальный выход работает и при недоступном сервере.
            try {
                if (hasNetwork() && !token.isNullOrBlank()) request("/auth/v1/logout?scope=local", "POST", token = token)
            } catch (_: ApiError) {
                // Серверная сессия могла уже истечь.
            } catch (_: AuthException) {
                // При сбое сети всё равно удаляем локальные токены.
            } catch (_: NoInternetException) {
                // Сеть могла исчезнуть во время запроса.
            } finally {
                store.remove("session"); store.remove("recovery"); store.remove("oauth"); store.remove("car_draft")
            }
            Unit
        }
    }
}
