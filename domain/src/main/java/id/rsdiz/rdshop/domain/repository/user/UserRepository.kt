package id.rsdiz.rdshop.domain.repository.user

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.rsdiz.rdshop.base.utils.AppExecutors
import id.rsdiz.rdshop.data.NetworkBoundResource
import id.rsdiz.rdshop.data.Resource
import id.rsdiz.rdshop.data.model.User
import id.rsdiz.rdshop.data.paging.UserRemoteMediator
import id.rsdiz.rdshop.data.source.local.UserLocalDataSource
import id.rsdiz.rdshop.data.source.remote.UserRemoteDataSource
import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import id.rsdiz.rdshop.data.source.remote.response.user.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [IUserRepository] from Domain Layer
 */
@Singleton
class UserRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val appExecutor: AppExecutors
) : IUserRepository {

    override suspend fun count(): Resource<Int> =
        when (
            val response = remoteDataSource.countUsers().first()
        ) {
            is ApiResponse.Success -> Resource.Success(response.data)
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun getUsers(): Flow<PagingData<User>> = Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = UserRemoteMediator(
            apiService = remoteDataSource.apiService,
            userDao = localDataSource.userDao,
            userRemoteKeysDao = localDataSource.userRemoteKeysDao,
            mapper = remoteDataSource.mapper
        ),
        pagingSourceFactory = { localDataSource.getAllUsers() }
    ).flow

    override fun getUser(userId: String): Flow<Resource<User>> =
        object : NetworkBoundResource<User, UserResponse>() {
            override fun loadFromDB(): Flow<User?> =
                localDataSource.getUserById(userId = userId).map {
                    it?.let { localDataSource.mapper.mapFromEntity(it) }
                }

            override fun shouldFetch(data: User?): Boolean = true

            override suspend fun createCall(): Flow<ApiResponse<UserResponse>> =
                remoteDataSource.getUserById(userId = userId)

            override suspend fun saveCallResult(data: UserResponse) =
                remoteDataSource.mapper.mapRemoteToEntity(data).let {
                    localDataSource.insert(it)
                }
        }.asFlow() as Flow<Resource<User>>

    override suspend fun searchUser(query: String): Resource<List<User>> {
        val result = localDataSource.searchUsers(query).first()
        val data = localDataSource.mapper.mapFromEntities(result)

        return Resource.Success(data)
    }

    override suspend fun insertUser(
        user: User,
        password: String,
        sourceFile: File?
    ): Resource<String> =
        when (
            val response = remoteDataSource.createUser(
                user = user,
                password = password,
                sourceFile = sourceFile
            ).first()
        ) {
            is ApiResponse.Success -> {
                remoteDataSource.mapper.mapRemoteToEntity(response.data!!).let {
                    localDataSource.insert(it)
                }

                Resource.Success("User Successfully Added!")
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun updateUser(
        user: User,
        password: String,
        sourceFile: File?
    ): Resource<String> =
        when (val response = remoteDataSource.updateUser(user, password, sourceFile).first()) {
            is ApiResponse.Success -> {
                localDataSource.mapper.mapToEntity(user).let {
                    appExecutor.diskIO().execute {
                        localDataSource.update(it)
                    }
                }

                Resource.Success("User Successfully Updated!")
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }

    override suspend fun deleteUser(userId: String): Resource<String> =
        when (val response = remoteDataSource.deleteUser(userId = userId).first()) {
            is ApiResponse.Success -> {
                val data = localDataSource.getUserById(userId)?.first()
                data?.let { localDataSource.delete(it) }

                Resource.Success(response.data!!)
            }
            is ApiResponse.Empty -> Resource.Error(response.toString(), null)
            else -> Resource.Error((response as ApiResponse.Error).errorMessage, null)
        }
}
