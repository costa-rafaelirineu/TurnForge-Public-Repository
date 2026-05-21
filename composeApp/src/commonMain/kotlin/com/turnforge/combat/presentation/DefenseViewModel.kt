package com.turnforge.combat.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turnforge.combat.domain.model.DefensePreset
import turnforge.composeapp.generated.resources.*
import org.jetbrains.compose.resources.getString
import kotlinx.coroutines.launch

class DefenseViewModel(
    initialPresets: List<DefensePreset>? = null
) : ViewModel() {
    var presets by mutableStateOf<List<DefensePreset>>(emptyList())
    var currentPreset by mutableStateOf(DefensePreset("1", "...", "🛡️", ""))

    init {
        viewModelScope.launch {
            val defaultPresets = initialPresets ?: listOf(
                DefensePreset(
                    "1",
                    getString(Res.string.preset_defense_dodge_name),
                    "🛡️",
                    getString(Res.string.preset_defense_dodge_desc),
                    duration = 1
                ),
                DefensePreset("2", getString(Res.string.preset_defense_block_name), "🧱", getString(Res.string.preset_defense_block_desc), duration = 1),
                DefensePreset("3", getString(Res.string.preset_defense_shield_name), "🪄", getString(Res.string.preset_defense_shield_desc), duration = 1)
            )
            presets = defaultPresets
            currentPreset = defaultPresets.first()
        }
    }

    fun selectPreset(preset: DefensePreset) {
        currentPreset = preset
    }

    fun addPreset(preset: DefensePreset) {
        presets = presets + preset
    }

    fun updatePreset(preset: DefensePreset) {
        presets = presets.map { if (it.id == preset.id) preset else it }
        if (currentPreset.id == preset.id) {
            currentPreset = preset
        }
    }

    fun removePreset(id: String) {
        presets = presets.filter { it.id != id }
        if (currentPreset.id == id && presets.isNotEmpty()) {
            currentPreset = presets.first()
        }
    }
}
