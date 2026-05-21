package com.turnforge.repository

import com.turnforge.model.ArchivedHistory
import com.turnforge.model.Turn

interface TurnRepository {
    suspend fun addTurn(turn: Turn)
    suspend fun getTurns(): List<Turn>
    suspend fun undoLast(): Turn?
    suspend fun clear()

    suspend fun archiveHistory(name: String): ArchivedHistory
    suspend fun getArchivedHistories(): List<ArchivedHistory>
    suspend fun clearArchivedHistories()

    suspend fun importData(turns: List<Turn>, archivedHistories: List<ArchivedHistory>)
}

