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
        UserRemoteKeysEntity::class
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
    abstract fun orderDao(): IOrderDao
    abstract fun detailOrderDao(): IDetailOrderDao
}
