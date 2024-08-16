package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.model.Media
import ru.netology.nmedia.model.MediaUpload
import ru.netology.nmedia.model.Post

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll()
    fun getNewerCount(): Flow<Int>
    suspend fun getLocalPosts(): List<Post>
    suspend fun save(post: Post, localId: Int)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun saveLocal(post: Post): Int
    suspend fun removeById(id: Int)
    suspend fun removeByIdLocal(id: Int)
    suspend fun likeById(id: Int, isDelete: Boolean)
    suspend fun rollbackLikeByIdLocal(id: Int)
    suspend fun setAllPostsVisible()
    suspend fun upload(upload: MediaUpload): Media
}