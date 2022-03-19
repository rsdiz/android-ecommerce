package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.rsdiz.rdshop.data.source.local.entity.ProductRemoteKeysEntity

@Dao
interface IProductRemoteKeysDao {
    @Query("SELECT * FROM product_remote_keys WHERE id = :productId")
    suspend fun getProductRemoteKeys(productId: String): ProductRemoteKeysEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ProductRemoteKeys: List<ProductRemoteKeysEntity>)

    @Query("DELETE FROM product_remote_keys")
    suspend fun deleteAll()
}
