package ru.netology.nmedia.repository

import ru.netology.nmedia.api.ApiService

import ru.netology.nmedia.appError.ApiError
import ru.netology.nmedia.appError.NetworkError
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao
) : PostRepository {
    override suspend fun getAll(): List<Post> {
        try {
            val response = ApiService.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val posts = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(posts.map(PostEntity::fromDto))
            return dao.getAll().map(PostEntity::toDto)
        } catch (e: IOException) {
            val posts = dao.getAll().map(PostEntity::toDto)
            if (posts.isNotEmpty()) {
                return posts
            }
            throw NetworkError
        } catch (e: Exception) {
            throw NetworkError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = ApiService.service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw NetworkError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val post = dao.getById(id).toDto()
            dao.removeById(id)
            ApiService.service.removeById(id)
        } catch (e: Exception) {
            dao.insert(PostEntity.fromDto(post))
            throw NetworkError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val post = dao.getById(id).toDto()
            val updatedPost = post.copy(
                likes = if (post.likedByMe) post.likes - 1 else post.likes + 1,
                likedByMe = !post.likedByMe
            )

            dao.insert(PostEntity.fromDto(updatedPost))

            if (updatedPost.likedByMe) {
                ApiService.service.likeById(id)
            } else {
                ApiService.service.dislikeById(id)
            }
        } catch (e: Exception) {
            dao.insert(PostEntity.fromDto(post))
            throw NetworkError
        }
    }
}