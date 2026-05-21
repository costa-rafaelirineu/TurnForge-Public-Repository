package com.turnforge.combat.domain.model

data class MagicPreset(
    val id: String,
    val name: String,
    val icon: String,
    val manaCost: Int,
    val damageDice: String? = null,
    val damageModifier: Int = 0,
    val description: String = ""
)
