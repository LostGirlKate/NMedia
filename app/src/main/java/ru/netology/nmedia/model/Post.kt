package ru.netology.nmedia.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface FeedItem {
    val id: Int
}

@Parcelize
data class Post(
    override val id: Int,
    val authorId: Long,
    val author: String,
    var authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val attachment: Attachment? = null,
    val localVersion: Boolean = false,
    val localID: Int = 0,
    val isForInsert: Boolean = false,
    val blockForDelete: Boolean = false,
    val showOnList: Boolean = true,
    val ownedByMe: Boolean = false,
) : Parcelable, FeedItem

data class Ad(
    override val id: Int,
    val image: String,
) : FeedItem

data class TimeSeparator(
    override val id: Int,
    val title: String,
) : FeedItem

