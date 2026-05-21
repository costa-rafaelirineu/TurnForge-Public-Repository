package com.turnforge.dice.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.turnforge.dice.domain.model.DiceDefinition
import com.turnforge.dice.domain.model.RollEvent
import com.turnforge.dice.domain.model.RollMode
import com.turnforge.dice.domain.parser.DiceParser
import com.turnforge.dice.domain.usecase.DiceInput
import com.turnforge.dice.domain.usecase.RollDiceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DiceViewModel(
    private val rollDiceUseCase: RollDiceUseCase,
    private val diceParser: DiceParser
) : ViewModel() {
    var selectedSides by mutableStateOf(20)
    var quantity by mutableStateOf(1)
    var modifier by mutableStateOf(0)
    var isDecimal by mutableStateOf(false)
    var rollMode by mutableStateOf(RollMode.NORMAL)
    var expression by mutableStateOf("")
    var isMultiDiceMode by mutableStateOf(false)
    var multiDiceSelections by mutableStateOf<Map<Int, Int>>(emptyMap()) // sides -> quantity

    private val _lastResult = MutableStateFlow<RollEvent?>(null)
    val lastResult: StateFlow<RollEvent?> = _lastResult

    fun reset() {
        selectedSides = 20
        quantity = 1
        modifier = 0
        isDecimal = false
        rollMode = RollMode.NORMAL
        expression = ""
        isMultiDiceMode = false
        multiDiceSelections = emptyMap()
        _lastResult.value = null
    }

    fun updateMultiDiceSelection(sides: Int, quantity: Int) {
        val newSelections = multiDiceSelections.toMutableMap()
        if (quantity > 0) {
            newSelections[sides] = quantity
        } else {
            newSelections.remove(sides)
        }
        multiDiceSelections = newSelections
    }

    fun roll() {
        val definition = when {
            expression.isNotBlank() -> diceParser.parse(expression)?.copy(mode = rollMode)
            isMultiDiceMode && multiDiceSelections.isNotEmpty() -> createMultiDiceDefinition()
            else -> null
        } ?: DiceDefinition.single(
            quantity = quantity,
            sides = selectedSides,
            modifier = modifier,
            mode = rollMode,
            isDecimal = isDecimal
        )

        val event = rollDiceUseCase.execute(DiceInput(definition = definition))
        _lastResult.value = event
    }

    private fun createMultiDiceDefinition(): DiceDefinition {
        // Ordenar os dados para garantir consistência na exibição dos resultados (d20 > d12 > ...)
        val sortedSides = multiDiceSelections.keys.sortedDescending()

        val expressionParts = sortedSides.map { sides ->
            val qty = multiDiceSelections[sides] ?: 0
            "${qty}d$sides"
        }
        val baseExpression = expressionParts.joinToString(" + ")
        val fullExpression = if (modifier != 0) {
            "$baseExpression ${if (modifier > 0) "+" else "-"} ${if (modifier > 0) modifier else -modifier}"
        } else {
            baseExpression
        }

        return diceParser.parse(fullExpression)?.copy(mode = rollMode)
            ?: DiceDefinition.single(
                quantity = 1,
                sides = 20,
                modifier = modifier,
                mode = rollMode,
                isDecimal = false
            )
    }
}
