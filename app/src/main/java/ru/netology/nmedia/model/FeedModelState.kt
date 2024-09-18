package ru.netology.nmedia.model

import ru.netology.nmedia.error.ErrorType

data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val errorType: ErrorType? = null,
    val refreshing: Boolean = false,
)
