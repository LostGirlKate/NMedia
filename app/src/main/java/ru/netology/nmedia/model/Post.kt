package ru.netology.nmedia.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: Int,
    val author: String,
    var authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val attachment: Attachment? = null
) : Parcelable
