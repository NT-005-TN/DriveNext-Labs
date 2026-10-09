package ru.mtuci.drivenext.domain

data class Account(val id: String, val email: String)
data class Registration(
    val email: String, val password: String, val surname: String, val name: String,
    val patronymic: String, val birthDate: String, val gender: String,
    val license: String, val issueDate: String, val profilePhoto: String?,
    val licensePhoto: String, val passportPhoto: String
)
class NoInternetException : Exception("Нет подключения к интернету.")
class AuthException(message: String) : Exception(message)
interface AuthRepository {
    suspend fun signIn(email: String, password: String): Account
    suspend fun register(data: Registration): Account
    suspend fun restore(): Account?
    suspend fun googleUrl(): String
    suspend fun completeOAuth(callback: String): Account
    suspend fun signOut()
    suspend fun sendRecovery(email: String)
    suspend fun updatePassword(password: String)
    fun hasNetwork(): Boolean
}
