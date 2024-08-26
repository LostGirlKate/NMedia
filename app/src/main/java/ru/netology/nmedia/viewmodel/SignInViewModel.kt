package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.AuthViewState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )
    private val _dataState = MutableLiveData<AuthViewState>()
    val dataState: LiveData<AuthViewState>
        get() = _dataState

    fun auth(login: String, password: String) {
        viewModelScope.launch {
            try {
                _dataState.value = AuthViewState(loading = true)
                val authData = repository.authentication(login, password)
                authData.token?.let { AppAuth.getInstance().setAuth(authData.id, it) }
                _dataState.value = AuthViewState()
            } catch (e: Exception) {
                _dataState.value = AuthViewState(error = true)
            }
        }

    }
}