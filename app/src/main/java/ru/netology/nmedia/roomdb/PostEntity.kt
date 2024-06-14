package ru.netology.nmedia.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.model.Post

@Entity(tableName = "post")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "author_avatar")
    val authorAvatar: Int,
    @ColumnInfo(name = "published")
    val published: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "like_count")
    val likeCount: Int = 0,
    @ColumnInfo(name = "share_count")
    val shareCount: Int = 0,
    @ColumnInfo(name = "view_count")
    val viewCount: Int = 0,
    @ColumnInfo(name = "is_liked_by_me")
    val isLikedByMe: Boolean = false,
    @ColumnInfo(name = "video")
    val video: String = ""
) {
    fun toPost(): Post = Post(
        id,
        author,
        authorAvatar,
        published,
        content,
        likeCount,
        shareCount,
        viewCount,
        isLikedByMe,
        video
    )
}
