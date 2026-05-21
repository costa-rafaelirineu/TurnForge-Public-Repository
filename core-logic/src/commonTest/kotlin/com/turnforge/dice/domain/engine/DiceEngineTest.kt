package com.turnforge.dice.domain.engine

import com.turnforge.dice.domain.model.DiceDefinition
import com.turnforge.dice.domain.model.DiceGroup
import com.turnforge.dice.domain.model.RollMode
import kotlin.test.Test
import kotlin.test.assertEquals

class DiceEngineTest {

    private class FakeRandomProvider : RandomProvider {
        var values = mutableListOf<Int>()
        override fun nextInt(min: Int, max: Int): Int {
            return if (values.isNotEmpty()) values.removeAt(0) else min
        }
    }

    private val randomProvider = FakeRandomProvider()
    private val engine = DiceEngineImpl(randomProvider)

    @Test
    fun testNormalRoll() {
        randomProvider.values = mutableListOf(5, 8)
        val definition = DiceDefinition(
            groups = listOf(DiceGroup(2, 10)),
            modifier = 3,
            mode = RollMode.NORMAL
        )

        val result = engine.roll(definition)

        assertEquals(listOf(5, 8), result.rolls)
        assertEquals(3, result.modifier)
        assertEquals(16, result.total) // 5 + 8 + 3
        assertEquals(16, result.finalTotal)
        assertEquals(RollMode.NORMAL, result.mode)
    }

    @Test
    fun testDecimalRoll() {
        randomProvider.values = mutableListOf(5)
        val definition = DiceDefinition(
            groups = listOf(DiceGroup(1, 10)),
            isDecimal = true
        )

        val result = engine.roll(definition)

        assertEquals(listOf(50), result.rolls)
        assertEquals(50, result.total)
    }

    @Test
    fun testAdvantageRollPickHigher() {
        // Set 1: 5, 5 (Sum 10)
        // Set 2: 8, 8 (Sum 16) -> Should pick this
        randomProvider.values = mutableListOf(5, 5, 8, 8)
        val definition = DiceDefinition(
            groups = listOf(DiceGroup(2, 20)),
            mode = RollMode.ADVANTAGE
        )

        val result = engine.roll(definition)

        assertEquals(listOf(8, 8), result.rolls)
        assertEquals(listOf(5, 5), result.otherRolls)
        assertEquals(16, result.total)
        assertEquals(10, result.otherTotal)
    }

    @Test
    fun testAdvantageRollPickFirstIfEqual() {
        // Set 1: 10
        // Set 2: 10
        randomProvider.values = mutableListOf(10, 10)
        val definition = DiceDefinition(
            groups = listOf(DiceGroup(1, 20)),
            mode = RollMode.ADVANTAGE
        )

        val result = engine.roll(definition)

        assertEquals(listOf(10), result.rolls)
        assertEquals(listOf(10), result.otherRolls)
    }

    @Test
    fun testDisadvantageRollPickLower() {
        // Set 1: 15, 15 (Sum 30)
        // Set 2: 5, 5 (Sum 10) -> Should pick this
        randomProvider.values = mutableListOf(15, 15, 5, 5)
        val definition = DiceDefinition(
            groups = listOf(DiceGroup(2, 20)),
            mode = RollMode.DISADVANTAGE
        )

        val result = engine.roll(definition)

        assertEquals(listOf(5, 5), result.rolls)
        assertEquals(listOf(15, 15), result.otherRolls)
        assertEquals(10, result.total)
        assertEquals(30, result.otherTotal)
    }

    @Test
    fun testMultipleGroupsNormalRoll() {
        randomProvider.values = mutableListOf(4, 6)
        val definition = DiceDefinition(
            groups = listOf(
                DiceGroup(1, 6),
                DiceGroup(1, 8)
            ),
            modifier = 2
        )

        val result = engine.roll(definition)

        assertEquals(listOf(4, 6), result.rolls)
        assertEquals(12, result.total) // 4 + 6 + 2
    }
}
