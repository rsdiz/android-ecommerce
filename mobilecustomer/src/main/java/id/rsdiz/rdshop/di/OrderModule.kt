package id.rsdiz.rdshop.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import id.rsdiz.rdshop.domain.usecase.order.IOrderUseCase
import id.rsdiz.rdshop.domain.usecase.order.OrderUseCase

@Module
@InstallIn(FragmentComponent::class)
abstract class OrderModule {
    @Binds
    abstract fun provideOrderUseCase(orderUseCase: OrderUseCase): IOrderUseCase
}
