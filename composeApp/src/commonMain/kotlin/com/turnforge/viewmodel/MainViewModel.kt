package com.turnforge.viewmodel

import androidx.lifecycle.ViewModel
import com.turnforge.model.combat.Enemy
import com.turnforge.navigation.SheetDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _activeSheet = MutableStateFlow<SheetDestination?>(null)
    val activeSheet: StateFlow<SheetDestination?> = _activeSheet.asStateFlow()

    private val _selectedEnemy = MutableStateFlow<Enemy?>(null)
    val selectedEnemy: StateFlow<Enemy?> = _selectedEnemy.asStateFlow()

    // Character State (Move from App.kt local state to ViewModel)
    private val _charName = MutableStateFlow("")
    val charName = _charName.asStateFlow()

    private val _charImage = MutableStateFlow("default_profile")
    val charImage = _charImage.asStateFlow()

    private val _charLevel = MutableStateFlow("")
    val charLevel = _charLevel.asStateFlow()

    private val _charRace = MutableStateFlow("")
    val charRace = _charRace.asStateFlow()

    private val _charClass = MutableStateFlow("")
    val charClass = _charClass.asStateFlow()

    fun navigateTo(destination: SheetDestination) {
        _activeSheet.value = destination
    }

    fun dismissSheet() {
        _activeSheet.value = null
        if (_activeSheet.value !is SheetDestination.QuickHp && _activeSheet.value !is SheetDestination.EnemyEdit) {
            _selectedEnemy.value = null
        }
    }

    fun selectEnemyForAction(enemy: Enemy?, destination: SheetDestination) {
        _selectedEnemy.value = enemy
        _activeSheet.value = destination
    }

    fun updateCharacterData(
        name: String,
        image: String,
        level: String,
        clazz: String,
        race: String
    ) {
        _charName.value = name
        _charImage.value = image
        _charLevel.value = level
        _charClass.value = clazz
        _charRace.value = race
    }

    fun initCharacterDefaults(name: String, level: String, race: String, clazz: String) {
        if (_charName.value.isEmpty()) {
            _charName.value = name
            _charLevel.value = level
            _charRace.value = race
            _charClass.value = clazz
        }
    }
}
