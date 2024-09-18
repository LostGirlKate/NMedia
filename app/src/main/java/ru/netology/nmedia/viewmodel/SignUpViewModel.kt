package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.model.AuthViewState
import ru.netology.nmedia.model.MediaUpload
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import java.io.File
import javax.inject.Inject

private val noPhoto = PhotoModel()
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth,
) : ViewModel() {
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
                    authData.token?.let { appAuth.setAuth(authData.id, it) }
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