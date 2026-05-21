package com.turnforge.model.combat

import kotlinx.serialization.Serializable

@Serializable
data class Enemy(
    val id: String,
    val name: String,
    val currentHp: Int,
    val maxHp: Int
)
