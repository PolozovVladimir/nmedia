package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.appError.ApiError
import ru.netology.nmedia.appError.NetworkError
import ru.netology.nmedia.appError.UnknownError
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import java.io.IOException

class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data: LiveData<List<Post>>
        get() = dao.getAll().map { it.toDto() }

    override suspend fun getAll() {
        try {
            val response = PostApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.map { it.copy(savedOnServer = true) }.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = PostApi.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body.copy(savedOnServer = true)))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun likeById(id: Long) {
        val original = dao.getById(id)?.toDto() ?: return
        val updated = original.copy(
            likedByMe = !original.likedByMe,
            likes = if (original.likedByMe) original.likes - 1 else original.likes + 1
        )

        dao.insert(PostEntity.fromDto(updated))

        try {
            val response = if (original.likedByMe) {
                PostApi.service.dislikeById(id)
            } else {
                PostApi.service.likeById(id)
            }

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body.copy(savedOnServer = true)))
        } catch (e: Exception) {
            dao.insert(PostEntity.fromDto(original))
            throw when (e) {
                is IOException -> NetworkError
                else -> UnknownError()
            }
        }
    }

    override suspend fun dislikeById(id: Long) {
        likeById(id)
    }

    override suspend fun removeById(id: Long) {
        val original = dao.getById(id)?.toDto() ?: return
        dao.removeById(id)

        try {
            val response = PostApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: Exception) {
            dao.insert(PostEntity.fromDto(original))
            throw when (e) {
                is IOException -> NetworkError
                else -> UnknownError()
            }
        }
    }
}