package id.rsdiz.rdshop.domain.usecase.user

import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Contract for User Use Case
 */
interface IUserUseCase {

    /**
     * Get list of users
     */
    fun getUsers(): Flow<Resource<List<User>>>

    /**
     * Get specified user by [userId]
     */
    fun getUser(userId: String): Flow<Resource<User>>

    /**
     * Search user
     */
    suspend fun searchUser(query: String): Resource<List<User>>

    /**
     * Insert new user
     */
    suspend fun insertUser(user: User, password: String, sourceFile: File?): Resource<String>

    /**
     * Update user
     */
    suspend fun updateUser(user: User, password: String, sourceFile: File?): Resource<String>

    /**
     * Delete user
     */
    suspend fun deleteUser(userId: String): Resource<String>
}
