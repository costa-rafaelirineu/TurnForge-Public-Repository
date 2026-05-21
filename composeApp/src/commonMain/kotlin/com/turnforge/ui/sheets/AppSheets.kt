package com.turnforge.ui.sheets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.turnforge.combat.presentation.AttackSheet
import com.turnforge.combat.presentation.AttackViewModel
import com.turnforge.combat.presentation.CombatViewModel
import com.turnforge.combat.presentation.DefenseSheet
import com.turnforge.combat.presentation.DefenseViewModel
import com.turnforge.combat.presentation.MagicSheet
import com.turnforge.combat.presentation.MagicViewModel
import com.turnforge.combat.presentation.StatusSheet
import com.turnforge.dice.presentation.DiceRollerSheet
import com.turnforge.dice.presentation.DiceViewModel
import com.turnforge.navigation.SheetDestination
import com.turnforge.ui.components.*
import com.turnforge.viewmodel.MainViewModel
import com.turnforge.viewmodel.TurnViewModelDriver
import com.turnforge.model.combat.SpellType

@Composable
fun AppSheets(
    mainViewModel: MainViewModel,
    combatViewModel: CombatViewModel,
    diceViewModel: DiceViewModel,
    attackViewModel: AttackViewModel,
    magicViewModel: MagicViewModel,
    defenseViewModel: DefenseViewModel,
    turnDriver: TurnViewModelDriver,
    userEmail: String?,
    lastBackupDate: String?,
    onLogin: () -> Unit,
    onLogout: () -> Unit,
    onBackup: (suspend () -> Boolean) -> Unit,
    onRestore: (suspend () -> Boolean) -> Unit,
    onPickImage: (((String) -> Unit)) -> Unit
) {
    val activeSheet by mainViewModel.activeSheet.collectAsState()
    val selectedEnemy by mainViewModel.selectedEnemy.collectAsState()
    val combatState by combatViewModel.combatState.collectAsState()

    val charName by mainViewModel.charName.collectAsState()
    val charImage by mainViewModel.charImage.collectAsState()
    val charLevel by mainViewModel.charLevel.collectAsState()
    val charClass by mainViewModel.charClass.collectAsState()
    val charRace by mainViewModel.charRace.collectAsState()

    when (activeSheet) {
        SheetDestination.Dice -> {
            DiceRollerSheet(
                viewModel = diceViewModel,
                onDismiss = { mainViewModel.dismissSheet() }
            )
        }
        SheetDestination.Attack -> {
            AttackSheet(
                viewModel = attackViewModel,
                combatViewModel = combatViewModel,
                onConfirm = {
                    attackViewModel.confirmAttack(
                        selectedEnemyId = combatState.selectedEnemyId,
                        combatViewModel = combatViewModel,
                        onActionRecorded = { action ->
                            combatViewModel.addAction(action)
                        }
                    )
                },
                onDismiss = { mainViewModel.dismissSheet() }
            )
        }
        SheetDestination.Magic -> {
            MagicSheet(
                viewModel = magicViewModel,
                combatViewModel = combatViewModel,
                currentMana = combatState.playerMana,
                onConfirm = {
                    magicViewModel.confirmCast(
                        selectedEnemyId = combatState.selectedEnemyId,
                        combatViewModel = combatViewModel,
                        onManaConsumed = { cost -> combatViewModel.updateMana(-cost) },
                        onEffectApplied = { rollValue ->
                            combatViewModel.applyHealingIfNecessary(
                                magicViewModel.currentPreset.name,
                                rollValue ?: 0
                            )
                        },
                        onActionRecorded = { action ->
                            combatViewModel.addAction(action)
                        }
                    )
                },
                onDismiss = { mainViewModel.dismissSheet() }
            )
        }
        SheetDestination.Defense -> {
            DefenseSheet(
                viewModel = defenseViewModel,
                onConfirm = {
                    val current = defenseViewModel.currentPreset
                    combatViewModel.addAction(
                        com.turnforge.model.combat.CombatAction.Defense(
                            name = current.name,
                            icon = current.icon,
                            effect = current.description,
                            duration = current.duration
                        )
                    )
                    mainViewModel.dismissSheet()
                },
                onDismiss = { mainViewModel.dismissSheet() }
            )
        }
        SheetDestination.History -> {
            HistorySheet(
                driver = turnDriver,
                onClear = { combatViewModel.resetTurnCount() },
                onDismiss = { mainViewModel.dismissSheet() }
            )
        }
        SheetDestination.Status -> {
            StatusSheet(
                onStatusesAdded = { statuses ->
                    combatViewModel.addStatuses(statuses)
                    mainViewModel.dismissSheet()
                },
                onDismiss = { mainViewModel.dismissSheet() }
            )
        }
        SheetDestination.CharacterEdit -> {
            CharacterEditSheet(
                name = charName,
                image = charImage,
                level = charLevel,
                clazz = charClass,
                race = charRace,
                currentHp = combatState.playerHp,
                maxHp = combatState.playerMaxHp,
                currentMana = combatState.playerMana,
                maxMana = combatState.playerMaxMana,
                isInfiniteMana = combatState.isInfiniteMana,
                onPickImage = onPickImage,
                onSave = { name, image, level, clazz, race, hp, mHp, mana, mMana, infinite ->
                    mainViewModel.updateCharacterData(name, image, level, clazz, race)
                    combatViewModel.setStats(hp, mHp, mana, mMana, infinite)
                    mainViewModel.dismissSheet()
                },
                onDismiss = { mainViewModel.dismissSheet() }
            )
        }
        SheetDestination.EnemyEdit -> {
            EnemyEditSheet(
                enemy = selectedEnemy,
                onSave = { name, currentHp, maxHp ->
                    selectedEnemy?.let { enemy ->
                        combatViewModel.updateEnemy(enemy.id, name, currentHp, maxHp)
                    } ?: run {
                        combatViewModel.addEnemy(name, maxHp)
                    }
                    mainViewModel.dismissSheet()
                },
                onDismiss = { mainViewModel.dismissSheet() }
            )
        }
        SheetDestination.QuickHp -> {
            selectedEnemy?.let { enemy ->
                QuickHpSheet(
                    enemy = enemy,
                    onApply = { change ->
                        combatViewModel.updateEnemyHp(enemy.id, change)
                    },
                    onDismiss = { mainViewModel.dismissSheet() }
                )
            }
        }
        SheetDestination.About -> {
            AboutSheet(
                onDismiss = { mainViewModel.dismissSheet() },
                userEmail = userEmail,
                lastBackupDate = lastBackupDate,
                onLogin = {
                    mainViewModel.dismissSheet()
                    onLogin()
                },
                onLogout = onLogout,
                onBackup = onBackup,
                onRestore = onRestore,
            )
        }
        null -> {}
    }
}
