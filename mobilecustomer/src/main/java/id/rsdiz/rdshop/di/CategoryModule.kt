package id.rsdiz.rdshop.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import id.rsdiz.rdshop.domain.usecase.category.CategoryInteractor
import id.rsdiz.rdshop.domain.usecase.category.CategoryUseCase

@Module
@InstallIn(FragmentComponent::class)
abstract class CategoryModule {
    @Binds
    abstract fun provideCategoryUseCase(categoryInteractor: CategoryInteractor): CategoryUseCase
}
