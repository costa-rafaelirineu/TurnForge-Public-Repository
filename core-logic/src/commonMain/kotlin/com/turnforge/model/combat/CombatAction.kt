package com.turnforge.model.combat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CombatAction {
    @Serializable
    @SerialName("Attack")
    data class Attack(
        val weaponName: String,
        val hit: Boolean,
        val critical: Boolean,
        val rollValue: Int,
        val damage: Int,
        val targetAc: Int,
        val targetId: String? = null
    ) : CombatAction()

    @Serializable
    @SerialName("Spell")
    data class Spell(
        val name: String,
        @SerialName("spellType")
        val type: SpellType,
        val value: Int,
        val manaCost: Int,
        val targetId: String? = null,
        val statusApplied: String? = null
    ) : CombatAction()

    @Serializable
    @SerialName("Defense")
    data class Defense(
        val name: String,
        val icon: String,
        val effect: String,
        val duration: Int = 1
    ) : CombatAction()

    @Serializable
    @SerialName("DiceRoll")
    data class DiceRoll(
        val expression: String,
        val result: Int,
        val individualValues: List<Int>
    ) : CombatAction()
}

@Serializable
enum class SpellType {
    @SerialName("DAMAGE")
    DAMAGE,

    @SerialName("HEAL")
    HEAL,
}
