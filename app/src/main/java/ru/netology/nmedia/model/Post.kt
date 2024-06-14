package ru.netology.nmedia.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.netology.nmedia.roomdb.PostEntity

@Parcelize
data class Post(
    val id: Int,
    val author: String,
    val authorAvatar: Int,
    val published: String,
    val content: String,
    val likeCount: Int = 0,
    val shareCount: Int = 0,
    val viewCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val video: String = ""
) : Parcelable {
    fun toEntity(): PostEntity = PostEntity(
        id,
        author,
        authorAvatar,
        published,
        content,
        likeCount,
        shareCount,
        viewCount,
        isLikedByMe,
        video
    )
}
