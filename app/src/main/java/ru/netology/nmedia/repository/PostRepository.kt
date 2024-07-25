package ru.netology.nmedia.repository

import ru.netology.nmedia.model.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Int, isDelete: Boolean): Post
    fun shareById(id: Int)
    fun removeById(id: Int)
    fun save(post: Post)

    fun getAllAsync(callback: GetAllCallback)
    fun likeByIdAsync(id: Int, isDelete: Boolean, callback: LikeByIdCallback)


    fun removeByIdAsync(id: Int, callback: BaseAsyncCallback)
    fun saveAsync(post: Post, callback: BaseAsyncCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    interface LikeByIdCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    interface BaseAsyncCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }
}