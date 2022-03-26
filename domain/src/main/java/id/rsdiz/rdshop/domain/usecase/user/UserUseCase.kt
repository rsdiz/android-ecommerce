package id.rsdiz.rdshop.domain.usecase.user

import androidx.paging.PagingData
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.domain.repository.user.IUserRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

/**
 * Implementation of [IUserUseCase]
 */
class UserUseCase @Inject constructor(
    private val repository: IUserRepository
) : IUserUseCase {
    override suspend fun count() = repository.count()

    override fun getUsers(): Flow<PagingData<User>> = repository.getUsers()

    override fun getUser(userId: String) = repository.getUser(userId)

    override suspend fun searchUser(query: String) =
        repository.searchUser(query)

    override suspend fun insertUser(
        user: User,
        password: String,
        sourceFile: File?
    ) = repository.insertUser(user, password, sourceFile)

    override suspend fun updateUser(
        user: User,
        password: String,
        sourceFile: File?
    ) = repository.updateUser(user, password, sourceFile)

    override suspend fun deleteUser(userId: String) = repository.deleteUser(userId)
}
