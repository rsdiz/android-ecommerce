package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Query
import id.rsdiz.rdshop.data.source.local.entity.Role
import id.rsdiz.rdshop.data.source.local.entity.UserEntity
import id.rsdiz.rdshop.data.source.local.room.base.IBaseDao
import kotlinx.coroutines.flow.Flow

/**
 * Contracts how application interacts with stored data in [UserEntity]
 */
interface IUserDao : IBaseDao<UserEntity> {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow<UserEntity>?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: Role): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username LIKE :word OR name LIKE :word OR email LIKE :word")
    fun searchUsers(word: String): Flow<List<UserEntity>>
}