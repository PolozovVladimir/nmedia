package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.Post
import java.io.IOException

class PostRepositoryImpl : PostRepository {
    private var tempIdCounter = -1L
    private val service = ApiService.service
    private val _data = MutableLiveData<List<Post>>()

    override val data: LiveData<List<Post>>
        get() = _data

    init {
        _data.value = emptyList()
    }

    override fun generateTempId(): Long = tempIdCounter--

    override suspend fun getAll() {
        try {
            val response = service.getAll()
            if (!response.isSuccessful) {
                throw RuntimeException("HTTP error: ${response.code()}")
            }
            val body = response.body() ?: throw RuntimeException("Body is null")
            _data.postValue(body.map { it.copy(savedOnServer = true) })
        } catch (e: IOException) {
            throw RuntimeException("Network error", e)
        }
    }

    override suspend fun likeById(id: Long) {
        val oldPost = _data.value?.find { it.id == id } ?: return
        val newLikes = (oldPost.likes + if (oldPost.likedByMe) -1 else 1).coerceAtLeast(0)
        val newPost = oldPost.copyWithLikes(!oldPost.likedByMe, newLikes)

        updateLocal(newPost)

        try {
            val response = service.likeById(id)
            if (!response.isSuccessful) {
                throw RuntimeException("HTTP error: ${response.code()}")
            }
            val body = response.body() ?: throw RuntimeException("Body is null")
            updateLocal(body.copy(savedOnServer = true))
        } catch (e: Exception) {
            updateLocal(oldPost)
            throw e
        }
    }

    override suspend fun save(post: Post) {
        val postToSave = if (post.id == 0L) {
            post.copy(id = generateTempId(), savedOnServer = false)
        } else {
            post
        }

        if (postToSave.id < 0) {
            addLocal(postToSave)
        } else {
            updateLocal(postToSave)
        }

        try {
            val response = service.save(postToSave.toDto())
            if (!response.isSuccessful) {
                throw RuntimeException("HTTP error: ${response.code()}")
            }
            val body = response.body() ?: throw RuntimeException("Body is null")
            replaceLocalPost(postToSave.id, body.copy(savedOnServer = true))
        } catch (e: Exception) {
            markPostAsNotSaved(postToSave)
            throw e
        }
    }

    override suspend fun removeById(id: Long) {
        val oldPost = _data.value?.find { it.id == id } ?: return
        removeLocal(id)

        try {
            val response = service.removeById(id)
            if (!response.isSuccessful) {
                throw RuntimeException("HTTP error: ${response.code()}")
            }
        } catch (e: Exception) {
            addLocal(oldPost)
            throw e
        }
    }

    private fun updateLocal(newPost: Post) {
        _data.value = _data.value?.map { if (it.id == newPost.id) newPost else it }
    }

    private fun addLocal(post: Post) {
        _data.value = listOf(post) + (_data.value ?: emptyList())
    }

    private fun removeLocal(id: Long) {
        _data.value = _data.value?.filter { it.id != id }
    }

    private fun replaceLocalPost(oldId: Long, newPost: Post) {
        _data.value = _data.value?.map { if (it.id == oldId) newPost else it }
    }

    private fun markPostAsNotSaved(post: Post) {
        _data.value = _data.value?.map {
            if (it.id == post.id) post.copy(savedOnServer = false) else it
        }
    }
}