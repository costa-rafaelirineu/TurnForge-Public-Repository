package com.turnforge.dice.data.repository

import com.turnforge.dice.domain.model.RollEvent
import com.turnforge.dice.domain.repository.DiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryDiceRepository : DiceRepository {
    private val _history = MutableStateFlow<List<RollEvent>>(emptyList())

    override fun save(event: RollEvent) {
        _history.update { current -> listOf(event) + current }
    }

    override fun getHistory(): Flow<List<RollEvent>> {
        return _history.asStateFlow()
    }

    override fun clear() {
        _history.value = emptyList()
    }
}
