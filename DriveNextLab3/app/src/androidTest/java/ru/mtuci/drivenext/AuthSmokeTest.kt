package ru.mtuci.drivenext

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.mtuci.drivenext.data.auth.SecureStore
import ru.mtuci.drivenext.data.auth.SupabaseAuthRepository

@RunWith(AndroidJUnit4::class)
class AuthSmokeTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun encryptedStoreRoundTrip() {
        val store = SecureStore(context)
        val name = "instrumentation_test_only"
        try {
            store.put(name, "sample-not-a-real-token")
            assertEquals("sample-not-a-real-token", store.get(name))
            val diskValue = context.getSharedPreferences("secure_auth", 0).getString(name, "")!!
            assertFalse(diskValue.contains("sample-not-a-real-token"))
        } finally { store.remove(name) }
    }

    @Test fun serverRejectsWrongPassword() = runBlocking {
        val repository = SupabaseAuthRepository(context, SecureStore(context))
        assertTrue("Эмулятор должен иметь интернет для сетевого smoke-теста", repository.hasNetwork())
        val error = runCatching { repository.signIn("nonexistent-lab-smoke@example.com", "not-a-real-user-password") }.exceptionOrNull()
        assertNotNull("Неверные данные не должны открывать главный экран", error)
        assertEquals("Неверный email или пароль.", error!!.message)
    }

    @Test fun foreignOAuthCallbackIsRejected() = runBlocking {
        val repository = SupabaseAuthRepository(context, SecureStore(context))
        val error = runCatching { repository.completeOAuth("untrusted://auth/callback?code=not-real") }.exceptionOrNull()
        assertNotNull(error)
        assertEquals("Неверный адрес возврата.", error!!.message)
    }

    @Test fun googleProviderBuildsProtectedAuthorizationRequest() = runBlocking {
        val store = SecureStore(context)
        val previous = store.get("oauth")
        try {
            val repository = SupabaseAuthRepository(context, store)
            val uri = android.net.Uri.parse(repository.googleUrl())
            assertEquals("https", uri.scheme)
            assertEquals("owkzoimnjojfuoxzufnz.supabase.co", uri.host)
            assertEquals("/auth/v1/authorize", uri.path)
            assertEquals("google", uri.getQueryParameter("provider"))
            assertEquals("s256", uri.getQueryParameter("code_challenge_method"))
            assertEquals(43, uri.getQueryParameter("code_challenge")!!.length)
            val callback = android.net.Uri.parse(uri.getQueryParameter("redirect_to"))
            assertEquals("drivenextlab3", callback.scheme)
            val pending = org.json.JSONObject(store.get("oauth")!!)
            assertTrue(callback.getQueryParameter("state") == pending.getString("state"))
            val error = runCatching {
                repository.completeOAuth("drivenextlab3://auth/callback?state=wrong&code=fake")
            }.exceptionOrNull()
            assertEquals("Попытка входа устарела. Начните заново.", error?.message)
        } finally {
            if (previous == null) store.remove("oauth") else store.put("oauth", previous)
        }
    }
}
