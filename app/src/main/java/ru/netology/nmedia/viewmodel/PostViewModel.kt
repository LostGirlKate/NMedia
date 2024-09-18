package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.ErrorType
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.MediaUpload
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
import javax.inject.Inject

private var empty = Post(
    id = 0,
    content = "",
    authorId = 0,
    author = "Me",
    authorAvatar = "",
    published = "",
    likedByMe = false,
    likes = 0
)

private val noPhoto = PhotoModel()
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val appAuth: AppAuth
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: LiveData<FeedModel> = appAuth
        .authStateFlow
        .flatMapLatest { (myId, _) ->
            repository.data
                .map { posts ->
                    FeedModel(
                        posts.map { it.copy(ownedByMe = it.authorId == myId) },
                        posts.isEmpty()
                    )
                }
        }.asLiveData(Dispatchers.Default)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val newerCount: LiveData<Int> = data.switchMap {
        repository.getNewerCount()
            .catch { e -> e.printStackTrace() }
            .asLiveData(Dispatchers.Default)
    }

    val edited = MutableLiveData(empty)
    private var filterPostId = 0
    private val _postCreated = SingleLiveEvent<Unit>()
    private val _showErrorWindow = SingleLiveEvent<String>()
    private val _showSignInDialog = SingleLiveEvent<Boolean>()
    private var lastEditedPost: Post? = null
    private var lastEditedId: Int? = null
    val postCreated: LiveData<Unit>
        get() = _postCreated

    val showErrorWindow: LiveData<String>
        get() = _showErrorWindow

    val showSignInDialog: LiveData<Boolean>
        get() = _showSignInDialog

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    init {
        loadPosts()
    }

    fun checkSignIn(): Boolean {
        val checkResult = appAuth.authStateFlow.value.id != 0L
        _showSignInDialog.value = !checkResult
        return checkResult
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            sendAllLocalPosts()
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            sendAllLocalPosts()
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun removeById(id: Int) = viewModelScope.launch {
        try {
            repository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            lastEditedId = id
            _dataState.value = FeedModelState(error = true, errorType = ErrorType.DELETE_ERROR)
        }
    }


    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.saveLocal(it)
                    edited.value = empty
                    when (_photo.value) {
                        noPhoto -> sendAllLocalPosts()
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }


                    _photo.value = noPhoto
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value =
                        FeedModelState(error = true, errorType = ErrorType.SAVE_ERROR)
                }
            }
        }
    }

    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun saveAfterError() {
        viewModelScope.launch {
            try {
                sendAllLocalPosts()
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value =
                    FeedModelState(error = true, errorType = ErrorType.SAVE_ERROR)
            }
        }
    }

    private fun sendLocalPost(post: Post) {
        post.let {
            viewModelScope.launch {
                try {
                    repository.save(if (it.isForInsert) it.copy(id = 0) else it, it.id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value =
                        FeedModelState(error = true, errorType = ErrorType.SAVE_ERROR)
                }
            }
        }
    }

    fun sendAllLocalPosts() {
        viewModelScope.launch {
            val localPosts = repository.getLocalPosts()
            localPosts.forEach {
                async { sendLocalPost(it) }
            }
        }
    }

    fun deleteLocal(post: Post) {
        viewModelScope.launch {
            repository.removeByIdLocal(post.id)
        }
    }

    fun setAllPostsVisible() {
        viewModelScope.launch {
            repository.setAllPostsVisible()
        }
    }

    fun removeByIdAfterError() = viewModelScope.launch {
        try {
            lastEditedId?.let { repository.removeById(it) }
            _dataState.value = FeedModelState()
            lastEditedId = null
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true, errorType = ErrorType.DELETE_ERROR)
        }
    }

    fun likeByIdAfterError() {
        val isDelete = lastEditedPost?.likedByMe
        viewModelScope.launch {
            try {
                isDelete?.let {
                    lastEditedId?.let { it1 -> repository.likeById(it1, it) }
                }
                _dataState.value = FeedModelState()
                lastEditedId = null
                lastEditedPost = null
            } catch (e: Exception) {
                lastEditedId?.let { repository.rollbackLikeByIdLocal(it) }
                _dataState.value = FeedModelState(error = true, errorType = ErrorType.LIKE_ERROR)
            }
        }
    }


    fun viewPost(post: Post) {
        filterPostId = post.id
    }

    fun getFilterPostID(): Int = filterPostId

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun changeVideo(video: String) {
//        val text = video.trim()
//        if (edited.value?.video == text) {
//            return
//        }
//        edited.value = edited.value?.copy(video = text)
    }

    fun clearEdited() {
        edited.value = empty
    }

    fun saveDraft(content: String) {
        empty = empty.copy(content = content)
    }

    fun clearDraft() {
        empty = empty.copy(content = "")
    }


    fun likeById(id: Int) {
        checkSignIn()
        val isDelete = data.value!!.posts.firstOrNull { it.id == id }?.likedByMe
        viewModelScope.launch {
            try {
                isDelete?.let {
                    repository.likeById(id, it)
                }
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                repository.rollbackLikeByIdLocal(id)
                lastEditedId = id
                lastEditedPost = data.value!!.posts.firstOrNull { it.id == id }
                _dataState.value = FeedModelState(error = true, errorType = ErrorType.LIKE_ERROR)
            }
        }
    }

}