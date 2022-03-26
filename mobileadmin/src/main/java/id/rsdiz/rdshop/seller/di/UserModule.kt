package id.rsdiz.rdshop.seller.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import id.rsdiz.rdshop.domain.usecase.user.IUserUseCase
import id.rsdiz.rdshop.domain.usecase.user.UserUseCase

@Module
@InstallIn(FragmentComponent::class)
abstract class UserModule {
    @Binds
    abstract fun provideUserUseCase(userUseCase: UserUseCase): IUserUseCase
}
