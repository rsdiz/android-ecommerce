package id.rsdiz.rdshop.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import id.rsdiz.rdshop.data.source.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        ProductImageEntity::class,
        OrderEntity::class,
        DetailOrderEntity::class,
        UserRemoteKeysEntity::class,
        ProductRemoteKeysEntity::class,
        OrderRemoteKeysEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): IUserDao
    abstract fun userRemoteKeysDao(): IUserRemoteKeysDao
    abstract fun categoryDao(): ICategoryDao
    abstract fun productDao(): IProductDao
    abstract fun productImageDao(): IProductImageDao
    abstract fun productRemoteKeysDao(): IProductRemoteKeysDao
    abstract fun orderDao(): IOrderDao
    abstract fun orderRemoteKeysDao(): IOrderRemoteKeysDao
    abstract fun detailOrderDao(): IDetailOrderDao
}
