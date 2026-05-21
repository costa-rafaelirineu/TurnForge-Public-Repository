package com.turnforge.dice.domain.parser

import com.turnforge.dice.domain.model.DiceDefinition

interface DiceParser {
    fun parse(input: String): DiceDefinition?
}
