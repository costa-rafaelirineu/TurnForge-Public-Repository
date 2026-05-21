package com.turnforge.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.turnforge.model.Turn
import com.turnforge.model.combat.CombatAction
import com.turnforge.model.combat.CombatState

@Entity(tableName = "turns")
data class TurnEntity(
    @PrimaryKey val id: String,
    val actorId: String,
    val actions: List<CombatAction>,
    val stateAtStart: CombatState,
    val stateAtEnd: CombatState,
    val timestamp: Long
) {
    fun toTurn(): Turn = Turn(
        id = id,
        actorId = actorId,
        actions = actions,
        stateAtStart = stateAtStart,
        stateAtEnd = stateAtEnd,
        timestamp = timestamp
    )

    companion object {
        fun from(turn: Turn): TurnEntity = TurnEntity(
            id = turn.id,
            actorId = turn.actorId,
            actions = turn.actions,
            stateAtStart = turn.stateAtStart,
            stateAtEnd = turn.stateAtEnd,
            timestamp = turn.timestamp
        )
    }
}

