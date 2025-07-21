package ru.netology.nmedia.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import javax.inject.Inject

@ViewModelScoped
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao
) : PostRepository {

    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { PostDbPagingSource(dao) }
    ).flow

    override suspend fun save(post: Post) {
        dao.insert(PostEntity.fromDto(post))
    }

    override suspend fun removeById(id: Long) {
        dao.removeById(id)
    }

    override suspend fun likeById(id: Long) {
        dao.getById(id)?.let { postEntity ->
            val updated = postEntity.copy(
                likes = if (postEntity.likedByMe) postEntity.likes - 1 else postEntity.likes + 1,
                likedByMe = !postEntity.likedByMe
            )
            dao.insert(updated)
        }
    }

    override suspend fun dislikeById(id: Long) {
        likeById(id)
    }
}

class PostDbPagingSource(
    private val dao: PostDao
) : PagingSource<Int, PostEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostEntity> {
        try {
            val pageNumber = params.key ?: 0
            val pageSize = params.loadSize
            val offset = pageNumber * pageSize

            val posts = dao.getPosts(offset, pageSize)
            val nextKey = if (posts.size < pageSize) null else pageNumber + 1
            val prevKey = if (pageNumber > 0) pageNumber - 1 else null

            return LoadResult.Page(
                data = posts,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PostEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}