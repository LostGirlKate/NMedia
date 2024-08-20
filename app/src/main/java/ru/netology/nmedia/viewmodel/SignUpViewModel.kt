package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.AuthViewState
import ru.netology.nmedia.model.MediaUpload
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import java.io.File

private val noPhoto = PhotoModel()

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )
    private val _dataState = MutableLiveData<AuthViewState>()
    val dataState: LiveData<AuthViewState>
        get() = _dataState

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun registry(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _dataState.value = AuthViewState(loading = true)
                val authData = when (_photo.value) {
                    noPhoto -> repository.registerUser(login, password, name)
                    else -> _photo.value?.file?.let { file ->
                        repository.registerUserWithPhoto(login, password, name, MediaUpload(file))
                    }
                }
                _photo.value = noPhoto
                if (authData != null) {
                    authData.token?.let { AppAuth.getInstance().setAuth(authData.id, it) }
                    _dataState.value = AuthViewState()
                } else {
                    _dataState.value = AuthViewState(error = true)
                }
            } catch (e: Exception) {
                _dataState.value = AuthViewState(error = true)
            }
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }


}