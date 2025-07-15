package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.fido.fido2.api.common.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    var toShow:Boolean,
    val savedOnServer: Boolean,

    @Embedded
    val attachment: Attachment?,


    ) {
    fun toDto() = Post(id, author, authorId, authorAvatar, content, published, likedByMe, likes, toShow, attachment, savedOnServer)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author,dto.authorId, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes, dto.toShow,dto.savedOnServer, dto.attachment, )

    }
}
fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)