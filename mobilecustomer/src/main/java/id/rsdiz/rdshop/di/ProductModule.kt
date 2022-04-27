package id.rsdiz.rdshop.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import id.rsdiz.rdshop.domain.usecase.product.IProductUseCase
import id.rsdiz.rdshop.domain.usecase.product.ProductUseCase

@Module
@InstallIn(FragmentComponent::class)
abstract class ProductModule {
    @Binds
    abstract fun provideProductUseCase(productUseCase: ProductUseCase): IProductUseCase
}
