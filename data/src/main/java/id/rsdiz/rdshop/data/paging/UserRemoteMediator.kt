package id.rsdiz.rdshop.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import id.rsdiz.rdshop.data.source.local.entity.UserRemoteKeysEntity
import id.rsdiz.rdshop.data.source.local.room.IUserDao
import id.rsdiz.rdshop.data.source.local.room.IUserRemoteKeysDao
import id.rsdiz.rdshop.data.source.remote.mapper.UserRemoteMapper
import id.rsdiz.rdshop.data.source.remote.network.ApiService
import id.rsdiz.rdshop.data.model.User

@OptIn(ExperimentalPagingApi::class)
class UserRemoteMediator(
    private val apiService: ApiService,
    private val userDao: IUserDao,
    private val userRemoteKeysDao: IUserRemoteKeysDao,
    private val mapper: UserRemoteMapper
) : RemoteMediator<Int, User>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, User>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.next?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val previous = remoteKeys?.previous
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    previous
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val next = remoteKeys?.next
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    next
                }
            }

            val response = apiService.getUsers(page = page)
            var endOfPaginationReached = false

            if (response.code == 200) {
                val responseData = response.data
                endOfPaginationReached = responseData == null
                responseData?.let { data ->
                    if (loadType == LoadType.REFRESH) {
                        userDao.deleteAll()
                        userRemoteKeysDao.deleteAll()
                    }
                    var previous: Int? = null
                    var next: Int? = null

                    data.next?.let {
                        next = page + 1
                    }

                    data.previous?.let {
                        previous = if (page <= 1) null else page - 1
                    }

                    val keys = data.results.map { user ->
                        UserRemoteKeysEntity(
                            id = user.id,
                            previous = previous,
                            next = next,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }

                    userRemoteKeysDao.insertAll(keys)
                    userDao.insertAll(mapper.mapRemoteToEntities(data.results))
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, User>
    ): UserRemoteKeysEntity? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.userId?.let {
                userRemoteKeysDao.getUserRemoteKeys(userId = it)
            }
        }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, User>
    ): UserRemoteKeysEntity? =
        state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { user ->
            userRemoteKeysDao.getUserRemoteKeys(userId = user.userId)
        }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, User>
    ): UserRemoteKeysEntity? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { user ->
            userRemoteKeysDao.getUserRemoteKeys(userId = user.userId)
        }
}
