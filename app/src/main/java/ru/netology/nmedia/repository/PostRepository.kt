package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    suspend fun getAll(): List<Post>
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
}