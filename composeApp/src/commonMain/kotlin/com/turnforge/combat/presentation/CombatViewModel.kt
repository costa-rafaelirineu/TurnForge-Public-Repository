package com.turnforge.combat.presentation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.turnforge.combat.domain.CombatEngine
import com.turnforge.model.Turn
import com.turnforge.model.combat.CombatAction
import com.turnforge.model.combat.CombatState
import com.turnforge.model.combat.SpellType
import com.turnforge.viewmodel.TurnViewModelDriver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CombatViewModel(
    private val turnDriver: TurnViewModelDriver,
    private val combatEngine: CombatEngine = CombatEngine()
) : ViewModel() {
    private val _combatState = MutableStateFlow(
        CombatState(
            playerHp = 100,
            playerMaxHp = 100,
            playerMana = 50,
            playerMaxMana = 50,
            turnNumber = 1
        )
    )
    val combatState: StateFlow<CombatState> = _combatState.asStateFlow()

    private val currentTurnActions = mutableStateListOf<CombatAction>()
    private var stateAtTurnStart: CombatState = _combatState.value

    val currentTurnDefenses: List<String>
        get() = currentTurnActions.filterIsInstance<CombatAction.Defense>()
            .map { "${it.icon} ${it.name} (${it.duration})" }

    fun addAction(action: CombatAction) {
        currentTurnActions.add(action)
        _combatState.update { applyAction(it, action) }
    }

    private fun applyAction(state: CombatState, action: CombatAction): CombatState {
        return when (action) {
            is CombatAction.Attack -> {
                if (action.hit && action.targetId != null) {
                    val updatedEnemies = state.enemies.mapNotNull { enemy ->
                        if (enemy.id == action.targetId) {
                            val newHp = (enemy.currentHp - action.damage).coerceIn(0, enemy.maxHp)
                            if (newHp <= 0) null else enemy.copy(currentHp = newHp)
                        } else enemy
                    }
                    state.copy(
                        enemies = updatedEnemies,
                        selectedEnemyId = if (state.selectedEnemyId == action.targetId && updatedEnemies.none { it.id == action.targetId }) null else state.selectedEnemyId
                    )
                } else state
            }

            is CombatAction.Spell -> {
                var newState = state
                // Consume mana
                if (!state.isInfiniteMana) {
                    newState =
                        newState.copy(playerMana = (state.playerMana - action.manaCost).coerceIn(0, state.playerMaxMana))
                }
                // Apply value (damage or heal)
                if (action.type == SpellType.HEAL) {
                    newState =
                        newState.copy(playerHp = (newState.playerHp + action.value).coerceIn(0, newState.playerMaxHp))
                } else if (action.targetId != null) {
                    val updatedEnemies = newState.enemies.mapNotNull { enemy ->
                        if (enemy.id == action.targetId) {
                            val newHp = (enemy.currentHp - action.value).coerceIn(0, enemy.maxHp)
                            if (newHp <= 0) null else enemy.copy(currentHp = newHp)
                        } else enemy
                    }
                    newState = newState.copy(
                        enemies = updatedEnemies,
                        selectedEnemyId = if (newState.selectedEnemyId == action.targetId && updatedEnemies.none { it.id == action.targetId }) null else newState.selectedEnemyId
                    )
                }
                newState
            }

            else -> state
        }
    }

    fun updateHp(change: Int) {
        _combatState.update {
            it.copy(playerHp = (it.playerHp + change).coerceIn(0, it.playerMaxHp))
        }
        if (currentTurnActions.isEmpty()) {
            stateAtTurnStart = _combatState.value
        }
    }

    fun setStats(hp: Int, maxHp: Int, mana: Int, maxMana: Int, isInfiniteMana: Boolean = false) {
        _combatState.update {
            it.copy(
                playerHp = hp.coerceIn(0, maxHp),
                playerMaxHp = maxHp,
                playerMana = mana.coerceIn(0, maxMana),
                playerMaxMana = maxMana,
                isInfiniteMana = isInfiniteMana
            )
        }
        if (currentTurnActions.isEmpty()) {
            stateAtTurnStart = _combatState.value
        }
    }

    fun updateEnemyHp(enemyId: String, change: Int) {
        _combatState.update { state ->
            val updatedEnemies = state.enemies.mapNotNull { enemy ->
                if (enemy.id == enemyId) {
                    val newHp = (enemy.currentHp + change).coerceIn(0, enemy.maxHp)
                    if (newHp <= 0) null else enemy.copy(currentHp = newHp)
                } else {
                    enemy
                }
            }
            state.copy(
                enemies = updatedEnemies,
                selectedEnemyId = if (state.selectedEnemyId == enemyId && updatedEnemies.none { it.id == enemyId }) null else state.selectedEnemyId
            )
        }
    }

    fun addEnemy(name: String, maxHp: Int) {
        _combatState.update { state ->
            val newEnemy = com.turnforge.model.combat.Enemy(
                id = "enemy_${kotlin.time.Clock.System.now().toEpochMilliseconds()}",
                name = name,
                currentHp = maxHp,
                maxHp = maxHp
            )
            state.copy(enemies = state.enemies + newEnemy)
        }
    }

    fun updateEnemy(id: String, name: String, currentHp: Int, maxHp: Int) {
        _combatState.update { state ->
            val updatedEnemies = state.enemies.mapNotNull {
                if (it.id == id) {
                    if (currentHp <= 0) null else it.copy(
                        name = name,
                        currentHp = currentHp,
                        maxHp = maxHp
                    )
                } else it
            }
            state.copy(
                enemies = updatedEnemies,
                selectedEnemyId = if (state.selectedEnemyId == id && updatedEnemies.none { it.id == id }) null else state.selectedEnemyId
            )
        }
    }

    fun removeEnemy(id: String) {
        _combatState.update { state ->
            state.copy(
                enemies = state.enemies.filter { it.id != id },
                selectedEnemyId = if (state.selectedEnemyId == id) null else state.selectedEnemyId
            )
        }
    }

    fun selectEnemy(enemyId: String?) {
        _combatState.update { it.copy(selectedEnemyId = enemyId) }
    }

    fun applyHealingIfNecessary(spellName: String, value: Int) {
        if (spellName.contains("Cura", ignoreCase = true)) {
            updateHp(value)
        }
    }

    fun updateMana(change: Int) {
        _combatState.update {
            if (it.isInfiniteMana && change < 0) return@update it
            it.copy(playerMana = (it.playerMana + change).coerceIn(0, it.playerMaxMana))
        }
        if (currentTurnActions.isEmpty()) {
            stateAtTurnStart = _combatState.value
        }
    }

    fun addStatuses(statuses: List<com.turnforge.model.combat.ActiveStatus>) {
        _combatState.update {
            it.copy(activeStatuses = it.activeStatuses + statuses)
        }
    }

    fun removeStatus(statusId: String) {
        _combatState.update {
            it.copy(activeStatuses = it.activeStatuses.filter { status -> status.id != statusId })
        }
    }

    fun resetTurnCount() {
        _combatState.update { it.copy(turnNumber = 1) }
        stateAtTurnStart = _combatState.value
    }

    fun finalizeTurn() {
        val stateAtEndBeforeProcessing = _combatState.value

        // Processa fim de turno (status, durations, etc)
        val nextTurnState =
            combatEngine.processEndOfTurn(stateAtEndBeforeProcessing, currentTurnActions.toList())

        val turnRecord = Turn(
            id = "turn_${_combatState.value.turnNumber}_${kotlin.time.Clock.System.now().toEpochMilliseconds()}",
            actorId = "PLAYER_1",
            actions = currentTurnActions.toList(),
            stateAtStart = stateAtTurnStart,
            stateAtEnd = stateAtEndBeforeProcessing,
            timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()
        )

        turnDriver.addTurn(turnRecord)

        // Prepara próximo turno
        _combatState.value = nextTurnState
        stateAtTurnStart = nextTurnState
        currentTurnActions.clear()
    }

    fun undoLastAction() {
        if (currentTurnActions.isNotEmpty()) {
            currentTurnActions.removeAt(currentTurnActions.size - 1)
            // Reverter estado para o início do turno e reaplicar as ações restantes
            var newState = stateAtTurnStart
            currentTurnActions.forEach { action ->
                newState = applyAction(newState, action)
            }
            _combatState.value = newState
        } else {
            // Se não há ações no turno atual, desfazer o turno inteiro via turnDriver
            turnDriver.undoLast()
            // Restaurar o estado do último turno disponível
            val previousTurns = turnDriver.turns.value
            if (previousTurns.isNotEmpty()) {
                val lastTurn = previousTurns.last()
                _combatState.value = lastTurn.stateAtEnd
                stateAtTurnStart = lastTurn.stateAtStart
            } else {
                // Se não houver turnos anteriores, volta para o turno 1
                _combatState.update { it.copy(turnNumber = 1) }
                stateAtTurnStart = _combatState.value
            }
        }
    }
}
