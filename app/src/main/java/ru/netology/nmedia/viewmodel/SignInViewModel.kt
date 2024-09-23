package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.AuthViewState
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
) : ViewModel() {

    private val _dataState = MutableLiveData<AuthViewState>()
    val dataState: LiveData<AuthViewState>
        get() = _dataState

    fun auth(login: String, password: String) {
        viewModelScope.launch {
            try {
                _dataState.value = AuthViewState(loading = true)
                val authData = repository.authentication(login, password)
                authData.token?.let { appAuth.setAuth(authData.id, it) }
                _dataState.value = AuthViewState()
            } catch (e: Exception) {
                _dataState.value = AuthViewState(error = true)
            }
        }

    }
}