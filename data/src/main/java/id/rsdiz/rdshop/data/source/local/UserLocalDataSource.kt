package id.rsdiz.rdshop.data.source.local

import id.rsdiz.rdshop.data.source.local.entity.Role
import id.rsdiz.rdshop.data.source.local.entity.UserEntity
import id.rsdiz.rdshop.data.source.local.mapper.UserMapper
import id.rsdiz.rdshop.data.source.local.room.IUserDao
import id.rsdiz.rdshop.data.source.local.room.IUserRemoteKeysDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserLocalDataSource @Inject constructor(
    val userDao: IUserDao,
    val userRemoteKeysDao: IUserRemoteKeysDao,
    val mapper: UserMapper
) {
    fun getAllUsers() = userDao.getAllUsers()

    fun getUserById(userId: String) = userDao.getUserById(userId)

    fun getUsersByRole(role: Role) = userDao.getUsersByRole(role)

    fun searchUsers(word: String) = userDao.searchUsers(word)

    fun update(data: UserEntity) = userDao.update(data)

    suspend fun insertAll(list: List<UserEntity>) = userDao.insertAll(list)

    suspend fun insert(data: UserEntity) = userDao.insert(data)

    suspend fun delete(data: UserEntity) = userDao.delete(data)
}
