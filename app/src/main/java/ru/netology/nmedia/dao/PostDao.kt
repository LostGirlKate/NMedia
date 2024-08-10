package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    @Query("update PostEntity set id = :id, localVersion = 0 WHERE id = :localId")
    suspend fun updateByLocalId(localId: Int, id: Int)

    @Query("SELECT * FROM PostEntity where localVersion = 1")
    suspend fun getLocalPosts(): List<PostEntity>

    @Query(
        """
        update PostEntity set
        likes = likes + case when likedByMe then -1 else 1 end,
        likedByMe = case when likedByMe then 0 else 1 end
        where id = :id
    """
    )
    suspend fun likeById(id: Int)
}