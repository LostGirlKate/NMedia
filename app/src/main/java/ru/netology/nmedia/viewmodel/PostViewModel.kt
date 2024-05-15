package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.netology.nmedia.repository.PostRepository

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    val data = repository.get()
    fun like() = repository.like()
    fun share() = repository.share()

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