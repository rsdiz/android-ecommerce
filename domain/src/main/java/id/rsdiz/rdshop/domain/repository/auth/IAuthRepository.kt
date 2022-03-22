package id.rsdiz.rdshop.domain.repository.auth

import id.rsdiz.rdshop.data.Resource

/**
 * Contract for Category Repository
 */
interface IAuthRepository {

    /**
     * Do Sign Up new Account to System
     */
    suspend fun signUp(
        name: String,
        username: String,
        email: String,
        password: String
    ): Resource<String>

    /**
     * Do Sign In with existing Account
     */
    suspend fun signIn(login: String, password: String): Resource<String>

    /**
     * Do Sign Out from System
     */
    suspend fun signOut(apiKey: String): Resource<String>
}
