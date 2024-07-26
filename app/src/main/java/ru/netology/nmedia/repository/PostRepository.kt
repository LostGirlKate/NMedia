package ru.netology.nmedia.repository

import ru.netology.nmedia.model.Post

interface PostRepository {
    fun getAllAsync(callback: Callback<List<Post>>)
    fun likeByIdAsync(id: Int, isDelete: Boolean, callback: Callback<Post>)
    fun removeByIdAsync(id: Int, callback: Callback<Unit>)
    fun saveAsync(post: Post, callback: Callback<Post>)

    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }
}