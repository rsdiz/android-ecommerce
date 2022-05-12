package id.rsdiz.rdshop.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import id.rsdiz.rdshop.domain.usecase.ongkir.IOngkirUseCase
import id.rsdiz.rdshop.domain.usecase.ongkir.OngkirUseCase

@Module
@InstallIn(FragmentComponent::class)
abstract class OngkirModule {
    @Binds
    abstract fun provideOngkirUseCase(ongkirUseCase: OngkirUseCase): IOngkirUseCase
}
