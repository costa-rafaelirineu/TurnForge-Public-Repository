package com.turnforge.di

import com.turnforge.combat.domain.CombatEngine
import com.turnforge.dice.data.parser.DiceParserImpl
import com.turnforge.dice.data.random.RandomProviderImpl
import com.turnforge.dice.domain.engine.DiceEngine
import com.turnforge.dice.domain.engine.DiceEngineImpl
import com.turnforge.dice.domain.engine.RandomProvider
import com.turnforge.dice.domain.parser.DiceParser
import com.turnforge.dice.domain.usecase.RollDiceUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val corePlatformModule: Module

val coreModule = module {
    includes(corePlatformModule)

    singleOf(::RandomProviderImpl) bind RandomProvider::class
    singleOf(::DiceEngineImpl) bind DiceEngine::class
    singleOf(::DiceParserImpl) bind DiceParser::class

    singleOf(::RollDiceUseCase)

    singleOf(::CombatEngine)
}
