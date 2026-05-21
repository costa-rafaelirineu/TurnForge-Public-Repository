package com.turnforge.dice.domain.engine

import com.turnforge.dice.domain.model.DiceDefinition
import com.turnforge.dice.domain.model.RollMode
import com.turnforge.dice.domain.model.RollResult

interface RandomProvider {
    fun nextInt(min: Int, max: Int): Int
}

interface DiceEngine {
    fun roll(definition: DiceDefinition): RollResult
}

class DiceEngineImpl(private val randomProvider: RandomProvider) : DiceEngine {
    override fun roll(definition: DiceDefinition): RollResult {
        return when (definition.mode) {
            RollMode.NORMAL -> rollNormal(definition)
            RollMode.ADVANTAGE -> rollWithAdvantage(definition)
            RollMode.DISADVANTAGE -> rollWithDisadvantage(definition)
        }
    }

    private fun rollNormal(definition: DiceDefinition): RollResult {
        val rolls = definition.groups.flatMap { group ->
            List(group.quantity) {
                val roll = randomProvider.nextInt(1, group.sides)
                if (definition.isDecimal) roll * 10 else roll
            }
        }
        val total = rolls.sum() + definition.modifier
        return RollResult(
            rolls = rolls,
            modifier = definition.modifier,
            total = total,
            finalTotal = total,
            mode = RollMode.NORMAL
        )
    }

    private fun rollWithAdvantage(definition: DiceDefinition): RollResult {
        val allChosenRolls = mutableListOf<Int>()
        val allOtherRolls = mutableListOf<Int>()

        definition.groups.forEach { group ->
            val set1 = List(group.quantity) {
                val roll = randomProvider.nextInt(1, group.sides)
                if (definition.isDecimal) roll * 10 else roll
            }
            val set2 = List(group.quantity) {
                val roll = randomProvider.nextInt(1, group.sides)
                if (definition.isDecimal) roll * 10 else roll
            }

            val sum1 = set1.sum()
            val sum2 = set2.sum()

            if (sum1 >= sum2) {
                allChosenRolls.addAll(set1)
                allOtherRolls.addAll(set2)
            } else {
                allChosenRolls.addAll(set2)
                allOtherRolls.addAll(set1)
            }
        }

        val finalTotal = allChosenRolls.sum() + definition.modifier
        val otherTotalValue = allOtherRolls.sum() + definition.modifier

        return RollResult(
            rolls = allChosenRolls,
            modifier = definition.modifier,
            total = finalTotal,
            finalTotal = finalTotal,
            mode = RollMode.ADVANTAGE,
            otherRolls = allOtherRolls,
            otherTotal = otherTotalValue
        )
    }

    private fun rollWithDisadvantage(definition: DiceDefinition): RollResult {
        val allChosenRolls = mutableListOf<Int>()
        val allOtherRolls = mutableListOf<Int>()

        definition.groups.forEach { group ->
            val set1 = List(group.quantity) {
                val roll = randomProvider.nextInt(1, group.sides)
                if (definition.isDecimal) roll * 10 else roll
            }
            val set2 = List(group.quantity) {
                val roll = randomProvider.nextInt(1, group.sides)
                if (definition.isDecimal) roll * 10 else roll
            }

            val sum1 = set1.sum()
            val sum2 = set2.sum()

            if (sum1 <= sum2) {
                allChosenRolls.addAll(set1)
                allOtherRolls.addAll(set2)
            } else {
                allChosenRolls.addAll(set2)
                allOtherRolls.addAll(set1)
            }
        }

        val finalTotal = allChosenRolls.sum() + definition.modifier
        val otherTotalValue = allOtherRolls.sum() + definition.modifier

        return RollResult(
            rolls = allChosenRolls,
            modifier = definition.modifier,
            total = finalTotal,
            finalTotal = finalTotal,
            mode = RollMode.DISADVANTAGE,
            otherRolls = allOtherRolls,
            otherTotal = otherTotalValue
        )
    }
}
