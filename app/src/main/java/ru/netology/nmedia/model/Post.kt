package ru.netology.nmedia.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Int,
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
    val showOnList: Boolean = true
) : Parcelable

