package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.rsdiz.rdshop.data.source.local.entity.OrderRemoteKeysEntity

@Dao
interface IOrderRemoteKeysDao {
    @Query("SELECT * FROM order_remote_entity WHERE id = :orderId")
    suspend fun getOrderRemoteKeys(orderId: String): OrderRemoteKeysEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orderRemoteKeys: List<OrderRemoteKeysEntity>)

    @Query("DELETE FROM order_remote_entity")
    suspend fun deleteAll()
}
