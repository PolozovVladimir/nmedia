package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun clear()

    suspend fun updateUser(login: String, pass: String)
    suspend fun registerUser(login: String, pass: String, name: String)
}