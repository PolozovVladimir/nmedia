package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.appError.NetworkError
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.PostDto
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao
) : PostRepository {
    override suspend fun getAll(): List<PostDto> {
        try {
            val response = PostsApi.service.getAll()
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

    override suspend fun save(post: PostDto) {
        try {
            val response = PostsApi.service.save(post)
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
            dao.removeById(id)
            PostsApi.service.removeById(id)
        } catch (e: Exception) {
            throw NetworkError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val post = dao.getById(id).toDto()
            val newPost = post.copy(
                likes = if (post.likedByMe) post.likes - 1 else post.likes + 1,
                likedByMe = !post.likedByMe
            )
            dao.insert(PostEntity.fromDto(newPost))

            if (newPost.likedByMe) {
                PostsApi.service.likeById(id)
            } else {
                PostsApi.service.dislikeById(id)
            }
        } catch (e: Exception) {
            throw NetworkError
        }
    }
}