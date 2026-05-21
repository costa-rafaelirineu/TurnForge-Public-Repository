package com.turnforge.combat.domain.model

data class AttackEvent(
    val weaponName: String,
    val hit: Boolean,
    val critical: Boolean,
    val attackRoll: Int,
    val damage: Int?,
    val timestamp: Long = 0L // Placeholder for system time
) {
    override fun toString(): String {
        val hitText = if (hit) {
            if (critical) "CRÍTICO! 💥" else "ACERTOU! ⚔️"
        } else "ERROU... 🛡️"

        val damageText = if (hit && damage != null) " | Dano: $damage" else ""

        return "$hitText $weaponName ($attackRoll)$damageText"
    }
}
