package com.turnforge.viewmodel

import androidx.lifecycle.ViewModel
import com.turnforge.model.ArchivedHistory
import com.turnforge.model.Turn
import com.turnforge.repository.TurnRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TurnViewModel(private val repository: TurnRepository) : ViewModel(), TurnViewModelDriver {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val _turns = MutableStateFlow<List<Turn>>(emptyList())
    override val turns: StateFlow<List<Turn>> = _turns

    private val _archivedHistories = MutableStateFlow<List<ArchivedHistory>>(emptyList())
    override val archivedHistories: StateFlow<List<ArchivedHistory>> = _archivedHistories

    init {
        scope.launch {
            _turns.value = repository.getTurns()
            _archivedHistories.value = repository.getArchivedHistories()
        }
    }

    override fun addTurn(turn: Turn) {
        scope.launch {
            repository.addTurn(turn)
            _turns.value = repository.getTurns()
        }
    }

    override fun undoLast() {
        scope.launch {
            repository.undoLast()
            _turns.value = repository.getTurns()
        }
    }

    override fun clear() {
        scope.launch {
            repository.clear()
            _turns.value = emptyList()
        }
    }

    override fun archiveHistory(name: String) {
        scope.launch {
            repository.archiveHistory(name)
            _turns.value = repository.getTurns()
            _archivedHistories.value = repository.getArchivedHistories()
        }
    }

    override fun clearArchivedHistories() {
        scope.launch {
            repository.clearArchivedHistories()
            _archivedHistories.value = emptyList()
        }
    }

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}
