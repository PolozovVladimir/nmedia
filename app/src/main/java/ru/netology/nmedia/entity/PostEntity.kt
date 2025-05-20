package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post


@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val countOfShares: Int = 0,
    val countOfLikes: Int = 0,
    val videoLink: String? = ""
) {
    fun toDto() = Post(id, author, content, published, likedByMe, countOfShares, countOfLikes, videoLink)

    companion object{
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likedByMe,dto.countOfShares,
                dto.countOfLikes, dto.videoLink)
    }
}