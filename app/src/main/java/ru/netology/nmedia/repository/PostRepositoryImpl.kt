package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.AttachmentType
import ru.netology.nmedia.model.Media
import ru.netology.nmedia.model.MediaUpload
import ru.netology.nmedia.model.Post
import java.io.IOException


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data = dao.getAll()
        .map(List<PostEntity>::toDto)
        .flowOn(Dispatchers.Default)


    override suspend fun getAll() {
        try {
            val response = PostsApi.service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity().map { it.copy(showOnList = true) })
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun getNewerCount(): Flow<Int> = flow {
        while (true) {
            delay(10_000L)
            val id = dao.getMaxIDForServer()
            val response = PostsApi.service.getNewer(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            val localPosts = dao.getLocalPostsFromServer()
            val postsForInsert =
                body.toEntity().filter { localPosts.none { localPost -> localPost.id == it.id } }
                    .map { it.copy(showOnList = false) }
            dao.insert(postsForInsert)
            emit(postsForInsert.size)
        }
    }
        .catch { e -> throw AppError.from(e) }
        .flowOn(Dispatchers.Default)

    override suspend fun getLocalPosts(): List<Post> {
        return dao.getLocalPosts().map { postEntity -> postEntity.toDto() }
    }


    override suspend fun likeById(
        id: Int,
        isDelete: Boolean,
    ) {
        try {
            dao.likeById(id)
            val response =
                if (isDelete) PostsApi.service.dislikeById(id) else PostsApi.service.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun rollbackLikeByIdLocal(id: Int) {
        dao.likeById(id)
    }

    override suspend fun setAllPostsVisible() {
        dao.setAllPostVisible()
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )

            val response = PostsApi.service.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun authentication(login: String, password: String): AuthState {
        try {
            val response = PostsApi.service.authentication(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

    }

    override suspend fun registerUserWithPhoto(
        login: String,
        pass: String,
        name: String,
        upload: MediaUpload,
    ): AuthState {
        try {
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )
            val response = PostsApi.service.registerWithPhoto(
                login.toRequestBody("text/plain".toMediaType()),
                pass.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                media
            )
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerUser(login: String, pass: String, name: String): AuthState {
        try {
            val response = PostsApi.service.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Int) {
        try {
            dao.removeById(id)
            val response = PostsApi.service.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeByIdLocal(id: Int) {
        dao.removeById(id)
    }


    override suspend fun save(post: Post, localId: Int) {
        try {
            dao.blockForDeleteLocal(localId)
            val response = PostsApi.service.save(post)
            if (!response.isSuccessful) {
                dao.blockForDeleteLocal(localId)
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.updateByLocalId(localId, PostEntity.fromDto(body).id)
            dao.insert(PostEntity.fromDto(body))
            dao.blockForDeleteLocal(localId)
        } catch (e: IOException) {
            dao.blockForDeleteLocal(localId)
            throw NetworkError
        } catch (e: Exception) {
            dao.blockForDeleteLocal(localId)
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            // TODO: add support for other types
            val postWithAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWithAttachment, postWithAttachment.id)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveLocal(post: Post): Int {
        val postVersion =
            if (post.id == 0) post.copy(localVersion = true, isForInsert = true) else post.copy(
                localVersion = true
            )
        return dao.insert(PostEntity.fromDto(postVersion)).toInt()
    }
}