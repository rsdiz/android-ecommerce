package id.rsdiz.rdshop.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.rsdiz.rdshop.domain.usecase.auth.AuthUseCase
import id.rsdiz.rdshop.domain.usecase.auth.IAuthUseCase

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun provideAuthUseCase(authUseCase: AuthUseCase): IAuthUseCase
}
