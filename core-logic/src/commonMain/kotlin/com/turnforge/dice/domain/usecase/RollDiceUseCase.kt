package com.turnforge.dice.domain.usecase

import com.turnforge.dice.domain.engine.DiceEngine
import com.turnforge.dice.domain.model.DiceDefinition
import com.turnforge.dice.domain.model.RollEvent
import com.turnforge.dice.domain.model.RollSource
import com.turnforge.dice.domain.parser.DiceParser

data class DiceInput(
    val rawInput: String? = null,
    val definition: DiceDefinition? = null,
    val source: RollSource = RollSource.MANUAL
)

class RollDiceUseCase(
    private val diceEngine: DiceEngine,
    private val diceParser: DiceParser
) {
    fun execute(input: DiceInput): RollEvent? {
        val definition = input.definition ?: input.rawInput?.let { diceParser.parse(it) }

        return definition?.let {
            val result = diceEngine.roll(it)
            RollEvent(
                id = "", // Should be generated (e.g., UUID)
                definition = it,
                result = result,
                source = input.source
            )
        }
    }
}
