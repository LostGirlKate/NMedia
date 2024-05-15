package ru.netology.nmedia.model

data class Post(
    val id: Int,
    val author: String,
    val authorAvatar: Int,
    val published: String,
    val content: String,
    val likeCount: Int = 0,
    val shareCount: Int = 0,
    val viewCount: Int = 0,
    val isLikedByMe: Boolean = false
)
