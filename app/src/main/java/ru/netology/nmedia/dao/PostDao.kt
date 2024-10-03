package ru.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverter
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.AttachmentType

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity where showOnList = 1 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>


    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>


    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT max(id) FROM PostEntity where isForInsert = 0")
    suspend fun getMaxIDForServer(): Int
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    @Query("update PostEntity set id = :id, localVersion = 0, isForInsert = 0 WHERE id = :localId")
    suspend fun updateByLocalId(localId: Int, id: Int)


    @Query("update PostEntity set blockForDelete = case when blockForDelete then 0 else 1 end WHERE id = :id")
    suspend fun blockForDeleteLocal(id: Int)

    @Query("SELECT * FROM PostEntity where localVersion = 1")
    suspend fun getLocalPosts(): List<PostEntity>

    @Query("SELECT * FROM PostEntity where isForInsert = 0")
    suspend fun getLocalPostsFromServer(): List<PostEntity>
    @Query("update PostEntity set showOnList = 1 where showOnList = 0")
    suspend fun setAllPostVisible()

    @Query(
        """
        update PostEntity set
        likes = likes + case when likedByMe then -1 else 1 end,
        likedByMe = case when likedByMe then 0 else 1 end
        where id = :id
    """
    )
    suspend fun likeById(id: Int)


    @Query("DELETE FROM PostEntity")
    suspend fun clear()
}

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}