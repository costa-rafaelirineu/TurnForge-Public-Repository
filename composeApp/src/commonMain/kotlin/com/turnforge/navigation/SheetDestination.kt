package com.turnforge.navigation

import com.turnforge.model.combat.Enemy

sealed class SheetDestination {
    data object Dice : SheetDestination()
    data object Attack : SheetDestination()
    data object Magic : SheetDestination()
    data object Defense : SheetDestination()
    data object History : SheetDestination()
    data object Status : SheetDestination()
    data object CharacterEdit : SheetDestination()
    data object EnemyEdit : SheetDestination()
    data object QuickHp : SheetDestination()
    data object About : SheetDestination()
}
