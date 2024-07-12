package ru.netology.nmedia.repository

import ru.netology.nmedia.model.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Int, isDelete: Boolean): Post
    fun shareById(id: Int)
    fun removeById(id: Int)

    fun save(post: Post)
}