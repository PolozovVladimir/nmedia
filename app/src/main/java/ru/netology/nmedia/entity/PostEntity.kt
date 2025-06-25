package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.PostDto


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
    //val attachment: Attachment?,
    val savedOnServer: Boolean

) {
    fun toDto() = PostDto(id, author, authorAvatar, content, published, likedByMe, likes, savedOnServer)

    companion object {
        fun fromDto(dto: PostDto) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes,  dto.savedOnServer)

    }
}
fun List<PostEntity>.toDto(): List<PostDto> = map(PostEntity::toDto)
fun List<PostDto>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)