package com.turnforge.combat.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.ui.theme.Secondary
import org.jetbrains.compose.resources.stringResource
import turnforge.composeapp.generated.resources.Res
import turnforge.composeapp.generated.resources.adicionar_alvo
import turnforge.composeapp.generated.resources.cancel_button
import turnforge.composeapp.generated.resources.change_preset_title
import turnforge.composeapp.generated.resources.conjurando_magia
import turnforge.composeapp.generated.resources.current_preset
import turnforge.composeapp.generated.resources.damage_result_label
import turnforge.composeapp.generated.resources.dano_efeito_label
import turnforge.composeapp.generated.resources.delete_preset_desc
import turnforge.composeapp.generated.resources.description_label
import turnforge.composeapp.generated.resources.dice_optional_label
import turnforge.composeapp.generated.resources.edit_magic_dialog_title
import turnforge.composeapp.generated.resources.editar_button
import turnforge.composeapp.generated.resources.finish_button
import turnforge.composeapp.generated.resources.hp_label
import turnforge.composeapp.generated.resources.icon_label
import turnforge.composeapp.generated.resources.magias_titulo
import turnforge.composeapp.generated.resources.magic_name_label
import turnforge.composeapp.generated.resources.mana_cost_label
import turnforge.composeapp.generated.resources.mana_infinito
import turnforge.composeapp.generated.resources.mana_insuficiente
import turnforge.composeapp.generated.resources.mana_label
import turnforge.composeapp.generated.resources.modifier_short_label
import turnforge.composeapp.generated.resources.new_magic_dialog_title
import turnforge.composeapp.generated.resources.nova_magia_button
import turnforge.composeapp.generated.resources.reroll_button
import turnforge.composeapp.generated.resources.save_button
import turnforge.composeapp.generated.resources.select_target_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagicSheet(
    viewModel: MagicViewModel,
    combatViewModel: CombatViewModel,
    currentMana: Int,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val current = viewModel.currentPreset
    val lastRoll by viewModel.lastRollResult.collectAsState()
    val combatState by combatViewModel.combatState.collectAsState()
    val enemies = combatState.enemies

    var showEditPreset by remember { mutableStateOf(false) }
    var showAddPreset by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var showEnemySelection by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(Res.string.magias_titulo),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Preset Atual Card
            Text(
                stringResource(Res.string.current_preset),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Secondary.copy(alpha = 0.3f))
                    .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(current.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(current.name, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Surface(
                            color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.5f))
                        ) {
                            Text(
                                if (combatState.isInfiniteMana) stringResource(Res.string.mana_infinito) else stringResource(
                                    Res.string.mana_label,
                                    current.manaCost
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = Color(0xFF60A5FA),
                                fontSize = if (combatState.isInfiniteMana) 14.sp else 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (current.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            current.description,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }

                    if (current.damageDice != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row {
                            Column {
                                Text(
                                    stringResource(Res.string.dano_efeito_label),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                val modSign = if (current.damageModifier >= 0) "+" else ""
                                Text(
                                    "${current.damageDice}$modSign${current.damageModifier}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Área de Resultados
            if (viewModel.isCastPending) {
                if (lastRoll != null) {
                    ResultCard(
                        label = stringResource(Res.string.damage_result_label),
                        total = lastRoll?.result?.finalTotal ?: 0,
                        details = "${current.damageDice} + ${current.damageModifier}",
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val selectedEnemy = enemies.find { it.id == combatState.selectedEnemyId }
                    OutlinedButton(
                        onClick = { showEnemySelection = true },
                        enabled = enemies.isNotEmpty(),
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = if (selectedEnemy != null) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        } else ButtonDefaults.outlinedButtonColors(),
                        border = BorderStroke(
                            width = if (selectedEnemy != null) 2.dp else 1.dp,
                            color = if (selectedEnemy != null) MaterialTheme.colorScheme.primary else Color.White.copy(
                                alpha = 0.2f
                            )
                        )
                    ) {
                        Text(
                            if (selectedEnemy != null) selectedEnemy.name else stringResource(Res.string.adicionar_alvo),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedEnemy != null) MaterialTheme.colorScheme.primary else (if (enemies.isEmpty()) Color.Gray else Color.White),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = { showEditPreset = true },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(Res.string.editar_button),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.castSpell(if (combatState.isInfiniteMana) Int.MAX_VALUE else currentMana) },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Text(
                            stringResource(Res.string.reroll_button),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF3B82F6),
                                            Color(0xFF2DD4BF)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(Res.string.finish_button),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            } else {
                val canCast = combatState.isInfiniteMana || currentMana >= current.manaCost

                Button(
                    onClick = { viewModel.castSpell(if (combatState.isInfiniteMana) Int.MAX_VALUE else currentMana) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    enabled = canCast
                ) {
                    val gradient = if (canCast) {
                        Brush.horizontalGradient(listOf(Color(0xFF3B82F6), Color(0xFF2DD4BF)))
                    } else {
                        Brush.horizontalGradient(listOf(Color.Gray, Color.DarkGray))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(gradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (canCast) stringResource(Res.string.conjurando_magia) else stringResource(
                                Res.string.mana_insuficiente
                            ),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val selectedEnemy = enemies.find { it.id == combatState.selectedEnemyId }
                    OutlinedButton(
                        onClick = { showEnemySelection = true },
                        enabled = enemies.isNotEmpty(),
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = if (selectedEnemy != null) {
                            ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        } else ButtonDefaults.outlinedButtonColors(),
                        border = BorderStroke(
                            width = if (selectedEnemy != null) 2.dp else 1.dp,
                            color = if (selectedEnemy != null) MaterialTheme.colorScheme.primary else Color.White.copy(
                                alpha = 0.2f
                            )
                        )
                    ) {
                        Text(
                            if (selectedEnemy != null) selectedEnemy.name else stringResource(Res.string.adicionar_alvo),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedEnemy != null) MaterialTheme.colorScheme.primary else (if (enemies.isEmpty()) Color.Gray else Color.White),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    OutlinedButton(
                        onClick = { showEditPreset = true },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(Res.string.editar_button),
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = { showAddPreset = true },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(Res.string.nova_magia_button),
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }

            if (showEditPreset) {
                MagicPresetDialog(
                    title = stringResource(Res.string.edit_magic_dialog_title),
                    preset = current,
                    onDismiss = { showEditPreset = false },
                    onSave = { updated ->
                        viewModel.updatePreset(updated)
                        showEditPreset = false
                    }
                )
            }

            if (showAddPreset) {
                MagicPresetDialog(
                    title = stringResource(Res.string.new_magic_dialog_title),
                    preset = com.turnforge.combat.domain.model.MagicPreset(
                        id = kotlin.time.Clock.System.now().toEpochMilliseconds().toString(),
                        name = "",
                        icon = "✨",
                        manaCost = 0,
                        damageDice = "1d8",
                        damageModifier = 0,
                        description = ""
                    ),
                    onDismiss = { showAddPreset = false },
                    onSave = { newPreset ->
                        viewModel.addPreset(newPreset)
                        showAddPreset = false
                    }
                )
            }

            if (showEnemySelection) {
                AlertDialog(
                    onDismissRequest = { showEnemySelection = false },
                    title = {
                        Text(
                            stringResource(Res.string.select_target_title),
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            enemies.forEach { enemy ->
                                val isSelected = enemy.id == combatState.selectedEnemyId
                                OutlinedButton(
                                    onClick = {
                                        combatViewModel.selectEnemy(enemy.id)
                                        showEnemySelection = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    border = if (isSelected) BorderStroke(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary
                                    ) else null
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.Start,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            enemy.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            stringResource(
                                                Res.string.hp_label,
                                                enemy.currentHp,
                                                enemy.maxHp
                                            ),
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showEnemySelection = false }) {
                            Text(stringResource(Res.string.cancel_button))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trocar Preset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(Res.string.change_preset_title),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )

                IconButton(
                    onClick = { isEditMode = !isEditMode },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isEditMode) Icons.Default.CheckCircle else Icons.Default.Edit,
                        contentDescription = null,
                        tint = if (isEditMode) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.6f
                        ),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = viewModel.availablePresets,
                    key = { it.id }
                ) { preset ->
                    MagicPresetItem(
                        preset = preset,
                        isSelected = current.id == preset.id,
                        isEditMode = isEditMode,
                        isInfinite = combatState.isInfiniteMana,
                        onDelete = { viewModel.removePreset(preset.id) },
                        onClick = { viewModel.selectPreset(preset) }
                    )
                }
            }

            if (!viewModel.isCastPending) {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    )
                ) {
                    Text(
                        stringResource(Res.string.cancel_button),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MagicPresetItem(
    preset: com.turnforge.combat.domain.model.MagicPreset,
    isSelected: Boolean,
    isEditMode: Boolean = false,
    isInfinite: Boolean = false,
    onDelete: () -> Unit = {},
    onClick: () -> Unit
) {
    val primaryColor = Color(0xFF3B82F6)
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .width(135.dp)
            .height(110.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isSelected) Brush.verticalGradient(
                        listOf(primaryColor.copy(alpha = 0.3f), primaryColor.copy(alpha = 0.05f))
                    ) else Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                    )
                )
                .border(
                    BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) primaryColor else Color.White.copy(alpha = 0.15f)
                    ),
                    RoundedCornerShape(20.dp)
                )
                .clickable { onClick() }
        ) {
            // Shine layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            0.0f to Color.White.copy(alpha = 0.05f),
                            0.5f to Color.Transparent,
                            1.0f to Color.White.copy(alpha = 0.02f)
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(12.dp).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(preset.icon, fontSize = 20.sp)
                        }
                    }

                    if (isSelected && !isEditMode) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = preset.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isInfinite) stringResource(Res.string.mana_infinito) else stringResource(
                                Res.string.mana_label,
                                preset.manaCost
                            ),
                            fontSize = if (isInfinite) 14.sp else 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )

                        val damageDice = preset.damageDice
                        if (damageDice != null) {
                            Box(
                                modifier = Modifier
                                    .size(3.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.3f))
                            )

                            Text(
                                text = damageDice,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }

        if (isEditMode) {
            val deleteInteractionSource = remember { MutableInteractionSource() }
            val isDeletePressed by deleteInteractionSource.collectIsPressedAsState()
            val deleteScale by animateFloatAsState(if (isDeletePressed) 0.85f else 1f)

            LaunchedEffect(isDeletePressed) {
                if (isDeletePressed) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(24.dp)
                    .graphicsLayer {
                        scaleX = deleteScale
                        scaleY = deleteScale
                    }
                    .background(MaterialTheme.colorScheme.error, CircleShape)
                    .clickable(
                        interactionSource = deleteInteractionSource,
                        indication = ripple(bounded = false, radius = 12.dp),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDelete()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.delete_preset_desc),
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun MagicPresetDialog(
    title: String,
    preset: com.turnforge.combat.domain.model.MagicPreset,
    onDismiss: () -> Unit,
    onSave: (com.turnforge.combat.domain.model.MagicPreset) -> Unit
) {
    var name by remember { mutableStateOf(preset.name) }
    var icon by remember { mutableStateOf(preset.icon) }
    var manaCost by remember { mutableStateOf(preset.manaCost.toString()) }
    var damageDice by remember { mutableStateOf(preset.damageDice ?: "") }
    var damageModifier by remember { mutableStateOf(preset.damageModifier.toString()) }
    var description by remember { mutableStateOf(preset.description) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val nameFocusRequester = remember { FocusRequester() }
    val iconFocusRequester = remember { FocusRequester() }
    val manaFocusRequester = remember { FocusRequester() }
    val diceFocusRequester = remember { FocusRequester() }
    val modifierFocusRequester = remember { FocusRequester() }
    val descFocusRequester = remember { FocusRequester() }

    val glassTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color(0xFF3B82F6),
        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
        cursorColor = Color(0xFF3B82F6),
        focusedLabelColor = Color(0xFF3B82F6),
        unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(24.dp),
        title = {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
        },
        containerColor = Color(0xFF1A1A1A),
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(Res.string.magic_name_label)) },
                    colors = glassTextFieldColors,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { iconFocusRequester.requestFocus() })
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = icon,
                        onValueChange = { icon = it },
                        label = { Text(stringResource(Res.string.icon_label)) },
                        colors = glassTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).focusRequester(iconFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { manaFocusRequester.requestFocus() })
                    )
                    OutlinedTextField(
                        value = manaCost,
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null) manaCost = it
                        },
                        label = { Text(stringResource(Res.string.mana_cost_label)) },
                        colors = glassTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).focusRequester(manaFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { diceFocusRequester.requestFocus() })
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = damageDice,
                        onValueChange = { damageDice = it },
                        label = { Text(stringResource(Res.string.dice_optional_label)) },
                        colors = glassTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1.5f).focusRequester(diceFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { modifierFocusRequester.requestFocus() })
                    )
                    OutlinedTextField(
                        value = damageModifier,
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null || it == "-") damageModifier =
                                it
                        },
                        label = { Text(stringResource(Res.string.modifier_short_label)) },
                        colors = glassTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).focusRequester(modifierFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { descFocusRequester.requestFocus() })
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(Res.string.description_label)) },
                    colors = glassTextFieldColors,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().focusRequester(descFocusRequester),
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    })
                )
            }
        },
        confirmButton = {
            val isValid = name.isNotBlank()
            Button(
                onClick = {
                    onSave(
                        preset.copy(
                            name = name,
                            icon = icon,
                            manaCost = manaCost.toIntOrNull() ?: 0,
                            damageDice = if (damageDice.isEmpty()) null else damageDice,
                            damageModifier = damageModifier.toIntOrNull() ?: 0,
                            description = description
                        )
                    )
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    disabledContainerColor = Color(0xFF3B82F6).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(stringResource(Res.string.save_button), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel_button), color = Color.Gray)
            }
        }
    )
}
