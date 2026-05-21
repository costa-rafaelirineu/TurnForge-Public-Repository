package com.turnforge.di

import com.turnforge.combat.domain.CombatEngine
import com.turnforge.combat.presentation.AttackViewModel
import com.turnforge.combat.presentation.CombatViewModel
import com.turnforge.combat.presentation.DefenseViewModel
import com.turnforge.combat.presentation.MagicViewModel
import com.turnforge.dice.data.parser.DiceParserImpl
import com.turnforge.dice.data.random.RandomProviderImpl
import com.turnforge.dice.domain.engine.DiceEngine
import com.turnforge.dice.domain.engine.DiceEngineImpl
import com.turnforge.dice.domain.engine.RandomProvider
import com.turnforge.dice.domain.parser.DiceParser
import com.turnforge.dice.domain.usecase.RollDiceUseCase
import com.turnforge.dice.presentation.DiceViewModel
import com.turnforge.viewmodel.TurnViewModel
import com.turnforge.viewmodel.TurnViewModelDriver
import com.turnforge.viewmodel.MainViewModel
import com.turnforge.di.coreModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    includes(coreModule)

    singleOf(::TurnViewModel) bind TurnViewModelDriver::class
    singleOf(::MainViewModel)
    factoryOf(::DiceViewModel)
    factory { AttackViewModel(get()) }
    factory { MagicViewModel(get()) }
    factory { DefenseViewModel() }
    factoryOf(::CombatViewModel)
}

expect val platformModule: Module
