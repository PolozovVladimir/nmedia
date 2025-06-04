package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okio.IOException
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.concurrent.thread


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.PostsCallback<List<Post>> {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.SaveAndRemovePostsCallback {
                override fun onSuccess() {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }

            })
        }

//        edited.value?.let {
//            thread {
//                repository.save(it)
//                _postCreated.postValue(Unit)
//            }
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
        var oldPosts = _data.value?.posts.orEmpty()
        repository.likeByIdAsync(id, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(post: Post) {
                oldPosts = oldPosts.map {
                    if (it.id != id) it else it.copy(
                        likedByMe = post.likedByMe,
                        likes = post.likes
                    )
                }
                _data.postValue(FeedModel(posts = oldPosts))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }

        })
//            val updatedPost = repository.likeByIdAsync(id)
//            var posts = _data.value?.posts.orEmpty()
//
//            posts = posts.map{
//                if (it.id != id) it else it.copy(
//                    likedByMe = updatedPost.likedByMe,
//                    likes = updatedPost.likes)
//            }
//            _data.postValue(FeedModel(posts = posts))
    }


    fun dislikeById(id: Long) {
        var oldPosts = _data.value?.posts.orEmpty()
        repository.dislikeByIdAsync(id, object : PostRepository.PostsCallback<Post> {
            override fun onSuccess(post: Post) {
                oldPosts = oldPosts.map {
                    if (it.id != id) it else it.copy(
                        likedByMe = post.likedByMe,
                        likes = post.likes
                    )
                }
                _data.postValue(FeedModel(posts = oldPosts))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }

        })
    }

    fun refresh() {
        loadPosts()
    }

    fun removeById(id: Long) {

        repository.removeByIdAsync(id, object : PostRepository.SaveAndRemovePostsCallback {
            override fun onSuccess() {
                _data.postValue(
                    _data.value?.copy(posts = _data.value?.posts.orEmpty()
                        .filter { it.id != id }
                    )
                )
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }

        })

    }

}