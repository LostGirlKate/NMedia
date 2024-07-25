package ru.netology.nmedia.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachment(
    var url: String,
    var description: String?,
    var type: AttachmentType
): Parcelable
