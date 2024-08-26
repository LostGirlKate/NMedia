package ru.netology.nmedia.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    val url: String,
    val type: AttachmentType,
): Parcelable
