package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.model.Post
import java.io.IOException
import java.util.concurrent.TimeUnit


class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}
    private val typePostToken = object : TypeToken<Post>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun likeById(id: Int, isDelete: Boolean): Post {
        val request: Request = if (isDelete)
            Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        else Request.Builder()
            .post("".toRequestBody())
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body.string() ?: throw RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typePostToken.type)
            }
    }

    override fun likeByIdAsync(
        id: Int,
        isDelete: Boolean,
        callback: PostRepository.LikeByIdCallback,
    ) {
        val request: Request = if (isDelete)
            Request.Builder()
                .delete()
                .url("${BASE_URL}/api/posts/$id/likes")
                .build()
        else Request.Builder()
            .post("".toRequestBody())
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val body = response.body.string() ?: throw RuntimeException("body is null")
                    try {
                        callback.onSuccess(gson.fromJson(body, typePostToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun removeById(id: Int) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeByIdAsync(id: Int, callback: PostRepository.BaseAsyncCallback) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) callback.onError(IOException("Unexpected code $response"))
                        callback.onSuccess()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }


    override fun shareById(id: Int) {
        TODO("Not yet implemented")
    }


    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun saveAsync(post: Post, callback: PostRepository.BaseAsyncCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()


        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) callback.onError(IOException("Unexpected code $response"))
                        callback.onSuccess()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }
}