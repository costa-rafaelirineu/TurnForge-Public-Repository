package com.turnforge.combat.presentation

import com.turnforge.combat.domain.CombatEngine
import com.turnforge.model.ArchivedHistory
import com.turnforge.model.Turn
import com.turnforge.model.combat.ActiveStatus
import com.turnforge.model.combat.CombatAction
import com.turnforge.model.combat.SpellType
import com.turnforge.model.combat.StatusEffectType
import com.turnforge.viewmodel.TurnViewModelDriver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CombatViewModelTest {

    private class FakeTurnDriver : TurnViewModelDriver {
        private val _turns = MutableStateFlow<List<Turn>>(emptyList())
        override val turns: StateFlow<List<Turn>> = _turns
        override val archivedHistories: StateFlow<List<ArchivedHistory>> = MutableStateFlow(emptyList())

        override fun addTurn(turn: Turn) {
            _turns.value = _turns.value + turn
        }
        override fun undoLast() {
            if (_turns.value.isNotEmpty()) {
                _turns.value = _turns.value.dropLast(1)
            }
        }
        override fun clear() { _turns.value = emptyList() }
        override fun archiveHistory(name: String) {}
        override fun clearArchivedHistories() {}
    }

    private val turnDriver = FakeTurnDriver()
    private val combatEngine = CombatEngine()
    private val viewModel = CombatViewModel(turnDriver, combatEngine)

    @Test
    fun testInitialStats() {
        val state = viewModel.combatState.value
        assertEquals(100, state.playerHp)
        assertEquals(50, state.playerMana)
        assertEquals(1, state.turnNumber)
    }

    @Test
    fun testUpdateHp() {
        viewModel.updateHp(-20)
        assertEquals(80, viewModel.combatState.value.playerHp)
        
        viewModel.updateHp(50)
        assertEquals(100, viewModel.combatState.value.playerHp) // Should cap at maxHp
    }

    @Test
    fun testAddEnemy() {
        viewModel.addEnemy("Orc", 30)
        assertEquals(1, viewModel.combatState.value.enemies.size)
        assertEquals("Orc", viewModel.combatState.value.enemies.first().name)
    }

    @Test
    fun testApplyAttackAction() {
        viewModel.addEnemy("Orc", 30)
        val enemyId = viewModel.combatState.value.enemies.first().id
        
        val attack = CombatAction.Attack(
            weaponName = "Sword",
            hit = true,
            critical = false,
            rollValue = 18,
            damage = 10,
            targetAc = 15,
            targetId = enemyId
        )
        
        viewModel.addAction(attack)
        
        assertEquals(20, viewModel.combatState.value.enemies.first().currentHp)
    }

    @Test
    fun testApplySpellAction() {
        val spell = CombatAction.Spell(
            name = "Fireball",
            type = SpellType.DAMAGE,
            value = 15,
            manaCost = 10,
            targetId = null // Player self damage if target null? (Logic check)
        )
        
        viewModel.addAction(spell)
        
        assertEquals(40, viewModel.combatState.value.playerMana)
    }

    @Test
    fun testUndoActionInCurrentTurn() {
        // Reduz HP para 80/100 antes de começar as ações do turno
        viewModel.updateHp(-20) 
        val hpAtStartOfActions = viewModel.combatState.value.playerHp
        assertEquals(80, hpAtStartOfActions)
        
        // Adiciona uma ação de cura
        val spell = CombatAction.Spell("Heal", SpellType.HEAL, 10, 5)
        viewModel.addAction(spell)
        
        // HP deve ser 90 (80 + 10)
        assertEquals(90, viewModel.combatState.value.playerHp)
        
        // Desfaz a ação
        viewModel.undoLastAction()
        
        // HP deve voltar para 80
        assertEquals(hpAtStartOfActions, viewModel.combatState.value.playerHp)
    }

    @Test
    fun testFinalizeTurn() {
        viewModel.finalizeTurn()
        assertEquals(2, viewModel.combatState.value.turnNumber)
        assertEquals(1, turnDriver.turns.value.size)
    }
}
