package ru.netology.nmedia.repository



import android.content.Context
import ru.netology.nmedia.dto.Post


interface PostRepository {
    fun getAllAsync(callback: Callback<List<Post>>, context: Context)
    fun save(post: Post, callback: Callback<Post>)
    fun removeById(id: Long, callback: RemCallback)
    fun likeById(id: Long, callback: Callback<Post>)
    fun dislikeById(id: Long,callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }

    interface RemCallback{
        fun onSuccess()
        fun onError(e:Exception){}
    }
}