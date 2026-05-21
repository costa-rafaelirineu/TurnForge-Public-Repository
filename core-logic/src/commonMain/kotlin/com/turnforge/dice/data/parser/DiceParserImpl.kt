package com.turnforge.dice.data.parser

import com.turnforge.dice.domain.model.DiceDefinition
import com.turnforge.dice.domain.model.DiceGroup
import com.turnforge.dice.domain.model.RollMode
import com.turnforge.dice.domain.parser.DiceParser

class DiceParserImpl : DiceParser {
    // Regex to match multiple dice groups like "2d6 + 1d4 + 5" or "d20 - 2"
    private val groupRegex = Regex("""(?:(\d+)?d(\d+))""", RegexOption.IGNORE_CASE)
    private val modifierRegex = Regex("""([+-])\s*(\d+)(?!d)""", RegexOption.IGNORE_CASE)

    override fun parse(input: String): DiceDefinition? {
        val cleanInput = input.replace(" ", "")

        val groups = groupRegex.findAll(cleanInput).map { match ->
            val (quantityStr, sidesStr) = match.destructured
            DiceGroup(
                quantity = quantityStr.toIntOrNull() ?: 1,
                sides = sidesStr.toIntOrNull() ?: return@map null
            )
        }.filterNotNull().toList()

        if (groups.isEmpty()) return null

        var totalModifier = 0
        modifierRegex.findAll(cleanInput).forEach { match ->
            val (operator, valueStr) = match.destructured
            val value = valueStr.toIntOrNull() ?: 0
            totalModifier += if (operator == "-") -value else value
        }

        return DiceDefinition(
            groups = groups,
            modifier = totalModifier,
            mode = RollMode.NORMAL
        )
    }
}
