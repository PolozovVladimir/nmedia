package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {
    val data: LiveData<List<Post>> = repository.data.asLiveData()
    val edited = SingleLiveEvent<Post>()
    val error = SingleLiveEvent<String>()

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            repository.getAll()
        } catch (e: Exception) {
            error.postValue(e.message ?: "Unknown error")
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likeById(id)
        } catch (e: Exception) {
            error.postValue(e.message ?: "Unknown error")
        }
    }

    fun save() {
        edited.value?.let {
            save(it)
        }
        edited.value = Post.empty()
    }

    private fun save(post: Post) = viewModelScope.launch {
        try {
            repository.save(post)
        } catch (e: Exception) {
            error.postValue(e.message ?: "Unknown error")
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        edited.value?.let {
            val text = content.trim()
            if (it.content != text) {
                edited.value = it.copy(content = text)
            }
        }
    }

    fun cancelEdit() {
        edited.value = Post.empty()
    }
}