package com.turnforge.dice.domain.model

enum class RollMode {
    NORMAL,
    ADVANTAGE,
    DISADVANTAGE
}

enum class RollSource {
    MANUAL
}

data class DiceGroup(
    val quantity: Int,
    val sides: Int
)

data class DiceDefinition(
    val groups: List<DiceGroup>,
    val modifier: Int = 0,
    val mode: RollMode = RollMode.NORMAL,
    val isDecimal: Boolean = false // If true, rolls 10, 20, 30... 100
) {
    companion object {
        fun single(
            quantity: Int,
            sides: Int,
            modifier: Int = 0,
            mode: RollMode = RollMode.NORMAL,
            isDecimal: Boolean = false
        ) =
            DiceDefinition(listOf(DiceGroup(quantity, sides)), modifier, mode, isDecimal)
    }
}

data class RollResult(
    val rolls: List<Int>,
    val modifier: Int,
    val total: Int, // Sum of rolls + modifier
    val finalTotal: Int, // Result after applying advantage/disadvantage logic
    val mode: RollMode,
    val otherRolls: List<Int>? = null, // The discarded set of rolls
    val otherTotal: Int? = null // The discarded total
)

data class RollEvent(
    val id: String,
    val definition: DiceDefinition,
    val result: RollResult,
    val timestamp: kotlin.time.Instant = kotlin.time.Clock.System.now(),
    val source: RollSource
)
