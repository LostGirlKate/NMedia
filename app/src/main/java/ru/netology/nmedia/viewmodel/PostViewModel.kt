package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.R
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    published = "",
    shareCount = 0,
    isLikedByMe = false,
    likeCount = 0,
    viewCount = 0,
    authorAvatar = R.drawable.ic_face
)

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    fun likeById(id: Int) = repository.likeById(id)
    fun shareById(id: Int) = repository.shareById(id)
    fun removeById(id: Int) = repository.removeById(id)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

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
        val text = video.trim()
        if (edited.value?.video == text) {
            return
        }
        edited.value = edited.value?.copy(video = text)
    }

    fun clearEdited() {
        edited.value = empty
    }

    class PostViewModelFactory(
        private val repository: PostRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PostViewModel(
                    repository
                ) as T
            }
            throw java.lang.IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}