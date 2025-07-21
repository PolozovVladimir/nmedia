package ru.netology.nmedia.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token

interface ApiService {
    @POST("api/users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken): Response<Unit>

    @GET("api/posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("latest")
    suspend fun getLatest(): Response<List<Post>>

    @GET("api/posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @GET("api/posts/{id}/before")
    suspend fun getBefore(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfter(@Path("id") id: Long, @Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("api/posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("api/media")
    suspend fun uploadPic(@Part media: MultipartBody.Part): Response<Media>

    @FormUrlEncoded
    @POST("api/users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<Token>

    @FormUrlEncoded
    @POST("api/users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<Token>
}