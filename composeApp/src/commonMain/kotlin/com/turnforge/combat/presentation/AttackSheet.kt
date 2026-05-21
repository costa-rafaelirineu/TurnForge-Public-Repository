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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import turnforge.composeapp.generated.resources.adjust_target_ac_title
import turnforge.composeapp.generated.resources.atk_suffix
import turnforge.composeapp.generated.resources.attack_button
import turnforge.composeapp.generated.resources.attack_name_label
import turnforge.composeapp.generated.resources.attack_result_label
import turnforge.composeapp.generated.resources.attack_title
import turnforge.composeapp.generated.resources.attack_value_optional
import turnforge.composeapp.generated.resources.attack_value_placeholder
import turnforge.composeapp.generated.resources.bonus_field_label
import turnforge.composeapp.generated.resources.bonus_label
import turnforge.composeapp.generated.resources.cancel_button
import turnforge.composeapp.generated.resources.change_preset_title
import turnforge.composeapp.generated.resources.choose_target
import turnforge.composeapp.generated.resources.current_preset
import turnforge.composeapp.generated.resources.damage_label
import turnforge.composeapp.generated.resources.damage_result_label
import turnforge.composeapp.generated.resources.damage_value_label
import turnforge.composeapp.generated.resources.damage_value_placeholder
import turnforge.composeapp.generated.resources.delete_preset_desc
import turnforge.composeapp.generated.resources.dice_field_label
import turnforge.composeapp.generated.resources.edit_attack_dialog_title
import turnforge.composeapp.generated.resources.edit_preset
import turnforge.composeapp.generated.resources.finish_button
import turnforge.composeapp.generated.resources.hit_miss
import turnforge.composeapp.generated.resources.hit_success
import turnforge.composeapp.generated.resources.hp_label
import turnforge.composeapp.generated.resources.icon_label
import turnforge.composeapp.generated.resources.manual_dice_input
import turnforge.composeapp.generated.resources.modifier_field_label
import turnforge.composeapp.generated.resources.new_attack_dialog_title
import turnforge.composeapp.generated.resources.new_preset
import turnforge.composeapp.generated.resources.ok_button
import turnforge.composeapp.generated.resources.reroll_button
import turnforge.composeapp.generated.resources.save_button
import turnforge.composeapp.generated.resources.select_target_title
import turnforge.composeapp.generated.resources.target_ac_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttackSheet(
    viewModel: AttackViewModel,
    combatViewModel: CombatViewModel,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val current = viewModel.currentPreset
    val attackResult by viewModel.lastAttackResult.collectAsState()
    val damageResult by viewModel.lastDamageResult.collectAsState()
    val combatState by combatViewModel.combatState.collectAsState()
    val enemies = combatState.enemies

    var showEditTargetAc by remember { mutableStateOf(false) }
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
                stringResource(Res.string.attack_title),
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
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(current.icon, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(current.name, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                stringResource(Res.string.bonus_label),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                "+${current.attackBonus}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Column {
                            Text(
                                stringResource(Res.string.damage_label),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Checkbox para modo manual de entrada
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.isManualMode,
                    onCheckedChange = { viewModel.isManualMode = it },
                    modifier = Modifier.offset(x = (-12).dp),
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                Text(
                    stringResource(Res.string.manual_dice_input),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.offset(x = (-12).dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campos de entrada manual
            if (viewModel.isManualMode) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.manualAttackRoll,
                        onValueChange = { viewModel.manualAttackRoll = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                stringResource(Res.string.attack_value_optional),
                                fontSize = 12.sp
                            )
                        },
                        placeholder = {
                            Text(
                                stringResource(Res.string.attack_value_placeholder),
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    OutlinedTextField(
                        value = viewModel.manualDamageRoll,
                        onValueChange = { viewModel.manualDamageRoll = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                stringResource(Res.string.damage_value_label),
                                fontSize = 12.sp
                            )
                        },
                        placeholder = {
                            Text(
                                stringResource(Res.string.damage_value_placeholder),
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            cursorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Área de Resultados
            if (attackResult != null) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val hitStatus = viewModel.isHit
                    Text(
                        text = when (hitStatus) {
                            true -> stringResource(Res.string.hit_success)
                            false -> stringResource(Res.string.hit_miss)
                            else -> ""
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = if (hitStatus == true) Color(0xFF10B981) else Color(0xFFEF5350)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val attackRes = attackResult?.result
                        val attackDiceSum = attackRes?.rolls?.sum() ?: 0
                        val attackMod = attackRes?.modifier ?: 0
                        val attackSign = if (attackMod >= 0) "+" else "-"
                        val attackAbsMod = if (attackMod >= 0) attackMod else -attackMod

                        ResultCard(
                            label = stringResource(Res.string.attack_result_label),
                            total = attackRes?.finalTotal ?: 0,
                            breakdown = "$attackDiceSum $attackSign $attackAbsMod",
                            details = stringResource(
                                Res.string.target_ac_label,
                                viewModel.targetAc
                            ),
                            modifier = Modifier.weight(1f).clickable { showEditTargetAc = true },
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (hitStatus == true) {
                            val dmgRes = damageResult?.result
                            val dmgDiceSum = dmgRes?.rolls?.sum() ?: 0
                            val dmgMod = dmgRes?.modifier ?: 0
                            val dmgSign = if (dmgMod >= 0) "+" else "-"
                            val dmgAbsMod = if (dmgMod >= 0) dmgMod else -dmgMod

                            ResultCard(
                                label = stringResource(Res.string.damage_result_label),
                                total = dmgRes?.finalTotal ?: 0,
                                breakdown = "$dmgDiceSum $dmgSign $dmgAbsMod",
                                details = "${current.damageDice} + ${current.damageModifier}",
                                modifier = Modifier.weight(1f),
                                color = Color(0xFF3B82F6)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                    alpha = 0.1f
                                )
                            )
                        ) {
                            Text(
                                if (selectedEnemy != null) selectedEnemy.name else stringResource(
                                    Res.string.choose_target
                                ),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedEnemy != null) MaterialTheme.colorScheme.primary else (if (enemies.isEmpty()) Color.Gray else Color.White),
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = { showEditTargetAc = true },
                            modifier = Modifier.weight(1f).height(44.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                stringResource(Res.string.target_ac_label, viewModel.targetAc),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Ações principais
            if (attackResult == null) {
                Button(
                    onClick = { viewModel.performAttack() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
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
                            stringResource(Res.string.attack_button),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Botão secundário de Re-rolar
                    OutlinedButton(
                        onClick = { viewModel.performAttack() },
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

                    // Botão primário de Concluir
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (attackResult == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditTargetAc = true },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            stringResource(Res.string.target_ac_label, viewModel.targetAc),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

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
                                alpha = 0.1f
                            )
                        )
                    ) {
                        Text(
                            if (selectedEnemy != null) selectedEnemy.name else stringResource(Res.string.choose_target),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedEnemy != null) MaterialTheme.colorScheme.primary else (if (enemies.isEmpty()) Color.Gray else Color.White),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditPreset = true },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(Res.string.edit_preset),
                            fontSize = 12.sp,
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
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            stringResource(Res.string.new_preset),
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }
            }

            if (showEditPreset) {
                AttackPresetDialog(
                    title = stringResource(Res.string.edit_attack_dialog_title),
                    preset = current,
                    onDismiss = { showEditPreset = false },
                    onSave = { updated ->
                        viewModel.updatePreset(updated)
                        showEditPreset = false
                    }
                )
            }

            if (showAddPreset) {
                AttackPresetDialog(
                    title = stringResource(Res.string.new_attack_dialog_title),
                    preset = com.turnforge.combat.domain.model.AttackPreset(
                        id = kotlin.time.Clock.System.now().toEpochMilliseconds().toString(),
                        name = "",
                        icon = "⚔️",
                        attackBonus = 0,
                        damageDice = "1d6",
                        damageModifier = 0
                    ),
                    onDismiss = { showAddPreset = false },
                    onSave = { newPreset ->
                        viewModel.addPreset(newPreset)
                        showAddPreset = false
                    }
                )
            }

            if (showEditTargetAc) {
                AlertDialog(
                    onDismissRequest = { showEditTargetAc = false },
                    title = {
                        Text(
                            stringResource(Res.string.adjust_target_ac_title),
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { viewModel.targetAc-- },
                                enabled = viewModel.targetAc > 0
                            ) {
                                Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                viewModel.targetAc.toString(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            IconButton(onClick = { viewModel.targetAc++ }) {
                                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showEditTargetAc = false }) {
                            Text(stringResource(Res.string.ok_button))
                        }
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

            if (viewModel.availablePresets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

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
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 0.dp)
                ) {
                    items(
                        items = viewModel.availablePresets,
                        key = { it.id }
                    ) { preset ->
                        AttackPresetItem(
                            preset = preset,
                            isSelected = preset.id == current.id,
                            isEditMode = isEditMode,
                            onDelete = { viewModel.removePreset(preset.id) },
                            onClick = { viewModel.selectPreset(preset) }
                        )
                    }
                }
            }

            if (attackResult == null) {
                Spacer(modifier = Modifier.height(12.dp))

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
fun AttackPresetItem(
    preset: com.turnforge.combat.domain.model.AttackPreset,
    isSelected: Boolean,
    isEditMode: Boolean = false,
    onDelete: () -> Unit = {},
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
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
                            text = stringResource(Res.string.atk_suffix, preset.attackBonus),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )

                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f))
                        )

                        Text(
                            text = preset.damageDice,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.5f)
                        )
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
fun ResultCard(
    label: String,
    total: Int,
    details: String,
    breakdown: String? = null,
    modifier: Modifier = Modifier,
    color: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        Text(total.toString(), fontSize = 28.sp, fontWeight = FontWeight.Black, color = color)
        if (breakdown != null) {
            Text(
                text = breakdown,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        Text(details, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AttackPresetDialog(
    title: String,
    preset: com.turnforge.combat.domain.model.AttackPreset,
    onDismiss: () -> Unit,
    onSave: (com.turnforge.combat.domain.model.AttackPreset) -> Unit
) {
    var name by remember { mutableStateOf(preset.name) }
    var icon by remember { mutableStateOf(preset.icon) }
    var attackBonus by remember { mutableStateOf(preset.attackBonus.toString()) }
    var damageDice by remember { mutableStateOf(preset.damageDice) }
    var damageModifier by remember { mutableStateOf(preset.damageModifier.toString()) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val nameFocusRequester = remember { FocusRequester() }
    val iconFocusRequester = remember { FocusRequester() }
    val bonusFocusRequester = remember { FocusRequester() }
    val diceFocusRequester = remember { FocusRequester() }
    val modifierFocusRequester = remember { FocusRequester() }

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
                    label = { Text(stringResource(Res.string.attack_name_label)) },
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
                        keyboardActions = KeyboardActions(onNext = { bonusFocusRequester.requestFocus() })
                    )
                    OutlinedTextField(
                        value = attackBonus,
                        onValueChange = {
                            if (it.isEmpty() || it.toIntOrNull() != null || it == "-") attackBonus =
                                it
                        },
                        label = { Text(stringResource(Res.string.bonus_field_label)) },
                        colors = glassTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).focusRequester(bonusFocusRequester),
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
                        label = { Text(stringResource(Res.string.dice_field_label)) },
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
                        label = { Text(stringResource(Res.string.modifier_field_label)) },
                        colors = glassTextFieldColors,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f).focusRequester(modifierFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            if (name.isNotBlank() && damageDice.isNotBlank()) {
                                onSave(
                                    preset.copy(
                                        name = name,
                                        icon = icon,
                                        attackBonus = attackBonus.toIntOrNull() ?: 0,
                                        damageDice = damageDice,
                                        damageModifier = damageModifier.toIntOrNull() ?: 0
                                    )
                                )
                            }
                        })
                    )
                }
            }
        },
        confirmButton = {
            val isValid = name.isNotBlank() && damageDice.isNotBlank()
            Button(
                onClick = {
                    onSave(
                        preset.copy(
                            name = name,
                            icon = icon,
                            attackBonus = attackBonus.toIntOrNull() ?: 0,
                            damageDice = damageDice,
                            damageModifier = damageModifier.toIntOrNull() ?: 0
                        )
                    )
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    disabledContainerColor = Color(0xFF3B82F6).copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
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
