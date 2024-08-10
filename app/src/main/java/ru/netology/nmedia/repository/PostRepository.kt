package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.model.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun getLocalPosts(): List<Post>
    suspend fun save(post: Post, localId: Int)
    suspend fun saveLocal(post: Post): Int
    suspend fun removeById(id: Int)
    suspend fun likeById(id: Int, isDelete: Boolean)
    suspend fun rollbackLikeByIdLocal(id: Int)
}