package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.AttachmentType
import ru.netology.nmedia.model.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val localVersion: Boolean = false,
    val isForInsert: Boolean = false,
    val blockForDelete: Boolean = false,
    val showOnList: Boolean = true,
    @Embedded
    var attachment: AttachmentEmbeddable?,
) {
    fun toDto() = Post(
        id,
        author,
        authorAvatar,
        content,
        published,
        likedByMe,
        likes,
        localVersion = localVersion,
        isForInsert = isForInsert,
        blockForDelete = blockForDelete,
        showOnList = showOnList,
        attachment = attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.localVersion,
                dto.isForInsert,
                dto.blockForDelete,
                dto.showOnList,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )

    }
}


data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType,
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
