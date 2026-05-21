package com.turnforge.model.combat

import kotlinx.serialization.Serializable

@Serializable
data class CombatState(
    val playerHp: Int,
    val playerMaxHp: Int,
    val playerMana: Int,
    val playerMaxMana: Int,
    val turnNumber: Int,
    val activeStatuses: List<ActiveStatus> = emptyList(),
    val enemies: List<Enemy> = emptyList(),
    val selectedEnemyId: String? = null,
    val isInfiniteMana: Boolean = false
)

@Serializable
data class ActiveStatus(
    val id: String,
    val name: String,
    val icon: String,
    val remainingDuration: Int,
    val type: StatusEffectType,
    val value: Int = 0
)

@Serializable
enum class StatusEffectType {
    BUFF_AC, DEBUFF_ATTACK, DOT_DAMAGE, HOT_HEAL, REDUCE_DAMAGE, CUSTOM
}
