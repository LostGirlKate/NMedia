package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.error.ErrorType
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private var empty = Post(
    id = 0,
    content = "",
    author = "Me",
    authorAvatar = "",
    published = "",
    likedByMe = false,
    likes = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val edited = MutableLiveData(empty)
    private var filterPostId = 0
    private val _postCreated = SingleLiveEvent<Unit>()
    private val _showErrorWindow = SingleLiveEvent<String>()
    private var lastEditedPost: Post? = null
    private var lastEditedId: Int? = null
    val postCreated: LiveData<Unit>
        get() = _postCreated

    val showErrorWindow: LiveData<String>
        get() = _showErrorWindow

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
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
                    val localId = repository.saveLocal(it)
                    lastEditedPost = it.copy(localID = localId)
                    edited.value = empty
                    repository.save(it, localId)
                    _dataState.value = FeedModelState()
                    lastEditedPost = null
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true, errorType = ErrorType.SAVE_ERROR)
                }
            }
        }
    }

    fun saveAfterError() {
        lastEditedPost?.let {
            viewModelScope.launch {
                try {
                    repository.save(it, it.localID)
                    _dataState.value = FeedModelState()
                    lastEditedPost = null
                } catch (e: Exception) {
                    lastEditedPost = it
                    _dataState.value = FeedModelState(error = true, errorType = ErrorType.SAVE_ERROR)
                }
            }
        }
    }

    fun removeByIdAfterError() = viewModelScope.launch {
        try {
            lastEditedId?.let { repository.removeById(it) }
            _dataState.value = FeedModelState()
            lastEditedId = null
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true,  errorType = ErrorType.DELETE_ERROR)
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
                _dataState.value = FeedModelState(error = true,  errorType = ErrorType.LIKE_ERROR)
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

    fun saveDraft(content: String, video: String) {
        empty = empty.copy(content = content)
    }

    fun clearDraft() {
        empty = empty.copy(content = "")
    }


    fun likeById(id: Int) {
        val isDelete = data.value!!.posts.firstOrNull { it.id == id }?.likedByMe
        viewModelScope.launch {
            try {
                isDelete?.let {
                    repository.likeById(id, it)
                }
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                lastEditedId = id
                lastEditedPost = data.value!!.posts.firstOrNull { it.id == id }
                _dataState.value = FeedModelState(error = true,  errorType = ErrorType.LIKE_ERROR)
            }
        }
    }

}