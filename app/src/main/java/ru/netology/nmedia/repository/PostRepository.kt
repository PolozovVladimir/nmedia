package ru.netology.nmedia.repository



import ru.netology.nmedia.dto.Post


interface PostRepository {
    fun getAll(): List<Post>
    fun likeByIdAsync(id:Long, callback: PostsCallback<Post>)
    fun dislikeByIdAsync(id:Long, callback: PostsCallback<Post>)
    fun removeByIdAsync(id: Long, callback: SaveAndRemovePostsCallback)
    fun saveAsync(post: Post,callback: SaveAndRemovePostsCallback)

    fun getAllAsync(callback: PostsCallback<List<Post>>)


    interface PostsCallback<T> {
        fun onSuccess(posts: T)
        fun onError(e: Exception)
    }

    interface SaveAndRemovePostsCallback{
        fun onSuccess()
        fun onError(e: Exception)
    }

}