package com.turnforge.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class ArchivedHistory(
    val id: String,
    val name: String,
    val turns: List<Turn>,
    val archivedAt: Long = Clock.System.now().toEpochMilliseconds()
)
