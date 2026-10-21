package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.MotionProjectDao
import com.example.data.model.MotionProject

@Database(
    entities = [MotionProject::class],
    version = 1,
    exportSchema = false
)
abstract class MotionDatabase : RoomDatabase() {
    abstract fun motionProjectDao(): MotionProjectDao

    companion object {
        @Volatile
        private var INSTANCE: MotionDatabase? = null

        fun getDatabase(context: Context): MotionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MotionDatabase::class.java,
                    "bangla_motion_studio.db"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
