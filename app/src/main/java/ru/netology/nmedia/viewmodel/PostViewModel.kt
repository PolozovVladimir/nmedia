package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {
    private val _data = MutableLiveData<List<Post>>()
    val data: LiveData<List<Post>> = _data

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _refreshing = MutableLiveData(false)
    val refreshing: LiveData<Boolean> = _refreshing

    private var lastAction: (() -> Unit)? = null

    private val _editedPost = MutableLiveData<Post?>(null)
    val editedPost: LiveData<Post?> = _editedPost

    init {
        loadPosts()
    }

    fun loadPosts() {
        _refreshing.value = true
        viewModelScope.launch {
            try {
                _data.value = repository.getAll()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _refreshing.value = false
            }
        }
    }

    fun refresh() = loadPosts()

    fun likeById(id: Long) {
        lastAction = { likeById(id) }
        viewModelScope.launch {
            try {
                repository.likeById(id)
            } catch (e: Exception) {
                _error.value = "Ошибка лайка: ${e.message}"
            }
        }
    }

    fun removeById(id: Long) {
        lastAction = { removeById(id) }
        viewModelScope.launch {
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                _error.value = "Ошибка удаления: ${e.message}"
            }
        }
    }

    fun save() {
        _editedPost.value?.let { post ->
            viewModelScope.launch {
                try {
                    repository.save(post)
                    _editedPost.value = null
                } catch (e: Exception) {
                    _error.value = "Ошибка сохранения: ${e.message}"
                }
            }
        }
    }

    fun edit(post: Post) {
        _editedPost.value = post
    }

    fun retryLastAction() {
        lastAction?.invoke()
        _error.value = null
    }

    fun resetError() {
        _error.value = null
    }
}