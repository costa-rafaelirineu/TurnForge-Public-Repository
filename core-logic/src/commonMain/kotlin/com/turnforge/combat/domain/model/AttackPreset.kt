package com.turnforge.combat.domain.model

data class AttackPreset(
    val id: String,
    val name: String,
    val icon: String,
    val attackBonus: Int,
    val damageDice: String, // ex: "1d8"
    val damageModifier: Int
)
