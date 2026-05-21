package com.turnforge.viewmodel

import androidx.lifecycle.ViewModel
import com.turnforge.model.Turn

class AndroidTurnViewModel(private val delegate: TurnViewModel) : ViewModel(),
    TurnViewModelDriver {
    override val turns = delegate.turns
    override val archivedHistories = delegate.archivedHistories

    override fun addTurn(turn: Turn) = delegate.addTurn(turn)
    override fun undoLast() = delegate.undoLast()
    override fun clear() = delegate.clear()
    override fun archiveHistory(name: String) = delegate.archiveHistory(name)
    override fun clearArchivedHistories() = delegate.clearArchivedHistories()
}
