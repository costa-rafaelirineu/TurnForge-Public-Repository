package com.turnforge.combat.presentation

import com.turnforge.dice.domain.engine.DiceEngine
import com.turnforge.dice.domain.model.DiceDefinition
import com.turnforge.dice.domain.model.DiceGroup
import com.turnforge.dice.domain.model.RollMode
import com.turnforge.dice.domain.model.RollResult
import com.turnforge.dice.domain.parser.DiceParser
import com.turnforge.dice.domain.usecase.RollDiceUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MagicViewModelTest {

    private class FakeDiceEngine : DiceEngine {
        override fun roll(definition: DiceDefinition): RollResult {
            return RollResult(
                rolls = listOf(8),
                modifier = definition.modifier,
                total = 8 + definition.modifier,
                finalTotal = 8 + definition.modifier,
                mode = RollMode.NORMAL
            )
        }
    }

    private class FakeDiceParser : DiceParser {
        override fun parse(input: String): DiceDefinition? {
            return DiceDefinition(
                groups = listOf(DiceGroup(quantity = 1, sides = 8)),
                modifier = 0,
                mode = RollMode.NORMAL
            )
        }
    }

    private val diceEngine = FakeDiceEngine()
    private val diceParser = FakeDiceParser()
    private val rollDiceUseCase = RollDiceUseCase(diceEngine, diceParser)
    private val testPresets = listOf(
        com.turnforge.combat.domain.model.MagicPreset("1", "Fireball", "🔥", 10, "8d6", 0, "A powerful fire spell"),
        com.turnforge.combat.domain.model.MagicPreset("2", "Magic Missiles", "✨", 5, "3d4", 3, "Homing magic missiles"),
        com.turnforge.combat.domain.model.MagicPreset("3", "Heal", "❇️", 8, "1d8", 4, "Healing spell")
    )
    private val viewModel = MagicViewModel(rollDiceUseCase, testPresets)

    @Test
    fun testInitialState() {
        assertNotNull(viewModel.currentPreset)
        assertTrue(viewModel.availablePresets.isNotEmpty())
        assertNull(viewModel.lastRollResult.value)
        assertFalse(viewModel.isCastPending)
    }

    @Test
    fun testCastSpellManaCheck() {
        val preset = viewModel.currentPreset
        val currentMana = preset.manaCost - 1
        
        viewModel.castSpell(currentMana)
        
        assertFalse(viewModel.isCastPending)
        assertNull(viewModel.lastRollResult.value)
    }

    @Test
    fun testCastSpellSuccess() {
        val preset = viewModel.currentPreset
        val currentMana = preset.manaCost
        
        viewModel.castSpell(currentMana)
        
        assertTrue(viewModel.isCastPending)
        assertNotNull(viewModel.lastRollResult.value)
    }

    @Test
    fun testConfirmCastResetsState() {
        viewModel.castSpell(100)
        assertTrue(viewModel.isCastPending)
        
        viewModel.confirmCast(
            onManaConsumed = {},
            onEffectApplied = {}
        )
        
        assertFalse(viewModel.isCastPending)
        assertNull(viewModel.lastRollResult.value)
    }
}
