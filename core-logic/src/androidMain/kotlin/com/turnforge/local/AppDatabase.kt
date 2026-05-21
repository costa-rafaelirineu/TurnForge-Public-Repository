package com.turnforge.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TurnEntity::class, ArchivedHistoryEntity::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun turnDao(): TurnDao
    abstract fun archivedHistoryDao(): ArchivedHistoryDao

    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "turnforge.db"
            )
                .fallbackToDestructiveMigration(true)
                .build()
        }
    }
}

