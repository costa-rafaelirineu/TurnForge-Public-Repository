package com.turnforge.dice.data.random

import com.turnforge.dice.domain.engine.RandomProvider
import kotlin.random.Random

class RandomProviderImpl : RandomProvider {
    override fun nextInt(min: Int, max: Int): Int {
        return Random.nextInt(min, max + 1)
    }
}
