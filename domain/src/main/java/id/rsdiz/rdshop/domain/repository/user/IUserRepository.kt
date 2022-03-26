package id.rsdiz.rdshop.domain.repository.user

import androidx.paging.PagingData
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.User
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Contract for User Repository
 */
interface IUserRepository {

    /**
     * Count total row in users
     */
    suspend fun count(): Resource<Int>

    /**
     * Get list of users
     */
    fun getUsers(): Flow<PagingData<User>>

    /**
     * Get specified user by [userId]
     */
    fun getUser(userId: String): Flow<Resource<User>>

    /**
     * Search user in repository
     */
    suspend fun searchUser(query: String): Resource<List<User>>

    /**
     * Insert new user to repository
     */
    suspend fun insertUser(user: User, password: String, sourceFile: File?): Resource<String>

    /**
     * Update user
     */
    suspend fun updateUser(user: User, password: String, sourceFile: File?): Resource<String>

    /**
     * Delete user from repository
     */
    suspend fun deleteUser(userId: String): Resource<String>
}
