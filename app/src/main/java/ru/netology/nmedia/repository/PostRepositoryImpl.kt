package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.appError.ApiError
import ru.netology.nmedia.appError.AppError
import ru.netology.nmedia.appError.NetworkError
import ru.netology.nmedia.appError.UnknownError
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.entity.PostEntity
import java.io.IOException
import javax.inject.Inject
import kotlin.random.Random

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService,
    private val appAuth: AppAuth,
    private val appDb: AppDb
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data:Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = PostRemoteMediator(apiService, appDb),
        pagingSourceFactory = { postDao.getPagingSource() }
    ).flow
        .map { pagingData ->
            pagingData.map(PostEntity::toDto)
                .insertSeparators { previos, _ ->
                   if (previos?.id?.rem(5) == 0L){
Ad (Random.nextLong(), "figma.jpeg")
                    } else {
                        null
                }
                }
        }

    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            postDao.insert(PostEntity.fromDto(body).copy(
                savedOnServer = true
            ))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = uploadFile(upload)
            val url = "${BuildConfig.BASE_URL}media/${media.id}"

            val postWithAttachment = post.copy(
                attachment = Attachment(url, AttachmentType.IMAGE),
                savedOnServer = true
            )
            save(postWithAttachment)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    private suspend fun uploadFile(upload: MediaUpload): Media {
        try {
            val part = MultipartBody.Part.createFormData(
                "file",
                upload.file.name,
                upload.file.asRequestBody()
            )
            val response = apiService.uploadPic(part)
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        postDao.likeById(id)
        try {
            val response = apiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        postDao.likeById(id)
        try {
            val response = apiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())

            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        postDao.removeById(id)
        try {
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun clear() {
        postDao.clear()
    }

    override suspend fun updateUser(login: String, pass: String) {
        try {
            val response = apiService.updateUser(login, pass)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            appAuth.setAuth(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerUser(login: String, pass: String, name: String) {
        try {
            val response = apiService.registerUser(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            appAuth.setAuth(body.id, body.token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}