package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    val data: Flow<PagingData<Post>> = repository.data
        .cachedIn(viewModelScope)
        .map { pagingData ->
            pagingData.map { it.copy(ownedByMe = true) }
        }

    private var photo: PhotoModel? = null

    fun save(content: String) {
        viewModelScope.launch {
            val post = Post(
                id = 0,
                content = content,
                author = "Me",
                authorId = 1,
                authorAvatar = "",
                likedByMe = false,
                likes = 0,
                published = "now",
                toShow = true,
                savedOnServer = true,
                attachment = photo?.uri?.toString()?.let {
                    ru.netology.nmedia.dto.Attachment(it, ru.netology.nmedia.dto.AttachmentType.IMAGE)
                }
            )
            repository.save(post)
            clearPhoto()
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            repository.likeById(id)
        }
    }

    fun dislikeById(id: Long) {
        viewModelScope.launch {
            repository.dislikeById(id)
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            repository.removeById(id)
        }
    }

    fun setPhoto(uri: Uri?, file: File?) {
        photo = if (uri != null && file != null) {
            PhotoModel(uri, file)
        } else {
            null
        }
    }

    fun clearPhoto() {
        photo = null
    }

    fun getPhoto(): PhotoModel? = photo
}