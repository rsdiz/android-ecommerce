package id.rsdiz.rdshop.seller.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase
import id.rsdiz.rdshop.domain.usecase.product.ProductInteractor

@Module
@InstallIn(FragmentComponent::class)
abstract class ProductModule {
    @Binds
    abstract fun provideProductUseCase(productInteractor: ProductInteractor): ProductUseCase
}
