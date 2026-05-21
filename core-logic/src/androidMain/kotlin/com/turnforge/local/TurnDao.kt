package com.turnforge.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TurnDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(turn: TurnEntity)

    @Query("SELECT * FROM turns ORDER BY rowid ASC")
    suspend fun getAll(): List<TurnEntity>

    @Query("DELETE FROM turns WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM turns")
    suspend fun clear()
}

