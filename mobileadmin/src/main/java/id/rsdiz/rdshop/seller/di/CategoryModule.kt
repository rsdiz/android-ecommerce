package id.rsdiz.rdshop.seller.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import id.rsdiz.rdshop.domain.usecase.category.CategoryUseCase
import id.rsdiz.rdshop.domain.usecase.category.ICategoryUseCase

@Module
@InstallIn(FragmentComponent::class)
abstract class CategoryModule {
    @Binds
    abstract fun provideCategoryUseCase(categoryUseCase: CategoryUseCase): ICategoryUseCase
}
