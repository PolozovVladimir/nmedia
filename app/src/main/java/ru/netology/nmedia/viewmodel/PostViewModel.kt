package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent



private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    //attachment = null
    savedOnServer = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
    private val _dataState = MutableLiveData<FeedModelState>(FeedModelState(idle = true))
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated
//    private val _data = MutableLiveData(FeedModel())
//    val data: LiveData<FeedModel>
//        get() = _data
//    private val edited = MutableLiveData(empty)
//    private val _postCreated = SingleLiveEvent<Unit>()
//    val postCreated: LiveData<Unit>
//        get() = _postCreated
//    private val _requestCode = MutableLiveData<Int>()
//    val requestCode: LiveData<Int> = _requestCode

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        _dataState.value = FeedModelState(loading = true)
        try {
            repository.getAll()
            _dataState.value = FeedModelState(idle = true)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


//        _data.value = FeedModel(loading = true)
//        repository.getAllAsync(object : PostRepository.Callback<List<Post>> {
//            override fun onSuccess(posts: List<Post>) {
//                _data.value = FeedModel(posts = posts, empty = posts.isEmpty())
//            }
//
//            override fun onError(e: Exception, requestCode: Int) {
//                _data.value = FeedModel(error = true)
//                _requestCode.value = requestCode
//            }
//
//        })


    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
//        var oldPosts = _data.value?.posts.orEmpty()
//        edited.value?.let {
//            repository.save(it, object : PostRepository.Callback<Post> {
//                override fun onSuccess(post: Post) {
//                    oldPosts = listOf(post)+oldPosts
//                    _data.postValue(FeedModel(posts = oldPosts))
//                    _postCreated.value = Unit
//                }
//
//                override fun onError(e: Exception, requestCode: Int) {
//                    _data.value = FeedModel(error = true)
//                    _requestCode.value = requestCode
//                }
//
//
//            })
//        }
//        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.likeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
//        var oldPosts = _data.value?.posts.orEmpty()
//        repository.likeById(id, object : PostRepository.Callback<Post> {
//            override fun onSuccess(post: Post) {
//                oldPosts = oldPosts.map {
//                    if (it.id != id) it else it.copy(
//                        likedByMe = post.likedByMe,
//                        likes = post.likes
//                    )
//                }
//                _data.value = FeedModel(posts = oldPosts)
//            }
//
//            override fun onError(e: Exception, requestCode: Int) {
//                _data.value = FeedModel(error = true)
//                _requestCode.value = requestCode
//            }
//
//        })
    }


    fun dislikeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.dislikeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState(idle = true)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
//        repository.removeById(id, object : PostRepository.RemCallback {
//            override fun onSuccess() {
//                _data.postValue(
//                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                        .filter { it.id != id }
//                    )
//                )
//            }
//            override fun onError(e: Exception, requestCode: Int) {
//                _data.value = FeedModel(error = true)
//                _requestCode.value = requestCode
//            }
//
//        })
//
//    }

    }
}