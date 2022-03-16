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
        DetailOrderEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class Database : RoomDatabase() {
    abstract fun userDao(): IUserDao
    abstract fun categoryDao(): ICategoryDao
    abstract fun productDao(): IProductDao
    abstract fun orderDao(): IOrderDao
}