package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CounterDao
import com.example.data.dao.ItemDao
import com.example.data.dao.TeamDao
import com.example.data.model.CounterEntity
import com.example.data.model.ItemEntity
import com.example.data.model.TeamMemberEntity
import com.example.data.model.TeamProfileEntity

@Database(
    entities = [
        CounterEntity::class,
        ItemEntity::class,
        TeamProfileEntity::class,
        TeamMemberEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CateringDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
    abstract fun itemDao(): ItemDao
    abstract fun teamDao(): TeamDao

    companion object {
        @Volatile
        private var INSTANCE: CateringDatabase? = null

        fun getDatabase(context: Context): CateringDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CateringDatabase::class.java,
                    "catering_display_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
