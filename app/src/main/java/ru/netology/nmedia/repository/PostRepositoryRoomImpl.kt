package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.roomdb.PostDao

class PostRepositoryRoomImpl(
    private val dao: PostDao
) : PostRepository {
    override fun getAll() = dao.getAll().map { list ->
        list.map {
            it.toPost()
        }
    }

    override fun likeById(id: Int) {
        dao.likeById(id)
    }

    override fun shareById(id: Int) {
        dao.shareById(id)
    }

    override fun removeById(id: Int) {
        dao.removeById(id)
    }

    override fun save(post: Post) {
        dao.save(post.toEntity())
    }
}