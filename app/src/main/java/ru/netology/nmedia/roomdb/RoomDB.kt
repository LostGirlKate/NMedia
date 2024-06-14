package ru.netology.nmedia.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PostEntity::class], version = 1)
abstract class RoomDB : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var instance: RoomDB? = null
        fun getInstance(context: Context): RoomDB {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, RoomDB::class.java, "app.db")
                .allowMainThreadQueries()
                .build()
    }
}