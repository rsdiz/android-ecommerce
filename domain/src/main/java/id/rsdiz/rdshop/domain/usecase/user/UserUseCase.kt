package id.rsdiz.rdshop.domain.usecase.user

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.User
import id.rsdiz.rdshop.domain.repository.IUserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of [IUserUseCase]
 */
class UserUseCase @Inject constructor(
    private val repository: IUserRepository
) : IUserUseCase {
    override fun getUsers(): Flow<Resource<List<User>>> = repository.getUsers()

    override fun getUser(userId: String): Flow<Resource<User>> = repository.getUser(userId)

    override suspend fun searchUser(query: String): Resource<List<User>> = repository.searchUser(query)

    override suspend fun insertUser(user: User) = repository.insertUser(user)

    override suspend fun updateUser(userId: String, user: User) = repository.updateUser(userId, user)

    override suspend fun deleteUser(userId: String) = repository.deleteUser(userId)
}
