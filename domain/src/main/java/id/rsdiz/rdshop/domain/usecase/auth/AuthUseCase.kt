package id.rsdiz.rdshop.domain.usecase.auth

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.repository.auth.IAuthRepository
import javax.inject.Inject

/**
 * Implementation of [IAuthUseCase]
 */
class AuthUseCase @Inject constructor(
    private val repository: IAuthRepository
) : IAuthUseCase {
    override suspend fun signUp(
        name: String,
        username: String,
        email: String,
        password: String
    ): Resource<String> = repository.signUp(name, username, email, password)

    override suspend fun signIn(login: String, password: String): Resource<String> =
        repository.signIn(login, password)

    override suspend fun signOut(apiKey: String): Resource<String> = repository.signOut(apiKey)
}
