package id.rsdiz.rdshop.seller.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import id.rsdiz.rdshop.domain.usecase.order.OrderUseCase
import id.rsdiz.rdshop.domain.usecase.order.OrderInteractor

@Module
@InstallIn(FragmentComponent::class)
abstract class OrderModule {
    @Binds
    abstract fun provideOrderUseCase(orderInteractor: OrderInteractor): OrderUseCase
}
