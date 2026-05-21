package com.turnforge.combat.presentation

import com.turnforge.combat.domain.model.AttackPreset
import com.turnforge.dice.domain.engine.DiceEngine
import com.turnforge.dice.domain.model.DiceDefinition
import com.turnforge.dice.domain.model.DiceGroup
import com.turnforge.dice.domain.model.RollMode
import com.turnforge.dice.domain.model.RollResult
import com.turnforge.dice.domain.parser.DiceParser
import com.turnforge.dice.domain.usecase.RollDiceUseCase
import com.turnforge.model.ArchivedHistory
import com.turnforge.model.Turn
import com.turnforge.viewmodel.TurnViewModelDriver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AttackViewModelTest {

    // Fakes
    private class FakeDiceEngine : DiceEngine {
        var forcedRoll: Int = 10
        override fun roll(definition: DiceDefinition): RollResult {
            // Se for d20 (ataque), usa o forcedRoll. Se for outro (dano), usa um valor fixo.
            val isD20 = definition.groups.any { it.sides == 20 }
            val rollValue = if (isD20) forcedRoll else 8

            return RollResult(
                rolls = listOf(rollValue),
                modifier = definition.modifier,
                total = rollValue + definition.modifier,
                finalTotal = rollValue + definition.modifier,
                mode = RollMode.NORMAL
            )
        }
    }

    private class FakeDiceParser : DiceParser {
        override fun parse(input: String): DiceDefinition? {
            val sides = if (input.contains("d20")) 20 else 8
            return DiceDefinition(
                groups = listOf(DiceGroup(quantity = 1, sides = sides)),
                modifier = 0,
                mode = RollMode.NORMAL
            )
        }
    }

    private class FakeTurnDriver : TurnViewModelDriver {
        override val turns: StateFlow<List<Turn>> = MutableStateFlow(emptyList())
        override val archivedHistories: StateFlow<List<ArchivedHistory>> =
            MutableStateFlow(emptyList())

        override fun addTurn(turn: Turn) {}
        override fun undoLast() {}
        override fun clear() {}
        override fun archiveHistory(name: String) {}
        override fun clearArchivedHistories() {}
    }

    private val diceEngine = FakeDiceEngine()
    private val diceParser = FakeDiceParser()
    private val rollDiceUseCase = RollDiceUseCase(diceEngine, diceParser)
    private val turnDriver = FakeTurnDriver()
    private val testPresets = listOf(
        AttackPreset("1", "Sword", "⚔️", 5, "1d8", 3),
        AttackPreset("2", "Bow", "🏹", 6, "1d8", 2),
        AttackPreset("3", "Dagger", "🗡️", 4, "1d4", 2)
    )
    private val viewModel = AttackViewModel(rollDiceUseCase, testPresets)

    @Test
    fun testInitialState() = runBlocking {
        delay(200)
        assertNotNull(viewModel.currentPreset)
        assertEquals(3, viewModel.availablePresets.size)
        assertNull(viewModel.lastAttackResult.value)
        assertNull(viewModel.isHit)
        assertFalse(viewModel.isManualMode)
    }

    @Test
    fun testSelectPreset() {
        val newPreset = AttackPreset("test", "Test Attack", "⚔️", 10, "1d10", 5)
        viewModel.selectPreset(newPreset)
        assertEquals(newPreset, viewModel.currentPreset)
    }

    @Test
    fun testReset() {
        viewModel.targetAc = 20
        viewModel.isManualMode = true
        viewModel.reset()
        assertEquals(15, viewModel.targetAc)
        assertNull(viewModel.isHit)
    }

    @Test
    fun testAddAndRemovePreset() {
        val newPreset = AttackPreset("4", "Special", "🔥", 8, "2d6", 4)
        viewModel.addPreset(newPreset)
        assertTrue(viewModel.availablePresets.contains(newPreset))
        assertEquals(newPreset, viewModel.currentPreset)

        viewModel.removePreset("4")
        assertFalse(viewModel.availablePresets.contains(newPreset))
    }

    @Test
    fun testManualModeCalculations() {
        viewModel.isManualMode = true
        viewModel.manualAttackRoll = "18"
        viewModel.manualDamageRoll = "12"
        viewModel.targetAc = 15

        viewModel.performAttack()

        assertEquals(true, viewModel.isHit)
    }

    @Test
    fun testAttackMiss() {
        // Mocking a low roll (1 + 5 bonus = 6 total) vs AC 15
        diceEngine.forcedRoll = 1
        viewModel.targetAc = 15
        viewModel.selectPreset(AttackPreset("1", "Sword", "⚔️", 5, "1d8", 3))

        viewModel.performAttack()

        assertEquals(false, viewModel.isHit)
        assertNotNull(viewModel.lastAttackResult.value)
    }

    @Test
    fun testAttackHit() {
        // Mocking a high roll (15 + 5 bonus = 20 total) vs AC 15
        diceEngine.forcedRoll = 15
        viewModel.targetAc = 15
        viewModel.selectPreset(AttackPreset("1", "Sword", "⚔️", 5, "1d8", 3))

        viewModel.performAttack()

        assertEquals(true, viewModel.isHit)
    }

    @Test
    fun testNatural20IsAlwaysHitAndCritical() {
        // Natural 20 should be critical and hit regardless of AC (standard RPG rule)
        diceEngine.forcedRoll = 20
        viewModel.targetAc = 30 // Very high AC
        viewModel.selectPreset(AttackPreset("1", "Sword", "⚔️", 0, "1d8", 0))

        viewModel.performAttack()

        assertEquals(true, viewModel.isHit)
        assertTrue(viewModel.isCritical)
    }

    @Test
    fun testNatural1IsAlwaysMiss() {
        diceEngine.forcedRoll = 1
        viewModel.targetAc = 2 // Very low AC
        viewModel.selectPreset(AttackPreset("1", "Sword", "⚔️", 10, "1d8", 0))

        viewModel.performAttack()

        // 1 + 10 = 11, but natural 1 is usually a miss.
        // Checking if the VM handles natural 1 as failure.
        assertEquals(false, viewModel.isHit)
    }

    @Test
    fun testDamageIsOnlyRolledOnHit() {
        diceEngine.forcedRoll = 1
        viewModel.targetAc = 20
        viewModel.performAttack()

        assertEquals(false, viewModel.isHit)
        assertNull(viewModel.lastDamageResult.value)
    }
}
