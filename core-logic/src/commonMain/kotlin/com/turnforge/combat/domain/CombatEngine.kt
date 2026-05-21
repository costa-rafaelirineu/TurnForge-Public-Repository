package com.turnforge.combat.domain

import com.turnforge.model.combat.ActiveStatus
import com.turnforge.model.combat.CombatAction
import com.turnforge.model.combat.CombatState
import com.turnforge.model.combat.StatusEffectType

class CombatEngine {

    fun processEndOfTurn(
        currentState: CombatState,
        currentTurnActions: List<CombatAction>
    ): CombatState {
        // 1. Aplicar efeitos de status (ex: DOT damage)
        var newPlayerHp = currentState.playerHp
        currentState.activeStatuses.forEach { status ->
            if (status.type == StatusEffectType.DOT_DAMAGE) {
                newPlayerHp = (newPlayerHp - status.value).coerceAtLeast(0)
            } else if (status.type == StatusEffectType.HOT_HEAL) {
                newPlayerHp = (newPlayerHp + status.value).coerceAtMost(currentState.playerMaxHp)
            }
        }

        // 2. Converter Defesas do turno atual em Status Ativos
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val defenseStatuses = currentTurnActions.filterIsInstance<CombatAction.Defense>()
            .mapIndexed { index, defense ->
                ActiveStatus(
                    id = "defense_${defense.name}_${now}_$index",
                    name = defense.name,
                    icon = defense.icon,
                    remainingDuration = defense.duration,
                    type = StatusEffectType.REDUCE_DAMAGE // Mapeamento padrão para defesas
                )
            }

        // 3. Converter Magias com statusApplied em Status Ativos (apenas se tiver statusApplied)
        val spellStatuses = currentTurnActions.filterIsInstance<CombatAction.Spell>()
            .filter { it.statusApplied != null }
            .mapIndexed { index, spell ->
                ActiveStatus(
                    id = "spell_${spell.name}_${now}_$index",
                    name = spell.statusApplied ?: "Spell Effect",
                    icon = "✨", // Ícone padrão para magias com status
                    remainingDuration = 1, // Duração padrão para status de magia
                    type = StatusEffectType.CUSTOM // Tipo padrão para status de magia
                )
            }

        // 4. Reduzir duração APENAS dos status que já EXISTIAM antes deste turno
        // As novas defesas e magias deste turno começam a contar a partir do próximo
        val updatedExistingStatuses = currentState.activeStatuses
            .map { it.copy(remainingDuration = it.remainingDuration - 1) }
            .filter { it.remainingDuration > 0 }

        return currentState.copy(
            playerHp = newPlayerHp,
            activeStatuses = updatedExistingStatuses + defenseStatuses + spellStatuses,
            turnNumber = currentState.turnNumber + 1
        )
    }
}
