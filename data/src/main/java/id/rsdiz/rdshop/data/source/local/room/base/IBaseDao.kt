package id.rsdiz.rdshop.data.source.local.room.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

/**
 * Contract for DAO
 */
interface IBaseDao<E> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<E>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: E)

    @Update
    fun update(data: E)

    @Delete
    suspend fun delete(data: E)
}
