package com.turnforge.repository

import com.turnforge.local.ArchivedHistoryDao
import com.turnforge.local.ArchivedHistoryEntity
import com.turnforge.local.TurnDao
import com.turnforge.local.TurnEntity
import com.turnforge.model.ArchivedHistory
import com.turnforge.model.Turn

class RoomTurnRepository(
    private val turnDao: TurnDao,
    private val archivedHistoryDao: ArchivedHistoryDao
) : TurnRepository {
    override suspend fun addTurn(turn: Turn) {
        turnDao.insert(TurnEntity.from(turn))
    }

    override suspend fun getTurns(): List<Turn> = turnDao.getAll().map { it.toTurn() }

    override suspend fun undoLast(): Turn? {
        val all = turnDao.getAll()
        if (all.isEmpty()) return null
        val last = all.last()
        turnDao.deleteById(last.id)
        return last.toTurn()
    }

    override suspend fun clear() {
        turnDao.clear()
    }

    override suspend fun archiveHistory(name: String): ArchivedHistory {
        val currentTurns = getTurns()
        val archivedHistory = ArchivedHistory(
            id = "archive_${System.currentTimeMillis()}",
            name = name,
            turns = currentTurns
        )
        archivedHistoryDao.insert(ArchivedHistoryEntity.from(archivedHistory))
        turnDao.clear()
        return archivedHistory
    }

    override suspend fun getArchivedHistories(): List<ArchivedHistory> {
        return archivedHistoryDao.getAll().map { it.toArchivedHistory() }
    }

    override suspend fun clearArchivedHistories() {
        archivedHistoryDao.clear()
    }

    override suspend fun importData(turns: List<Turn>, archivedHistories: List<ArchivedHistory>) {
        turnDao.clear()
        turns.forEach { turnDao.insert(TurnEntity.from(it)) }
        archivedHistoryDao.clear()
        archivedHistories.forEach { archivedHistoryDao.insert(ArchivedHistoryEntity.from(it)) }
    }
}

