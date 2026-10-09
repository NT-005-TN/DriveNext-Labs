package ru.mtuci.drivenext.domain

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class AuthUseCaseTest {
    private class FakeRepository : AuthRepository {
        var calls = 0
        override suspend fun signIn(email: String, password: String): Account { calls++; return Account("id", email) }
        override suspend fun register(data: Registration): Account { calls++; return Account("id", data.email) }
        override suspend fun restore(): Account? = null
        override suspend fun googleUrl() = "https://example.com"
        override suspend fun completeOAuth(callback: String) = Account("id", "test@example.com")
        override suspend fun signOut() = Unit
        override suspend fun sendRecovery(email: String) = Unit
        override suspend fun updatePassword(password: String) = Unit
        override fun hasNetwork() = true
    }
    @Test fun emailFollowsLabPattern() {
        val useCase = AuthUseCase(FakeRepository())
        assertTrue(useCase.emailValid("name@domain.ru"))
        assertTrue(useCase.emailValid("name@domain.com"))
        assertFalse(useCase.emailValid("name@domain"))
        assertFalse(useCase.emailValid("name@domain.org"))
        assertFalse(useCase.emailValid("name domain.com"))
    }
    @Test fun invalidInputNeverCallsServer() = runBlocking {
        val repository = FakeRepository()
        val useCase = AuthUseCase(repository)
        assertTrue(runCatching { useCase.login("invalid", "password") }.isFailure)
        assertTrue(runCatching { useCase.login("name@domain.com", " ") }.isFailure)
        assertEquals(0, repository.calls)
        assertEquals("name@domain.com", useCase.login(" name@domain.com ", "password").email)
        assertEquals(1, repository.calls)
    }
    @Test fun registrationRequiresMatchingPasswordsAndConsent() {
        val useCase = AuthUseCase(FakeRepository())
        assertNotNull(useCase.validateStep1("name@domain.com", "password", "other", true))
        assertNotNull(useCase.validateStep1("name@domain.com", "password", "password", false))
        assertNotNull(useCase.validateStep1("name@domain.com", "1", "1", true))
        assertNull(useCase.validateStep1("name@domain.com", "password", "password", true))
    }
}
