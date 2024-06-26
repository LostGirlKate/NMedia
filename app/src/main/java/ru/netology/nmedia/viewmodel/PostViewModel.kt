package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl
import ru.netology.nmedia.roomdb.RoomDB

private var empty = Post(
    id = 0,
    content = "",
    author = "Me",
    published = "now",
    shareCount = 0,
    isLikedByMe = false,
    likeCount = 0,
    viewCount = 0,
    authorAvatar = R.drawable.ic_face
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryRoomImpl(
        RoomDB.getInstance(application).postDao()
    )
    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    private var filterPostId = 0

    fun likeById(id: Int) = repository.likeById(id)
    fun shareById(id: Int) = repository.shareById(id)
    fun removeById(id: Int) = repository.removeById(id)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
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
        val text = video.trim()
        if (edited.value?.video == text) {
            return
        }
        edited.value = edited.value?.copy(video = text)
    }

    fun clearEdited() {
        edited.value = empty
    }

    fun saveDraft(content: String, video: String) {
        empty = empty.copy(content = content, video = video)
    }

    fun clearDraft() {
        empty = empty.copy(content = "", video = "")
    }

}