package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import kotlin.concurrent.thread

private var empty = Post(
    id = 0,
    content = "",
    author = "Me",
    published = 0,
    likedByMe = false,
    likes = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private var filterPostId = 0
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }


    fun removeById(id: Int) {
        thread {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            repository.removeByIdAsync(id, object : PostRepository.BaseAsyncCallback {
                override fun onSuccess() {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(_data.value?.copy(posts = old))
                }
            })
        }
    }


    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.BaseAsyncCallback {
                override fun onSuccess() {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _postCreated.postValue(Unit)
                    _data.postValue(FeedModel(error = true))
                }
            })
        }
        edited.postValue(empty)
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
        val isDelete = _data.value!!.posts.firstOrNull { it.id == id }?.likedByMe
        isDelete?.let {
            repository.likeByIdAsync(id, it,
                object : PostRepository.LikeByIdCallback {
                    override fun onSuccess(post: Post) {
                        _data.postValue(
                            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                                .map { dataPost -> if (dataPost.id == id) post else dataPost }
                            )
                        )
                    }

                    override fun onError(e: Exception) {
                        _data.postValue(FeedModel(error = true))
                    }
                }
            )
        }
    }

    fun shareById(id: Int) = repository.shareById(id)

}