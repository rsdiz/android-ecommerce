package id.rsdiz.rdshop.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.rsdiz.rdshop.base.utils.Consts
import id.rsdiz.rdshop.data.source.local.room.Database
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database {
        val passphrase = SQLiteDatabase.getBytes(Consts.PASSPHRASE.toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(context, Database::class.java, Consts.DB_NAME)
            .fallbackToDestructiveMigration()
//            .openHelperFactory(factory)
            .build()
    }

    @Provides
    fun provideUserDao(database: Database) = database.userDao()

    @Provides
    fun provideUserRemoteKeysDao(database: Database) = database.userRemoteKeysDao()

    @Provides
    fun provideCategoryDao(database: Database) = database.categoryDao()

    @Provides
    fun provideProductDao(database: Database) = database.productDao()

    @Provides
    fun provideProductImageDao(database: Database) = database.productImageDao()

    @Provides
    fun provideProductRemoteKeysDao(database: Database) = database.productRemoteKeysDao()

    @Provides
    fun provideOrderDao(database: Database) = database.orderDao()

    @Provides
    fun provideDetailOrderDao(database: Database) = database.detailOrderDao()

    @Provides
    fun provideOrderRemoteKeysDao(database: Database) = database.orderRemoteKeysDao()
}
