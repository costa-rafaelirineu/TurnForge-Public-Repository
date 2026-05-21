package com.turnforge.dice.domain.repository

import com.turnforge.dice.domain.model.RollEvent
import kotlinx.coroutines.flow.Flow

interface DiceRepository {
    fun save(event: RollEvent)
    fun getHistory(): Flow<List<RollEvent>>
    fun clear()
}
