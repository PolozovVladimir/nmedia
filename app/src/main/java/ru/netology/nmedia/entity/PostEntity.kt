package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val savedOnServer: Boolean
) {
    fun toDto(): Post = Post(
        id = id,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        likedByMe = likedByMe,
        likes = likes,
        savedOnServer = savedOnServer
    )

    companion object {
        fun fromDto(dto: Post): PostEntity = PostEntity(
            id = dto.id,
            author = dto.author,
            authorAvatar = dto.authorAvatar,
            content = dto.content,
            published = dto.published,
            likedByMe = dto.likedByMe,
            likes = dto.likes,
            savedOnServer = dto.savedOnServer
        )
    }
}

fun List<PostEntity>.toDto(): List<Post> = map { it.toDto() }
fun List<Post>.toEntity(): List<PostEntity> = map { PostEntity.fromDto(it) }