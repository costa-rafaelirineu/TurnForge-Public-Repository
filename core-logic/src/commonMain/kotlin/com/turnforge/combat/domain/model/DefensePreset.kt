package com.turnforge.combat.domain.model

data class DefensePreset(
    val id: String,
    val name: String,
    val icon: String,
    val description: String = "",
    val duration: Int = 1
)
