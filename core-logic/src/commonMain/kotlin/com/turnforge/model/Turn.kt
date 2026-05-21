package com.turnforge.model

import com.turnforge.model.combat.CombatAction
import com.turnforge.model.combat.CombatState
import kotlinx.serialization.Serializable

@Serializable
data class Turn(
    val id: String,
    val actorId: String,
    val actions: List<CombatAction> = emptyList(),
    val stateAtStart: CombatState,
    val stateAtEnd: CombatState,
    val timestamp: Long = 0L
)
