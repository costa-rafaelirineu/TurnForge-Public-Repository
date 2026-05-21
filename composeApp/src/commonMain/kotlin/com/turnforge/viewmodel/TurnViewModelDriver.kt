package com.turnforge.viewmodel

import com.turnforge.model.ArchivedHistory
import com.turnforge.model.Turn
import kotlinx.coroutines.flow.StateFlow

interface TurnViewModelDriver {
    val turns: StateFlow<List<Turn>>
    val archivedHistories: StateFlow<List<ArchivedHistory>>
    fun addTurn(turn: Turn)
    fun undoLast()
    fun clear()
    fun archiveHistory(name: String)
    fun clearArchivedHistories()
}
