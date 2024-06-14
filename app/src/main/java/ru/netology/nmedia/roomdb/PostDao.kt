package ru.netology.nmedia.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostDao {
    @Query("Select * from post order by id desc")
    fun getAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)

    @Query("update post set content = :content, video = :video where id = :id")
    fun updatePost(id: Int, content: String, video: String)
    fun save(post: PostEntity) =
        if (post.id == 0) insert(post) else updatePost(post.id, post.content, post.video)

    @Query(
        """
        update post set
        like_count = like_count + case when is_liked_by_me then -1 else 1 end,
        is_liked_by_me = case when is_liked_by_me then 0 else 1 end
        where id = :id
    """
    )
    fun likeById(id: Int)

    @Query(
        """
        update post set
        share_count = share_count + 1
        where id = :id
    """
    )
    fun shareById(id: Int)

    @Query("delete from post where id = :id")
    fun removeById(id: Int)

}