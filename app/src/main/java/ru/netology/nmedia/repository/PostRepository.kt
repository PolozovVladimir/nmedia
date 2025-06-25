package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.PostDto

interface PostRepository {
    suspend fun getAll(): List<PostDto>
    suspend fun save(post: PostDto)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
}