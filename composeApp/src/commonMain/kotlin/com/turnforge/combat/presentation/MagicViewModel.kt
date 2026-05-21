package com.turnforge.combat.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.turnforge.combat.domain.model.MagicPreset
import com.turnforge.dice.domain.model.RollEvent
import com.turnforge.dice.domain.usecase.DiceInput
import com.turnforge.dice.domain.usecase.RollDiceUseCase
import com.turnforge.model.combat.CombatAction
import com.turnforge.model.combat.SpellType
import turnforge.composeapp.generated.resources.*
import org.jetbrains.compose.resources.getString
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MagicViewModel(
    private val rollDiceUseCase: RollDiceUseCase,
    initialPresets: List<MagicPreset>? = null
) : ViewModel() {

    var currentPreset by mutableStateOf(MagicPreset("1", "...", "✨", 0, "1d8", 0, ""))
    var availablePresets by mutableStateOf<List<MagicPreset>>(emptyList())

    init {
        viewModelScope.launch {
            val defaultPresets = initialPresets ?: listOf(
                MagicPreset("1", getString(Res.string.preset_magic_fireball_name), "🔥", 10, "8d6", 0, getString(Res.string.preset_magic_fireball_desc)),
                MagicPreset("2", getString(Res.string.preset_magic_missiles_name), "✨", 5, "3d4", 3, getString(Res.string.preset_magic_missiles_desc)),
                MagicPreset("3", getString(Res.string.preset_magic_heal_name), "❇️", 8, "1d8", 4, getString(Res.string.preset_magic_heal_desc))
            )
            availablePresets = defaultPresets
            currentPreset = defaultPresets[0]
        }
    }

    private val _lastRollResult = MutableStateFlow<RollEvent?>(null)
    val lastRollResult = _lastRollResult.asStateFlow()

    var isCastPending by mutableStateOf(false)
        private set

    fun selectPreset(preset: MagicPreset) {
        currentPreset = preset
        resetResults()
    }

    private fun resetResults() {
        _lastRollResult.value = null
        isCastPending = false
    }

    fun reset() {
        resetResults()
    }

    fun updatePreset(updatedPreset: MagicPreset) {
        availablePresets = availablePresets.map {
            if (it.id == updatedPreset.id) updatedPreset else it
        }
        if (currentPreset.id == updatedPreset.id) {
            currentPreset = updatedPreset
        }
    }

    fun addPreset(preset: MagicPreset) {
        availablePresets = availablePresets + preset
    }

    fun removePreset(id: String) {
        availablePresets = availablePresets.filter { it.id != id }
        if (currentPreset.id == id && availablePresets.isNotEmpty()) {
            currentPreset = availablePresets[0]
        }
    }

    fun castSpell(currentMana: Int) {
        // Assume infinite mana if currentMana is Int.MAX_VALUE
        if (currentMana != Int.MAX_VALUE && currentMana < currentPreset.manaCost) return

        // Roll the dice automatically
        if (currentPreset.damageDice != null) {
            val expr = "${currentPreset.damageDice} + ${currentPreset.damageModifier}"
            val rollResult = rollDiceUseCase.execute(DiceInput(rawInput = expr))
            _lastRollResult.value = rollResult
        } else {
            _lastRollResult.value = null
        }
        isCastPending = true
    }

    fun confirmCast(
        selectedEnemyId: String? = null,
        combatViewModel: CombatViewModel? = null,
        onManaConsumed: (Int) -> Unit,
        onEffectApplied: (Int?) -> Unit,
        onActionRecorded: (CombatAction.Spell) -> Unit = {}
    ) {
        val rollValue = _lastRollResult.value?.result?.finalTotal
        onManaConsumed(currentPreset.manaCost)
        onEffectApplied(rollValue)

        val type = if (currentPreset.name.contains("Cura", ignoreCase = true)) {
            SpellType.HEAL
        } else {
            SpellType.DAMAGE
        }

        // Aplicar dano ao inimigo se houver um selecionado e for uma magia de dano
        if (type == SpellType.DAMAGE && rollValue != null) {
            selectedEnemyId?.let { enemyId ->
                combatViewModel?.updateEnemyHp(enemyId, -rollValue)
            }
        }

        val action = CombatAction.Spell(
            name = currentPreset.name,
            type = type,
            value = rollValue ?: 0,
            manaCost = currentPreset.manaCost,
            targetId = selectedEnemyId
        )
        onActionRecorded(action)

        // Reset state
        _lastRollResult.value = null
        isCastPending = false
    }

    private fun recordAction(text: String) {
        // Agora as ações são registradas pelo CombatViewModel
    }
}
