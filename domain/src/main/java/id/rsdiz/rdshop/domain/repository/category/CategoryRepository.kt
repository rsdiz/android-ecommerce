package id.rsdiz.rdshop.domain.repository.category

import id.rsdiz.rdshop.base.utils.AppExecutors
import id.rsdiz.rdshop.data.NetworkBoundResource
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.source.local.CategoryLocalDataSource
import id.rsdiz.rdshop.data.source.remote.CategoryRemoteDataSource
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import id.rsdiz.rdshop.data.source.remote.response.category.CategoriesResponse
import id.rsdiz.rdshop.data.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [ICategoryRepository] from Domain Layer
 */
@Singleton
class CategoryRepository @Inject constructor(
    private val remoteDataSource: CategoryRemoteDataSource,
    private val localDataSource: CategoryLocalDataSource,
    private val appExecutors: AppExecutors
) : ICategoryRepository {
    override suspend fun count(): Resource<Int> =
        when (
            val response = remoteDataSource.countCategories().first()
        ) {
            is ApiResponse.Success -> {
                Resource.Success(response.data)
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override fun getCategories(): Flow<Resource<List<Category>>> =
        object : NetworkBoundResource<List<Category>, CategoriesResponse>() {
            override fun loadFromDB(): Flow<List<Category>?> =
                localDataSource.getAllCategories().map {
                    localDataSource.mapper.mapFromEntities(it)
                }

            override fun shouldFetch(data: List<Category>?): Boolean = data.isNullOrEmpty()

            override suspend fun createCall(): Flow<ApiResponse<CategoriesResponse>> =
                remoteDataSource.getCategories()

            override suspend fun saveCallResult(data: CategoriesResponse) =
                remoteDataSource.mapper.mapRemoteToEntities(data.data).let {
                    localDataSource.insertAll(it)
                }
        }.asFlow() as Flow<Resource<List<Category>>>

    override suspend fun insertCategory(name: String): Resource<String> =
        when (val response = remoteDataSource.createCategory(name = name).first()) {
            is ApiResponse.Success -> {
                remoteDataSource.mapper.mapRemoteToEntity(response.data).let {
                    localDataSource.insert(it)
                }

                Resource.Success("Category Successfully Added!")
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun updateCategory(categoryId: String, newName: String): Resource<String> =
        when (
            val response = remoteDataSource.updateCategory(
                categoryId = categoryId,
                newName = newName
            ).first()
        ) {
            is ApiResponse.Success -> {
                remoteDataSource.mapper.mapRemoteToEntity(response.data).let {
                    appExecutors.diskIO().execute {
                        localDataSource.update(it)
                    }
                }

                Resource.Success("Category Successfully Updated!")
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun deleteCategory(categoryId: String): Resource<String> =
        when (val response = remoteDataSource.deleteCategory(categoryId = categoryId).first()) {
            is ApiResponse.Success -> {
                val data = localDataSource.getCategoryById(categoryId).first()
                localDataSource.delete(data)

                Resource.Success(response.data)
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }
}
