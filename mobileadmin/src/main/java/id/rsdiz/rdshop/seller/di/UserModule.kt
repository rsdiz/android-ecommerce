package id.rsdiz.rdshop.seller.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import id.rsdiz.rdshop.domain.usecase.user.UserUseCase
import id.rsdiz.rdshop.domain.usecase.user.UserInteractor

@Module
@InstallIn(FragmentComponent::class)
abstract class UserModule {
    @Binds
    abstract fun provideUserUseCase(userInteractor: UserInteractor): UserUseCase
}
