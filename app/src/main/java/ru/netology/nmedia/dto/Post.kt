package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String = "",
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    //var attachment: Attachment? = null,
    var savedOnServer: Boolean = false
) {
    companion object {
        fun empty() = Post(
            id = 0,
            author = "",
            authorAvatar = "",
            content = "",
            published = "",
            likedByMe = false,
            likes = 0,
            savedOnServer = false
        )
    }

    data class Attachment(
        val url: String,
        val description: String?,
        val type: AttachmentType,
    )

    enum class AttachmentType {
        IMAGE

    }

}
