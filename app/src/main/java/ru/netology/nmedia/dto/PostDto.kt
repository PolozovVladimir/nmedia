package ru.netology.nmedia.dto

data class PostDto(
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val shares: Int,
    val views: Int,
    val videoUrl: String? = null
) {
    companion object {
        fun empty() = PostDto(
            id = 0,
            author = "",
            authorAvatar = "",
            content = "",
            published = "",
            likedByMe = false,
            likes = 0,
            shares = 0,
            views = 0,
            videoUrl = null
        )
    }
}