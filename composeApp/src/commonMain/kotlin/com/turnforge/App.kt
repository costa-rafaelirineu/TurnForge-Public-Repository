package com.turnforge

import androidx.compose.foundation.lazy.items
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.combat.presentation.*
import com.turnforge.core.rememberImagePickerLauncher
import com.turnforge.dice.presentation.DiceViewModel
import com.turnforge.navigation.SheetDestination
import com.turnforge.ui.components.*
import com.turnforge.ui.sheets.AppSheets
import com.turnforge.ui.theme.BattleSheetTheme
import com.turnforge.viewmodel.MainViewModel
import com.turnforge.viewmodel.TurnViewModel
import com.turnforge.viewmodel.TurnViewModelDriver
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import turnforge.composeapp.generated.resources.*

@Composable
fun App(
    driver: TurnViewModelDriver? = null,
    userEmail: String? = null,
    lastBackupDate: String? = null,
    onLogin: () -> Unit = {},
    onLogout: () -> Unit = {},
    onBackup: (suspend () -> Boolean) -> Unit = {},
    onRestore: (suspend () -> Boolean) -> Unit = {},
) {
    BattleSheetTheme {
        val mainViewModel = koinViewModel<MainViewModel>()
        val diceViewModel = koinViewModel<DiceViewModel>()
        val attackViewModel = koinViewModel<AttackViewModel>()
        val magicViewModel = koinViewModel<MagicViewModel>()
        val defenseViewModel = koinViewModel<DefenseViewModel>()
        val combatViewModel = koinViewModel<CombatViewModel>()

        val uiDriver: TurnViewModelDriver = driver ?: koinViewModel<TurnViewModel>()

        val combatState by combatViewModel.combatState.collectAsState()
        val charName by mainViewModel.charName.collectAsState()
        val charImage by mainViewModel.charImage.collectAsState()
        val charLevel by mainViewModel.charLevel.collectAsState()
        val charRace by mainViewModel.charRace.collectAsState()
        val charClass by mainViewModel.charClass.collectAsState()

        var isEditingStatuses by remember { mutableStateOf(false) }
        var isEditingEnemies by remember { mutableStateOf(false) }

        val defaultName = stringResource(Res.string.char_default_name)
        val defaultLvl = stringResource(Res.string.char_default_lvl)
        val defaultRace = stringResource(Res.string.char_default_race)
        val defaultClass = stringResource(Res.string.char_default_class)

        LaunchedEffect(Unit) {
            mainViewModel.initCharacterDefaults(defaultName, defaultLvl, defaultRace, defaultClass)
        }

        var tempImageCallback by remember { mutableStateOf<((String) -> Unit)?>(null) }
        val imagePickerLauncher = rememberImagePickerLauncher { uri ->
            tempImageCallback?.invoke(uri)
        }

        val snackbarHostState = remember { SnackbarHostState() }

        AppSheets(
            mainViewModel = mainViewModel,
            combatViewModel = combatViewModel,
            diceViewModel = diceViewModel,
            attackViewModel = attackViewModel,
            magicViewModel = magicViewModel,
            defenseViewModel = defenseViewModel,
            turnDriver = uiDriver,
            userEmail = userEmail,
            lastBackupDate = lastBackupDate,
            onLogin = onLogin,
            onLogout = onLogout,
            onBackup = onBackup,
            onRestore = onRestore,
            onPickImage = { callback ->
                tempImageCallback = { uri ->
                    callback(uri)
                    tempImageCallback = null
                }
                imagePickerLauncher()
            }
        )

        val scrollState = rememberScrollState()

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = { mainViewModel.navigateTo(SheetDestination.About) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = stringResource(Res.string.menu_content_desc),
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            bottomBar = {
                Box(modifier = Modifier.navigationBarsPadding()) {
                    FooterButtons(
                        turnNumber = combatState.turnNumber,
                        onUndo = { combatViewModel.undoLastAction() },
                        onHistory = { mainViewModel.navigateTo(SheetDestination.History) },
                        onFinish = {
                            diceViewModel.reset()
                            attackViewModel.reset()
                            magicViewModel.reset()
                            combatViewModel.finalizeTurn()
                        }
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CharacterCard(
                    name = charName,
                    image = charImage,
                    level = charLevel,
                    race = charRace,
                    clazz = charClass,
                    currentHp = combatState.playerHp,
                    maxHp = combatState.playerMaxHp,
                    currentMana = combatState.playerMana,
                    maxMana = combatState.playerMaxMana,
                    isInfiniteMana = combatState.isInfiniteMana,
                    onClick = { mainViewModel.navigateTo(SheetDestination.CharacterEdit) }
                )

                ActionGrid(
                    onAttack = { mainViewModel.navigateTo(SheetDestination.Attack) },
                    onMagic = { mainViewModel.navigateTo(SheetDestination.Magic) },
                    onDice = { mainViewModel.navigateTo(SheetDestination.Dice) },
                    onDefend = { mainViewModel.navigateTo(SheetDestination.Defense) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SectionTitleRow(
                    title = stringResource(Res.string.status_ativos_title),
                    isEditing = isEditingStatuses,
                    showEdit = combatState.activeStatuses.isNotEmpty(),
                    onEditClick = { isEditingStatuses = !isEditingStatuses },
                    onAddClick = { mainViewModel.navigateTo(SheetDestination.Status) }
                )

                Spacer(modifier = Modifier.height(4.dp))

                StatusRow(
                    activeStatuses = combatState.activeStatuses,
                    defenseLabels = combatViewModel.currentTurnDefenses,
                    isEditing = isEditingStatuses,
                    onDeleteStatus = { statusId ->
                        combatViewModel.removeStatus(statusId)
                        if (combatState.activeStatuses.isEmpty()) isEditingStatuses = false
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SectionTitleRow(
                    title = stringResource(Res.string.oponentes_ativos_title),
                    isEditing = isEditingEnemies,
                    showEdit = combatState.enemies.isNotEmpty(),
                    onEditClick = { isEditingEnemies = !isEditingEnemies },
                    onAddClick = {
                        mainViewModel.selectEnemyForAction(null, SheetDestination.EnemyEdit)
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (combatState.enemies.isEmpty()) {
                    isEditingEnemies = false
                    EmptyEnemiesMessage()
                } else {
                    EnemyList(
                        enemies = combatState.enemies,
                        selectedEnemyId = combatState.selectedEnemyId,
                        isEditing = isEditingEnemies,
                        onDelete = { combatViewModel.removeEnemy(it) },
                        onActionClick = { enemy ->
                            mainViewModel.selectEnemyForAction(enemy, SheetDestination.QuickHp)
                        },
                        onClick = { enemy, isSelected ->
                            if (isEditingEnemies) {
                                mainViewModel.selectEnemyForAction(enemy, SheetDestination.EnemyEdit)
                            } else {
                                combatViewModel.selectEnemy(if (isSelected) null else enemy.id)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionTitleRow(
    title: String,
    isEditing: Boolean,
    showEdit: Boolean,
    onEditClick: () -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        )
        val haptic = LocalHapticFeedback.current
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showEdit) {
                ActionButton(
                    icon = Icons.Default.Edit,
                    contentDescription = "Editar",
                    isSelected = isEditing,
                    onClick = onEditClick
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            ActionButton(
                icon = Icons.Default.Add,
                contentDescription = "Adicionar",
                onClick = onAddClick
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.85f else 1f)

    LaunchedEffect(isPressed) {
        if (isPressed) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 16.dp),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun EmptyEnemiesMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(Res.string.sem_oponentes_msg),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun EnemyList(
    enemies: List<com.turnforge.model.combat.Enemy>,
    selectedEnemyId: String?,
    isEditing: Boolean,
    onDelete: (String) -> Unit,
    onActionClick: (com.turnforge.model.combat.Enemy) -> Unit,
    onClick: (com.turnforge.model.combat.Enemy, Boolean) -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = enemies,
            key = { it.id }
        ) { enemy ->
            val isSelected = enemy.id == selectedEnemyId
            EnemyCard(
                name = enemy.name,
                currentHp = enemy.currentHp,
                maxHp = enemy.maxHp,
                isEditing = isEditing,
                isSelected = isSelected,
                onDelete = { onDelete(enemy.id) },
                onActionClick = { onActionClick(enemy) },
                onClick = { onClick(enemy, isSelected) }
            )
        }
    }
}
