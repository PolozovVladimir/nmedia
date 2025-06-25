package ru.netology.nmedia.dto

import com.google.gson.annotations.SerializedName
import ru.netology.nmedia.util.DateUtils

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    @SerializedName("published")
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String? = null,

    @Transient val savedOnServer: Boolean = false
) {

    fun copyWithLikes(byMe: Boolean, count: Int) = copy(
        likedByMe = byMe,
        likes = count
    )


    fun toDto() = Post(
        id = id,
        author = author,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        shares = shares,
        views = views,
        video = video
    )

    companion object {

        fun empty() = Post(
            id = 0,
            author = "",
            content = "",
            published = DateUtils.now(),
            savedOnServer = false
        )
    }
}