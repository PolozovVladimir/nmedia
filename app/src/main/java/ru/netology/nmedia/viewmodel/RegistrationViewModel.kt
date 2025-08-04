package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _tokenReceived = SingleLiveEvent<Int>()
    val tokenReceived: LiveData<Int>
        get() = _tokenReceived

    fun registerUser(login: String, pass: String, name: String) {
        viewModelScope.launch {
            try {
                repository.registerUser(login, pass, name)
                _tokenReceived.value = 0
            } catch (e: Exception) {
                _tokenReceived.value = -1
            }
        }
    }
}