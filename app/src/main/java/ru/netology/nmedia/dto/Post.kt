package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String = "",
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    var attachment: Attachment? = null,
    var savedOnServer: Boolean = false,
    val ownedByMe: Boolean = false
)