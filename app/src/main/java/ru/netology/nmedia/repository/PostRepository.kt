package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.model.FeedItem
import ru.netology.nmedia.model.Media
import ru.netology.nmedia.model.MediaUpload
import ru.netology.nmedia.model.Post

interface PostRepository {
    val data: Flow<PagingData<FeedItem>>
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
    suspend fun authentication(login: String, password: String): AuthState
    suspend fun registerUser(login: String, pass: String, name: String): AuthState
    suspend fun registerUserWithPhoto(login: String, pass: String, name: String, upload: MediaUpload): AuthState

}