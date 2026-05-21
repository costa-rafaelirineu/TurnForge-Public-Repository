package com.turnforge.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArchivedHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(archivedHistory: ArchivedHistoryEntity)

    @Query("SELECT * FROM archived_histories ORDER BY archivedAt DESC")
    suspend fun getAll(): List<ArchivedHistoryEntity>

    @Query("DELETE FROM archived_histories")
    suspend fun clear()
}
