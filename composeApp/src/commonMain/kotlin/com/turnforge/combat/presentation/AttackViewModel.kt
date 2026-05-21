package com.turnforge.combat.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.turnforge.combat.domain.model.AttackEvent
import com.turnforge.combat.domain.model.AttackPreset
import com.turnforge.dice.domain.model.RollEvent
import com.turnforge.dice.domain.usecase.DiceInput
import com.turnforge.dice.domain.usecase.RollDiceUseCase
import com.turnforge.model.combat.CombatAction
import turnforge.composeapp.generated.resources.Res
import turnforge.composeapp.generated.resources.preset_attack_bow_name
import turnforge.composeapp.generated.resources.preset_attack_dagger_name
import turnforge.composeapp.generated.resources.preset_attack_sword_name
import org.jetbrains.compose.resources.getString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope

class AttackViewModel(
    private val rollDiceUseCase: RollDiceUseCase,
    initialPresets: List<AttackPreset>? = null
) : ViewModel() {
    
    var currentPreset by mutableStateOf(AttackPreset("1", "...", "⚔️", 0, "1d8", 0))
    var availablePresets by mutableStateOf<List<AttackPreset>>(emptyList())

    init {
        viewModelScope.launch {
            val defaultPresets = initialPresets ?: listOf(
                AttackPreset("1", getString(Res.string.preset_attack_sword_name), "⚔️", 5, "1d8", 3),
                AttackPreset("2", getString(Res.string.preset_attack_bow_name), "🏹", 6, "1d8", 2),
                AttackPreset("3", getString(Res.string.preset_attack_dagger_name), "🗡️", 4, "1d4", 2)
            )
            availablePresets = defaultPresets
            currentPreset = defaultPresets[0]
        }
    }

    private val _lastAttackResult = MutableStateFlow<RollEvent?>(null)
    val lastAttackResult = _lastAttackResult.asStateFlow()

    private val _lastDamageResult = MutableStateFlow<RollEvent?>(null)
    val lastDamageResult = _lastDamageResult.asStateFlow()

    var isHit by mutableStateOf<Boolean?>(null)
        private set

    var isCritical by mutableStateOf(false)
        private set

    var lastEvent by mutableStateOf<AttackEvent?>(null)
        private set

    private var _targetAc by mutableStateOf(15)
    var targetAc: Int
        get() = _targetAc
        set(value) {
            _targetAc = value.coerceAtLeast(0)
        }

    var isManualMode by mutableStateOf(false) // Modo de entrada manual
    var manualAttackRoll by mutableStateOf("") // Valor manual do ataque
    var manualDamageRoll by mutableStateOf("") // Valor manual do dano

    fun selectPreset(preset: AttackPreset) {
        currentPreset = preset
        resetResults()
    }

    fun reset() {
        resetResults()
        targetAc = 15
    }

    fun updatePreset(updatedPreset: AttackPreset) {
        availablePresets = availablePresets.map {
            if (it.id == updatedPreset.id) updatedPreset else it
        }
        if (currentPreset.id == updatedPreset.id) {
            currentPreset = updatedPreset
        }
    }

    fun addPreset(newPreset: AttackPreset) {
        availablePresets = availablePresets + newPreset
        currentPreset = newPreset
    }

    fun removePreset(presetId: String) {
        availablePresets = availablePresets.filter { it.id != presetId }
        if (currentPreset.id == presetId && availablePresets.isNotEmpty()) {
            currentPreset = availablePresets[0]
        }
    }


    private fun resetResults() {
        _lastAttackResult.value = null
        _lastDamageResult.value = null
        isHit = null
        isCritical = false
        lastEvent = null
    }

    fun performAttack() {
        if (isManualMode) {
            // Modo manual: usar valores digitados pelo usuário
            val hasAttack = manualAttackRoll.trim().isNotEmpty()
            val hasDamage = manualDamageRoll.trim().isNotEmpty()

            when {
                // Caso 1: Apenas ataque informado - mostrar apenas resultado do ataque (verificar hit/miss vs AC)
                hasAttack && !hasDamage -> {
                    val totalAttack = manualAttackRoll.trim().toIntOrNull() ?: 0
                    isCritical = totalAttack == 20

                    // Verificar Hit/Miss vs AC (Crítico sempre acerta)
                    isHit = isCritical || totalAttack >= targetAc

                    _lastAttackResult.value = RollEvent(
                        id = kotlin.time.Clock.System.now().toEpochMilliseconds().toString(),
                        definition = com.turnforge.dice.domain.model.DiceDefinition.single(
                            quantity = 1,
                            sides = 20,
                            modifier = currentPreset.attackBonus,
                            mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                        ),
                        result = com.turnforge.dice.domain.model.RollResult(
                            rolls = listOf(totalAttack),
                            total = totalAttack,
                            finalTotal = totalAttack,
                            modifier = currentPreset.attackBonus,
                            mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                        ),
                        timestamp = kotlin.time.Clock.System.now(),
                        source = com.turnforge.dice.domain.model.RollSource.MANUAL
                    )
                    _lastDamageResult.value = null

                    lastEvent = AttackEvent(
                        weaponName = currentPreset.name,
                        hit = isHit ?: false,
                        critical = isCritical,
                        attackRoll = totalAttack,
                        damage = null,
                        timestamp = 0L
                    )
                }

                // Caso 2: Apenas dano informado - mostrar apenas resultado do dano (assumir acerto)
                !hasAttack && hasDamage -> {
                    val totalDamage = manualDamageRoll.trim().toIntOrNull() ?: 0
                    isCritical = false
                    isHit = true // Assumir acerto se informou dano

                    // Criar um RollEvent dummy para o ataque para permitir exibição
                    _lastAttackResult.value = RollEvent(
                        id = kotlin.time.Clock.System.now().toEpochMilliseconds().toString(),
                        definition = com.turnforge.dice.domain.model.DiceDefinition.single(
                            quantity = 1,
                            sides = 20,
                            modifier = currentPreset.attackBonus,
                            mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                        ),
                        result = com.turnforge.dice.domain.model.RollResult(
                            rolls = listOf(0), // Valor dummy
                            total = 0,
                            finalTotal = 0,
                            modifier = currentPreset.attackBonus,
                            mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                        ),
                        timestamp = kotlin.time.Clock.System.now(),
                        source = com.turnforge.dice.domain.model.RollSource.MANUAL
                    )

                    _lastDamageResult.value = RollEvent(
                        id = kotlin.time.Clock.System.now().toEpochMilliseconds().toString(),
                        definition = com.turnforge.dice.domain.model.DiceDefinition.single(
                            quantity = 1,
                            sides = currentPreset.damageDice.substringAfter("d").toIntOrNull() ?: 8,
                            modifier = currentPreset.damageModifier,
                            mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                        ),
                        result = com.turnforge.dice.domain.model.RollResult(
                            rolls = listOf(totalDamage),
                            total = totalDamage,
                            finalTotal = totalDamage,
                            modifier = currentPreset.damageModifier,
                            mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                        ),
                        timestamp = kotlin.time.Clock.System.now(),
                        source = com.turnforge.dice.domain.model.RollSource.MANUAL
                    )

                    lastEvent = AttackEvent(
                        weaponName = currentPreset.name,
                        hit = true,
                        critical = false,
                        attackRoll = 0,
                        damage = totalDamage,
                        timestamp = 0L
                    )
                }

                // Caso 3: Ambos informados - comportamento padrão (verificar hit/miss vs AC)
                hasAttack && hasDamage -> {
                    val totalAttack = manualAttackRoll.trim().toIntOrNull() ?: 0
                    val totalDamage = manualDamageRoll.trim().toIntOrNull() ?: 0

                    // Verificar se é crítico (assumindo 20 se não especificado)
                    isCritical = totalAttack == 20

                    // Verificar Hit/Miss vs AC (Crítico sempre acerta)
                    if (isCritical || totalAttack >= targetAc) {
                        isHit = true

                        _lastAttackResult.value = RollEvent(
                            id = kotlin.time.Clock.System.now().toEpochMilliseconds().toString(),
                            definition = com.turnforge.dice.domain.model.DiceDefinition.single(
                                quantity = 1,
                                sides = 20,
                                modifier = currentPreset.attackBonus,
                                mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                            ),
                            result = com.turnforge.dice.domain.model.RollResult(
                                rolls = listOf(totalAttack),
                                total = totalAttack,
                                finalTotal = totalAttack,
                                modifier = currentPreset.attackBonus,
                                mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                            ),
                            timestamp = kotlin.time.Clock.System.now(),
                            source = com.turnforge.dice.domain.model.RollSource.MANUAL
                        )

                        _lastDamageResult.value = RollEvent(
                            id = kotlin.time.Clock.System.now().toEpochMilliseconds().toString(),
                            definition = com.turnforge.dice.domain.model.DiceDefinition.single(
                                quantity = 1,
                                sides = currentPreset.damageDice.substringAfter("d").toIntOrNull()
                                    ?: 8,
                                modifier = currentPreset.damageModifier,
                                mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                            ),
                            result = com.turnforge.dice.domain.model.RollResult(
                                rolls = listOf(totalDamage),
                                total = totalDamage,
                                finalTotal = totalDamage,
                                modifier = currentPreset.damageModifier,
                                mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                            ),
                            timestamp = kotlin.time.Clock.System.now(),
                            source = com.turnforge.dice.domain.model.RollSource.MANUAL
                        )

                        lastEvent = AttackEvent(
                            weaponName = currentPreset.name,
                            hit = true,
                            critical = isCritical,
                            attackRoll = totalAttack,
                            damage = totalDamage,
                            timestamp = 0L
                        )
                    } else {
                        isHit = false
                        _lastAttackResult.value = RollEvent(
                            id = kotlin.time.Clock.System.now().toEpochMilliseconds().toString(),
                            definition = com.turnforge.dice.domain.model.DiceDefinition.single(
                                quantity = 1,
                                sides = 20,
                                modifier = currentPreset.attackBonus,
                                mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                            ),
                            result = com.turnforge.dice.domain.model.RollResult(
                                rolls = listOf(totalAttack),
                                total = totalAttack,
                                finalTotal = totalAttack,
                                modifier = currentPreset.attackBonus,
                                mode = com.turnforge.dice.domain.model.RollMode.NORMAL
                            ),
                            timestamp = kotlin.time.Clock.System.now(),
                            source = com.turnforge.dice.domain.model.RollSource.MANUAL
                        )
                        _lastDamageResult.value = null

                        lastEvent = AttackEvent(
                            weaponName = currentPreset.name,
                            hit = false,
                            critical = false,
                            attackRoll = totalAttack,
                            damage = null,
                            timestamp = 0L
                        )
                    }
                }

                // Caso 4: Nenhum informado - não fazer nada
                else -> {
                    // Não fazer nada se nenhum valor foi informado
                }
            }
        } else {
            // Modo automático: rolar os dados
            // 1. Rolagem de Ataque: d20 + bônus
            val attackExpr = "1d20 + ${currentPreset.attackBonus}"
            val attackEvent = rollDiceUseCase.execute(DiceInput(rawInput = attackExpr))
            _lastAttackResult.value = attackEvent

            val totalAttack = attackEvent?.result?.finalTotal ?: 0
            val naturalRoll = attackEvent?.result?.rolls?.firstOrNull() ?: 0
            isCritical = naturalRoll == 20

            // 2. Verificar Hit/Miss vs AC (Crítico sempre acerta)
            if (isCritical || totalAttack >= targetAc) {
                isHit = true
                // 3. Se HIT -> rolar dano
                val damageExpr = "${currentPreset.damageDice} + ${currentPreset.damageModifier}"
                val damageEvent = rollDiceUseCase.execute(DiceInput(rawInput = damageExpr))
                _lastDamageResult.value = damageEvent

                // 4. Preparar evento (sem registrar ainda)
                lastEvent = AttackEvent(
                    weaponName = currentPreset.name,
                    hit = true,
                    critical = isCritical,
                    attackRoll = totalAttack,
                    damage = damageEvent?.result?.finalTotal,
                    timestamp = 0L
                )
            } else {
                isHit = false
                _lastDamageResult.value = null
                // 4. Preparar evento
                lastEvent = AttackEvent(
                    weaponName = currentPreset.name,
                    hit = false,
                    critical = false,
                    attackRoll = totalAttack,
                    damage = null,
                    timestamp = 0L
                )
            }
        }
    }

    fun confirmAttack(
        selectedEnemyId: String? = null,
        combatViewModel: CombatViewModel? = null,
        onDamageApplied: (Int) -> Unit = {},
        onActionRecorded: (CombatAction.Attack) -> Unit = {}
    ) {
        lastEvent?.let { event ->
            val damage = event.damage
            if (event.hit && damage != null) {
                onDamageApplied(damage)
                // Aplicar dano ao inimigo selecionado
                selectedEnemyId?.let { enemyId ->
                    combatViewModel?.updateEnemyHp(enemyId, -damage)
                }
            }
            val combatAction = CombatAction.Attack(
                weaponName = event.weaponName,
                hit = event.hit,
                critical = event.critical,
                rollValue = event.attackRoll,
                damage = damage ?: 0,
                targetAc = targetAc,
                targetId = selectedEnemyId
            )
            onActionRecorded(combatAction)
            resetResults()
        }
    }
}
