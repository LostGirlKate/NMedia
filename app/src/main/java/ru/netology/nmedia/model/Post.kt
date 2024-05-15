package ru.netology.nmedia.model

data class Post(
    val id: Int,
    val author: String,
    val authorAvatar: Int,
    val published: String,
    val content: String,
    var likeCount: Int = 0,
    var shareCount: Int = 0,
    var viewCount: Int = 0,
    var isLikedByMe: Boolean = false
)
