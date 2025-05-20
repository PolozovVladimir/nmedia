package ru.netology.nmedia.dto


data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val countOfShares: Int = 0,
    val countOfLikes: Int = 0,
    val videoLink: String?=""

)