package ru.mtuci.drivenext.data.auth

import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.MemoryCodeVerifierCache
import io.github.jan.supabase.auth.MemorySessionManager
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.client.engine.android.Android
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject
import kotlin.time.Duration.Companion.seconds

// Один SDK-клиент; постоянная сессия остаётся в зашифрованном SecureStore.
@OptIn(SupabaseInternal::class, kotlin.time.ExperimentalTime::class)
class SupabaseGateway {
    val url = "https://owkzoimnjojfuoxzufnz.supabase.co"
    private val client = createSupabaseClient(url, "sb_publishable_xs_-HbeUup8rjXg5264fNA_yjm4OfRZ") {
        defaultLogLevel = LogLevel.NONE
        httpEngine = Android.create()
        requestTimeout = 25.seconds
        httpConfig { followRedirects = false; expectSuccess = false }
        install(Auth) {
            autoLoadFromStorage = false; autoSaveToStorage = false; alwaysAutoRefresh = false
            enableLifecycleCallbacks = false
            sessionManager = MemorySessionManager()
            codeVerifierCache = MemoryCodeVerifierCache()
        }
        install(Postgrest)
        install(Storage)
    }
    data class Response(val status:Int, val body:String)
    suspend fun request(path:String, verb:String, payload:ByteArray?, token:String?, mime:String, upsert:Boolean):Response {
        val response = client.httpClient.request(url + path) {
            method = HttpMethod.parse(verb)
            headers.append("Content-Type",mime)
            token?.let { headers.append("Authorization","Bearer $it") }
            if(upsert) {
                headers.append("x-upsert","true")
                headers.append("Prefer","resolution=merge-duplicates,return=minimal")
            }
            payload?.let { setBody(it) }
        }
        return Response(response.status.value,response.bodyAsText())
    }
    suspend fun useSession(session:JSONObject) {
        client.auth.importSession(UserSession(
            accessToken=session.getString("access_token"),
            refreshToken=session.getString("refresh_token"),
            expiresIn=(session.optLong("expires_at")-System.currentTimeMillis()/1000).coerceAtLeast(1),
            tokenType="bearer"
        ),autoRefresh=false)
    }
    suspend fun rpc(name:String,parameters:JSONObject):String =
        client.postgrest.rpc(name,Json.parseToJsonElement(parameters.toString()).jsonObject).data
    suspend fun image(bucket:String,path:String):ByteArray =
        client.storage.from(bucket).downloadAuthenticated(path)
    suspend fun clearSession() = client.auth.clearSession()
}
