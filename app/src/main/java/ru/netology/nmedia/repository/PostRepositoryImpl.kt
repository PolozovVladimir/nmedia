package ru.netology.nmedia.repository

import android.content.Context
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostApiServiceHolder
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl : PostRepository {

    override fun getAllAsync(callback: PostRepository.Callback<List<Post>>, context: Context) {
        PostApiServiceHolder.service.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (!response.isSuccessful) {
                        handleErrorResponse(response.code(), context)
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    private fun handleErrorResponse(code: Int, context: Context) {
        when {
            code.toString().startsWith("1") ->
                Toast.makeText(context, "Информационный код ответа", Toast.LENGTH_SHORT).show()
            code.toString().startsWith("3") ->
                Toast.makeText(context, "Перенаправление", Toast.LENGTH_SHORT).show()
            code.toString().startsWith("4") ->
                Toast.makeText(context, "Ошибка клиента", Toast.LENGTH_SHORT).show()
            code.toString().startsWith("5") ->
                Toast.makeText(context, "Ошибка сервера", Toast.LENGTH_SHORT).show()
        }
    }

    override fun save(post: Post, callback: PostRepository.Callback<Post>) {
        PostApiServiceHolder.service.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    override fun likeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostApiServiceHolder.service.likeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    override fun dislikeById(id: Long, callback: PostRepository.Callback<Post>) {
        PostApiServiceHolder.service.dislikeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.RemCallback) {
        PostApiServiceHolder.service.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (!response.isSuccessful) {
                        callback.onError(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(RuntimeException(t))
                }
            })
    }
}