package com.turnforge.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.turnforge.model.ArchivedHistory
import com.turnforge.model.Turn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Entity(tableName = "archived_histories")
data class ArchivedHistoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val turnsJson: String,
    val archivedAt: Long
) {
    companion object {
        fun from(archivedHistory: ArchivedHistory): ArchivedHistoryEntity {
            return ArchivedHistoryEntity(
                id = archivedHistory.id,
                name = archivedHistory.name,
                turnsJson = Json.encodeToString(archivedHistory.turns),
                archivedAt = archivedHistory.archivedAt
            )
        }
    }

    fun toArchivedHistory(): ArchivedHistory {
        val turns = Json.decodeFromString<List<Turn>>(turnsJson)
        return ArchivedHistory(
            id = id,
            name = name,
            turns = turns,
            archivedAt = archivedAt
        )
    }
}
