package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dto.PushToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext
    private val context: Context,
) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0L)

        if (token == null || id == 0L) {
            _authStateFlow = MutableStateFlow(AuthState())
            with(prefs.edit()) {
                clear()
                apply()
            }
        } else {
            _authStateFlow = MutableStateFlow(AuthState(id, token))
        }
    }

    val authStateFlow = _authStateFlow.asStateFlow()

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        prefs.edit {
            remove(tokenKey)
            remove(idKey)
        }
        sendPushToken()
    }

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        prefs.edit {
            putString(tokenKey, token)
            putLong(idKey, id)
        }
        sendPushToken()
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val pushToken = PushToken(token ?: FirebaseMessaging.getInstance().token.await())
                val entryPoint = EntryPointAccessors.fromApplication(context, AppAuthEntryPoint::class.java)
                entryPoint.getApiService().sendPushToken(pushToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun getApiService(): ApiService
    }

    fun getId(): Long = prefs.getLong(idKey, 0L)
}

data class AuthState(val id: Long = 0, val token: String? = null)