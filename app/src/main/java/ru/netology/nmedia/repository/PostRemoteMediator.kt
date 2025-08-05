package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val database: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    private val postDao = database.postDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    val maxId = postDao.getMaxId()
                    if (maxId == null) {
                        apiService.getLatest(state.config.pageSize)
                    } else {
                        apiService.getAfter(maxId, state.config.pageSize)
                    }
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val id = state.lastItemOrNull()?.id ?: return MediatorResult.Success(false)
                    apiService.getBefore(id, state.config.pageSize)
                }
            }

            if (!response.isSuccessful) {
                return MediatorResult.Error(HttpException(response))
            }

            val body = response.body() ?: return MediatorResult.Success(true)
            if (body.isEmpty()) {
                return MediatorResult.Success(true)
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    val existingIds = postDao.getAllIds().toSet()
                    val newPosts = body.filter { it.id !in existingIds }
                    postDao.insert(newPosts.map(PostEntity::fromDto))
                } else {
                    postDao.insert(body.map(PostEntity::fromDto))
                }
            }

            return MediatorResult.Success(false)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}