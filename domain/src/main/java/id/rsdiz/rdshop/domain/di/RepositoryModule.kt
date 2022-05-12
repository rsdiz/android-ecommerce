package id.rsdiz.rdshop.domain.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.rsdiz.rdshop.data.di.NetworkModule
import id.rsdiz.rdshop.domain.repository.auth.AuthRepository
import id.rsdiz.rdshop.domain.repository.auth.IAuthRepository
import id.rsdiz.rdshop.domain.repository.category.CategoryRepository
import id.rsdiz.rdshop.domain.repository.category.ICategoryRepository
import id.rsdiz.rdshop.domain.repository.ongkir.IOngkirRepository
import id.rsdiz.rdshop.domain.repository.ongkir.OngkirRepository
import id.rsdiz.rdshop.domain.repository.order.IOrderRepository
import id.rsdiz.rdshop.domain.repository.order.OrderRepository
import id.rsdiz.rdshop.domain.repository.product.IProductRepository
import id.rsdiz.rdshop.domain.repository.product.ProductRepository
import id.rsdiz.rdshop.domain.repository.user.IUserRepository
import id.rsdiz.rdshop.domain.repository.user.UserRepository

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(repository: AuthRepository): IAuthRepository

    @Binds
    abstract fun bindUserRepository(repository: UserRepository): IUserRepository

    @Binds
    abstract fun bindCategoryRepository(repository: CategoryRepository): ICategoryRepository

    @Binds
    abstract fun bindProductRepository(repository: ProductRepository): IProductRepository

    @Binds
    abstract fun bindOrderRepository(repository: OrderRepository): IOrderRepository

    @Binds
    abstract fun bindOngkirRepository(repository: OngkirRepository): IOngkirRepository
}
