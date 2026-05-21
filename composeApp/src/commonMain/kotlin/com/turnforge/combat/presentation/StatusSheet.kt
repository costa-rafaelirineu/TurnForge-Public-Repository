package com.turnforge.combat.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.model.combat.ActiveStatus
import com.turnforge.model.combat.StatusEffectType
import org.jetbrains.compose.resources.stringResource
import turnforge.composeapp.generated.resources.Res
import turnforge.composeapp.generated.resources.adicionar_status_desc
import turnforge.composeapp.generated.resources.adicionar_status_titulo
import turnforge.composeapp.generated.resources.cancel_button
import turnforge.composeapp.generated.resources.confirmar_button
import turnforge.composeapp.generated.resources.duracao_turnos_label
import turnforge.composeapp.generated.resources.novo_status_dialog_title
import turnforge.composeapp.generated.resources.personalizado_label
import turnforge.composeapp.generated.resources.save_button
import turnforge.composeapp.generated.resources.status_buff_ac
import turnforge.composeapp.generated.resources.status_debuff_atk
import turnforge.composeapp.generated.resources.status_dot
import turnforge.composeapp.generated.resources.status_hot
import turnforge.composeapp.generated.resources.status_icon_label
import turnforge.composeapp.generated.resources.status_name_label
import turnforge.composeapp.generated.resources.status_reduce_damage
import turnforge.composeapp.generated.resources.tipo_efeito_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusSheet(
    onStatusesAdded: (List<ActiveStatus>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    var duration by remember { mutableStateOf(3) }
    var showCustomInput by remember { mutableStateOf(false) }
    var customName by remember { mutableStateOf("") }
    var customIcon by remember { mutableStateOf("✨") }

    // Usando remember para a lista de opções para que adições persistam durante a vida do Sheet
    val statusOptions = remember {
        mutableStateListOf<StatusOption>()
    }

    // Inicializa a lista com strings traduzidas (precisa ser dentro de um Composable)
    if (statusOptions.isEmpty()) {
        statusOptions.addAll(
            listOf(
                StatusOption(
                    "buff_ac",
                    StatusEffectType.BUFF_AC,
                    "🛡️",
                    stringResource(Res.string.status_buff_ac)
                ),
                StatusOption(
                    "debuff_atk",
                    StatusEffectType.DEBUFF_ATTACK,
                    "⚔️↓",
                    stringResource(Res.string.status_debuff_atk)
                ),
                StatusOption(
                    "dot",
                    StatusEffectType.DOT_DAMAGE,
                    "☠️",
                    stringResource(Res.string.status_dot)
                ),
                StatusOption(
                    "hot",
                    StatusEffectType.HOT_HEAL,
                    "💖",
                    stringResource(Res.string.status_hot)
                ),
                StatusOption(
                    "reduce",
                    StatusEffectType.REDUCE_DAMAGE,
                    "🛡️",
                    stringResource(Res.string.status_reduce_damage)
                )
            )
        )
    }

    val plusOption = StatusOption(
        "plus",
        StatusEffectType.CUSTOM,
        "➕",
        stringResource(Res.string.personalizado_label)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(Res.string.adicionar_status_titulo),
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Seleção de Tipo
            Text(
                stringResource(Res.string.tipo_efeito_label),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.heightIn(max = 200.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Renderiza as opções base + as customizadas que forem sendo adicionadas
                items(
                    items = statusOptions,
                    key = { it.id }
                ) { option ->
                    StatusOptionItem(
                        option = option,
                        isSelected = selectedIds.contains(option.id),
                        onClick = {
                            selectedIds = if (selectedIds.contains(option.id)) {
                                selectedIds - option.id
                            } else {
                                selectedIds + option.id
                            }
                        }
                    )
                }
                // Botão de adicionar novo
                item {
                    StatusOptionItem(
                        option = plusOption,
                        isSelected = false, // O botão de "+" nunca deve ficar selecionado
                        onClick = { showCustomInput = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Duração
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(Res.string.duracao_turnos_label),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.5f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (duration > 1) duration-- }) {
                        Text("-", color = Color.White, fontSize = 20.sp)
                    }
                    Text(
                        duration.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = { duration++ }) {
                        Icon(
                            Icons.Default.Add,
                            stringResource(Res.string.adicionar_status_desc),
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val haptic = LocalHapticFeedback.current
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val confirmScale by animateFloatAsState(if (isPressed) 0.96f else 1f)

            LaunchedEffect(isPressed) {
                if (isPressed && selectedIds.isNotEmpty()) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .graphicsLayer {
                        scaleX = confirmScale
                        scaleY = confirmScale
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(
                        enabled = selectedIds.isNotEmpty(),
                        interactionSource = interactionSource,
                        indication = ripple(),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val statuses = statusOptions
                                .filter { it.id in selectedIds }
                                .map { option ->
                                    ActiveStatus(
                                        id = if (option.id.startsWith("custom_")) option.id else "status_${option.id}_${
                                            kotlin.time.Clock.System.now().toEpochMilliseconds()
                                        }",
                                        name = option.label,
                                        icon = option.icon,
                                        remainingDuration = duration,
                                        type = option.type
                                    )
                                }
                            onStatusesAdded(statuses)
                            onDismiss()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                val isEnabled = selectedIds.isNotEmpty()
                val gradient = if (isEnabled) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF3B82F6), // Blue
                            Color(0xFF2DD4BF)  // Teal
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradient)
                        .background(if (isPressed) Color.White.copy(alpha = 0.1f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.confirmar_button),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp,
                        color = if (isEnabled) Color.White else Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }

    if (showCustomInput) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = Color(0xFF1A1A1A),
            title = {
                Text(
                    stringResource(Res.string.novo_status_dialog_title),
                    color = Color.White
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text(stringResource(Res.string.status_name_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customIcon,
                        onValueChange = { customIcon = it },
                        label = { Text(stringResource(Res.string.status_icon_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF3B82F6),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (customName.isNotBlank()) {
                        val newId = "custom_${kotlin.time.Clock.System.now().toEpochMilliseconds()}"
                        statusOptions.add(
                            StatusOption(newId, StatusEffectType.CUSTOM, customIcon, customName)
                        )
                        selectedIds = selectedIds + newId
                        showCustomInput = false
                        customName = ""
                        customIcon = "✨"
                    }
                }) {
                    Text(
                        stringResource(Res.string.save_button),
                        color = Color(0xFF2DD4BF),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showCustomInput = false
                    customName = ""
                    customIcon = "✨"
                }) {
                    Text(
                        stringResource(Res.string.cancel_button),
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        )
    }
}

@Composable
fun StatusOptionItem(
    option: StatusOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f)

    LaunchedEffect(isPressed) {
        if (isPressed) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) Color(0xFF5A3BDB).copy(alpha = 0.4f)
                else if (isPressed) Color.White.copy(alpha = 0.12f)
                else Color.White.copy(alpha = 0.05f)
            )
            .border(
                1.dp,
                if (isSelected) Color(0xFF5A3BDB) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(option.icon, fontSize = 20.sp)
            Text(
                option.label,
                fontSize = 10.sp,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

data class StatusOption(
    val id: String,
    val type: StatusEffectType,
    val icon: String,
    val label: String
)
