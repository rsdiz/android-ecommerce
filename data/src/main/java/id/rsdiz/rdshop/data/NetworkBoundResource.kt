package id.rsdiz.rdshop.data

import id.rsdiz.rdshop.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.*

/**
 * Helper class wrapping access to a resource behind network + database cache
 */
abstract class NetworkBoundResource<ResultType, RequestType> {
    private var result: Flow<Resource<out ResultType?>> = flow {
        emit(Resource.Loading())
        val databaseSource = loadFromDB()?.first()
        if (shouldFetch(databaseSource)) {
            emit(Resource.Loading())
            when (val apiResponse = createCall().first()) {
                is ApiResponse.Success -> {
                    saveCallResult(apiResponse.data)
                    loadFromDB()?.let { data -> emitAll(data.map { Resource.Success(it) }) }
                }
                is ApiResponse.Empty -> {
                    loadFromDB()?.let { data -> emitAll(data.map { Resource.Success(it) }) }
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    emit(Resource.Error<ResultType>(apiResponse.errorMessage))
                }
            }
        } else {
            loadFromDB()?.let { data -> emitAll(data.map { Resource.Success(it) }) }
        }
    }

    protected open fun onFetchFailed() {}
    protected abstract fun loadFromDB(): Flow<ResultType?>?
    protected abstract fun shouldFetch(data: ResultType?): Boolean
    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>
    protected abstract suspend fun saveCallResult(data: RequestType)

    fun asFlow(): Flow<Resource<out ResultType?>> = result
}
