package com.turnforge.combat.presentation

import androidx.compose.foundation.lazy.items
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.BottomSheetDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.combat.domain.model.DefensePreset
import com.turnforge.ui.theme.Secondary
import org.jetbrains.compose.resources.stringResource
import turnforge.composeapp.generated.resources.Res
import turnforge.composeapp.generated.resources.cancel_button
import turnforge.composeapp.generated.resources.change_preset_title
import turnforge.composeapp.generated.resources.current_preset
import turnforge.composeapp.generated.resources.defender_button
import turnforge.composeapp.generated.resources.defense_name_label
import turnforge.composeapp.generated.resources.defesa_titulo
import turnforge.composeapp.generated.resources.delete_preset_desc
import turnforge.composeapp.generated.resources.description_effect_label
import turnforge.composeapp.generated.resources.duration_label
import turnforge.composeapp.generated.resources.edit_preset
import turnforge.composeapp.generated.resources.icon_label
import turnforge.composeapp.generated.resources.new_defense_dialog_title
import turnforge.composeapp.generated.resources.new_preset
import turnforge.composeapp.generated.resources.save_button
import turnforge.composeapp.generated.resources.turnos_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefenseSheet(
    viewModel: DefenseViewModel,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val current = viewModel.currentPreset
    val presets = viewModel.presets
    var isEditMode by remember { mutableStateOf(false) }
    var showAddPreset by remember { mutableStateOf(false) }
    var showEditPreset by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }
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
                stringResource(Res.string.defesa_titulo),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(current.icon, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            current.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    if (current.description.isNotEmpty() || current.duration > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (current.description.isNotEmpty()) {
                                Text(
                                    current.description,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(0xFF10B981).copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    stringResource(Res.string.turnos_label, current.duration),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Confirmar Defesa
            Button(
                onClick = onConfirm,
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
                        stringResource(Res.string.defender_button),
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

            Spacer(modifier = Modifier.height(24.dp))

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
                    items = presets,
                    key = { it.id }
                ) { preset ->
                    DefensePresetItem(
                        preset = preset,
                        isSelected = preset.id == current.id,
                        isEditMode = isEditMode,
                        onDelete = { viewModel.removePreset(preset.id) },
                        onClick = { viewModel.selectPreset(preset) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Cancelar
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color.White.copy(alpha = 0.1f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
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

    if (showAddPreset) {
        DefensePresetDialog(
            title = stringResource(Res.string.new_defense_dialog_title),
            preset = DefensePreset(
                id = (presets.size + 1).toString(),
                name = "",
                icon = "🛡️",
                description = ""
            ),
            onDismiss = { showAddPreset = false },
            onSave = {
                viewModel.addPreset(it)
                showAddPreset = false
            }
        )
    }

    if (showEditPreset) {
        DefensePresetDialog(
            title = stringResource(Res.string.edit_preset),
            preset = current,
            onDismiss = { showEditPreset = false },
            onSave = {
                viewModel.updatePreset(it)
                showEditPreset = false
            }
        )
    }
}

@Composable
fun DefensePresetItem(
    preset: DefensePreset,
    isSelected: Boolean,
    isEditMode: Boolean = false,
    onDelete: () -> Unit = {},
    onClick: () -> Unit
) {
    val primaryColor = Color(0xFF10B981)
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
                    androidx.compose.foundation.BorderStroke(
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
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.2f)
                        )
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

                    Text(
                        text = stringResource(Res.string.turnos_label, preset.duration),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryColor
                    )
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
fun DefensePresetDialog(
    title: String,
    preset: DefensePreset,
    onDismiss: () -> Unit,
    onSave: (DefensePreset) -> Unit
) {
    var name by remember { mutableStateOf(preset.name) }
    var icon by remember { mutableStateOf(preset.icon) }
    var duration by remember { mutableStateOf(preset.duration.toString()) }
    var description by remember { mutableStateOf(preset.description) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val nameFocusRequester = remember { FocusRequester() }
    val iconFocusRequester = remember { FocusRequester() }
    val durationFocusRequester = remember { FocusRequester() }
    val descFocusRequester = remember { FocusRequester() }

    val glassTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = Color(0xFF10B981),
        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
        cursorColor = Color(0xFF10B981),
        focusedLabelColor = Color(0xFF10B981),
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
                    label = { Text(stringResource(Res.string.defense_name_label)) },
                    colors = glassTextFieldColors,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { iconFocusRequester.requestFocus() })
                )

                OutlinedTextField(
                    value = icon,
                    onValueChange = { icon = it },
                    label = { Text(stringResource(Res.string.icon_label)) },
                    colors = glassTextFieldColors,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().focusRequester(iconFocusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { durationFocusRequester.requestFocus() })
                )

                OutlinedTextField(
                    value = duration,
                    onValueChange = { if (it.all { char -> char.isDigit() }) duration = it },
                    label = { Text(stringResource(Res.string.duration_label)) },
                    colors = glassTextFieldColors,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().focusRequester(durationFocusRequester),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { descFocusRequester.requestFocus() })
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(Res.string.description_effect_label)) },
                    colors = glassTextFieldColors,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().focusRequester(descFocusRequester),
                    minLines = 2,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        if (name.isNotBlank()) {
                            onSave(
                                preset.copy(
                                    name = name,
                                    icon = icon,
                                    duration = duration.toIntOrNull() ?: 1,
                                    description = description
                                )
                            )
                        }
                    })
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        preset.copy(
                            name = name,
                            icon = icon,
                            duration = duration.toIntOrNull() ?: 1,
                            description = description
                        )
                    )
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    disabledContainerColor = Color(0xFF10B981).copy(alpha = 0.5f)
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
