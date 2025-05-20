package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl


val empty = Post(
    0,
    "",
    "",
    "",
    false,
    0,
    0,
    null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )
    val data = repository.getAll()
    fun likeById(id: Long) = repository.likeById(id)
    fun sharedById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)

    private val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content:String, videoLink:String?) {
        val text = content?.trim()
        val videoLink = videoLink?.trim()
        if (edited.value?.content == text && edited.value?.videoLink == videoLink?.trim()) {
            return
        }
        edited.value = edited.value?.copy(
            content = content,
            videoLink = videoLink
        )
    }

}
