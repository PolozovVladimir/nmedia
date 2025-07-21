package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppAuth(context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val idKey = "id"
    private val tokenKey = "token"

    private val _authStateFlow: MutableStateFlow<AuthState>

    init {
        val token = prefs.getString(tokenKey, null)
        val id = prefs.getLong(idKey, 0L)

        _authStateFlow = if (token == null || id == 0L) {
            MutableStateFlow(AuthState())
        } else {
            MutableStateFlow(AuthState(id, token))
        }
    }

    val authStateFlow = _authStateFlow.asStateFlow()

    @Synchronized
    fun setAuth(id: Long, token: String) {
        _authStateFlow.value = AuthState(id, token)
        prefs.edit {
            putString(tokenKey, token)
            putLong(idKey, id)
        }
    }

    @Synchronized
    fun removeAuth() {
        _authStateFlow.value = AuthState()
        prefs.edit {
            clear()
        }
    }
}

data class AuthState(val id: Long = 0, val token: String? = null)