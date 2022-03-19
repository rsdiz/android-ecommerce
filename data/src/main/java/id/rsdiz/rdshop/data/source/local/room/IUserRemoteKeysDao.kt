package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.rsdiz.rdshop.data.source.local.entity.UserRemoteKeysEntity

@Dao
interface IUserRemoteKeysDao {
    @Query("SELECT * FROM users_remote_keys WHERE id = :userId")
    suspend fun getUserRemoteKeys(userId: String): UserRemoteKeysEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(userRemoteKeys: List<UserRemoteKeysEntity>)

    @Query("DELETE FROM users_remote_keys")
    suspend fun deleteAll()
}
