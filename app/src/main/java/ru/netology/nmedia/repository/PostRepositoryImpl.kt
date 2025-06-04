package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

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
            .let { it.body?.string() ?: throw  RuntimeException("body is null") }
            .let {
                gson.fromJson(it, typeToken.type)
            }
    }

    override fun getAllAsync(callback: PostRepository.PostsCallback<List<Post>>) {
        val request: Request =Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("Body is null")
                        callback.onSuccess(gson.fromJson(body, typeToken.type))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.PostsCallback<Post>) {
        val request:Request = Request.Builder()
            .post("".toRequestBody())
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("Body is null")
                        println(body)
                        callback.onSuccess(gson.fromJson(body, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

            })
    }

    override fun dislikeByIdAsync(id: Long, callback: PostRepository.PostsCallback<Post>) {
        val request:Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val body = response.body?.string() ?: throw RuntimeException("Body is null")
                        callback.onSuccess(gson.fromJson(body, Post::class.java))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

            })

//        return client.newCall(request)
//            .execute()
//         //   .close()
//            .let { it.body?.string() ?:throw RuntimeException("error") }
//            .let{
//                gson.fromJson(it, Post::class.java)
//            }
    }

    override fun removeByIdAsync(id: Long,callback: PostRepository.SaveAndRemovePostsCallback) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()
        client.newCall(request)
            .enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
            })

    }

    override fun saveAsync(post: Post, callback: PostRepository.SaveAndRemovePostsCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()
        client.newCall(request)
            .enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
                override fun onResponse(call: Call, response: Response) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

            })
//
//        client.newCall(request)
//            .execute()
//            .close()
    }


}