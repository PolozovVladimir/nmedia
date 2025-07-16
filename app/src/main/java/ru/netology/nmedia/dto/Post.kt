package ru.netology.nmedia.dto

enum class AttachmentType {
    IMAGE
}

data class Attachment(
    val url: String,
    val type: AttachmentType = AttachmentType.IMAGE
)

data class Post(
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String = "",
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    var toShow: Boolean,
    var attachment: Attachment? = null,
    var savedOnServer: Boolean = false,
    val ownedByMe: Boolean = false
)