package com.turnforge.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.core.appIconPainter
import org.jetbrains.compose.resources.stringResource
import turnforge.composeapp.generated.resources.Res
import turnforge.composeapp.generated.resources.alterar_label
import turnforge.composeapp.generated.resources.cancel_button
import turnforge.composeapp.generated.resources.classe_label
import turnforge.composeapp.generated.resources.editar_personagem_titulo
import turnforge.composeapp.generated.resources.hp_atual_label
import turnforge.composeapp.generated.resources.hp_maximo_label
import turnforge.composeapp.generated.resources.lvl_label
import turnforge.composeapp.generated.resources.mana_atual_label
import turnforge.composeapp.generated.resources.mana_infinita_label
import turnforge.composeapp.generated.resources.mana_infinito
import turnforge.composeapp.generated.resources.mana_maximo_label
import turnforge.composeapp.generated.resources.nome_jogador_label
import turnforge.composeapp.generated.resources.raca_label
import turnforge.composeapp.generated.resources.salvar_alteracoes_button

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterEditSheet(
    name: String,
    image: String,
    level: String,
    clazz: String,
    race: String,
    currentHp: Int,
    maxHp: Int,
    currentMana: Int,
    maxMana: Int,
    isInfiniteMana: Boolean,
    onPickImage: ((String) -> Unit) -> Unit,
    onSave: (String, String, String, String, String, Int, Int, Int, Int, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var editName by remember { mutableStateOf(name) }
    var editImage by remember { mutableStateOf(image) }
    var editLevel by remember { mutableStateOf(level) }
    var editClazz by remember { mutableStateOf(clazz) }
    var editRace by remember { mutableStateOf(race) }
    var editCurrentHp by remember { mutableStateOf(currentHp.toString()) }
    var editMaxHp by remember { mutableStateOf(maxHp.toString()) }
    var editCurrentMana by remember { mutableStateOf(currentMana.toString()) }
    var editMaxMana by remember { mutableStateOf(maxMana.toString()) }
    var editIsInfiniteMana by remember { mutableStateOf(isInfiniteMana) }

    var imageUriToCrop by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val levelFocusRequester = remember { FocusRequester() }
    val classFocusRequester = remember { FocusRequester() }
    val raceFocusRequester = remember { FocusRequester() }
    val curHpFocusRequester = remember { FocusRequester() }
    val maxHpFocusRequester = remember { FocusRequester() }
    val curManaFocusRequester = remember { FocusRequester() }
    val maxManaFocusRequester = remember { FocusRequester() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF1A1A1A),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.White.copy(alpha = 0.3f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(Res.string.editar_personagem_titulo),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar Selection
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .clickable {
                        onPickImage { pickedUri ->
                            imageUriToCrop = pickedUri
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val p = appIconPainter(editImage)
                if (p != null) {
                    Image(
                        painter = p,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.alterar_label),
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Overlay informativo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        stringResource(Res.string.alterar_label),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                label = {
                    Text(
                        stringResource(Res.string.nome_jogador_label),
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { levelFocusRequester.requestFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = editLevel,
                    onValueChange = { if (it.length <= 3) editLevel = it },
                    label = {
                        Text(
                            stringResource(Res.string.lvl_label),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.weight(0.22f).focusRequester(levelFocusRequester),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { raceFocusRequester.requestFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    )
                )
                OutlinedTextField(
                    value = editRace,
                    onValueChange = { editRace = it },
                    label = {
                        Text(
                            stringResource(Res.string.raca_label),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.weight(0.38f).focusRequester(raceFocusRequester),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { classFocusRequester.requestFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    )
                )
                OutlinedTextField(
                    value = editClazz,
                    onValueChange = { editClazz = it },
                    label = {
                        Text(
                            stringResource(Res.string.classe_label),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.weight(0.40f).focusRequester(classFocusRequester),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { curHpFocusRequester.requestFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = editCurrentHp,
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) editCurrentHp = it
                    },
                    label = {
                        Text(
                            stringResource(Res.string.hp_atual_label),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.weight(1f).focusRequester(curHpFocusRequester),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { maxHpFocusRequester.requestFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF66BB6A),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    )
                )
                OutlinedTextField(
                    value = editMaxHp,
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) editMaxHp = it
                    },
                    label = {
                        Text(
                            stringResource(Res.string.hp_maximo_label),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.weight(1f).focusRequester(maxHpFocusRequester),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { curManaFocusRequester.requestFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF66BB6A),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = if (editIsInfiniteMana) stringResource(Res.string.mana_infinito) else editCurrentMana,
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) editCurrentMana = it
                    },
                    label = {
                        Text(
                            stringResource(Res.string.mana_atual_label),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.weight(1f).focusRequester(curManaFocusRequester),
                    enabled = !editIsInfiniteMana,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { maxManaFocusRequester.requestFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        disabledBorderColor = Color.White.copy(alpha = 0.05f)
                    )
                )
                OutlinedTextField(
                    value = if (editIsInfiniteMana) stringResource(Res.string.mana_infinito) else editMaxMana,
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) editMaxMana = it
                    },
                    label = {
                        Text(
                            stringResource(Res.string.mana_maximo_label),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.weight(1f).focusRequester(maxManaFocusRequester),
                    enabled = !editIsInfiniteMana,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        disabledTextColor = Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = Color(0xFF3B82F6),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        disabledBorderColor = Color.White.copy(alpha = 0.05f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editIsInfiniteMana = !editIsInfiniteMana },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = editIsInfiniteMana,
                    onCheckedChange = { editIsInfiniteMana = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF3B82F6),
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    stringResource(Res.string.mana_infinita_label),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Salvar com Design igual ao Finalizar Turno
            val haptic = LocalHapticFeedback.current
            val saveInteractionSource = remember { MutableInteractionSource() }
            val isSavePressed by saveInteractionSource.collectIsPressedAsState()
            val saveScale by animateFloatAsState(if (isSavePressed) 0.96f else 1f)

            LaunchedEffect(isSavePressed) {
                if (isSavePressed) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .graphicsLayer {
                        scaleX = saveScale
                        scaleY = saveScale
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = saveInteractionSource,
                        indication = ripple(),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSave(
                                editName,
                                editImage,
                                editLevel,
                                editClazz,
                                editRace,
                                editCurrentHp.toIntOrNull() ?: currentHp,
                                editMaxHp.toIntOrNull() ?: maxHp,
                                editCurrentMana.toIntOrNull() ?: currentMana,
                                editMaxMana.toIntOrNull() ?: maxMana,
                                editIsInfiniteMana
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6), // Blue
                                    Color(0xFF2DD4BF)  // Teal
                                )
                            )
                        )
                        .background(if (isSavePressed) Color.White.copy(alpha = 0.1f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(Res.string.salvar_alteracoes_button),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val cancelInteractionSource = remember { MutableInteractionSource() }
            val isCancelPressed by cancelInteractionSource.collectIsPressedAsState()
            val cancelScale by animateFloatAsState(if (isCancelPressed) 0.96f else 1f)

            LaunchedEffect(isCancelPressed) {
                if (isCancelPressed) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .graphicsLayer {
                        scaleX = cancelScale
                        scaleY = cancelScale
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        1.dp,
                        Color.White.copy(alpha = if (isCancelPressed) 0.3f else 0.1f),
                        RoundedCornerShape(14.dp)
                    )
                    .background(if (isCancelPressed) Color.White.copy(alpha = 0.05f) else Color.Transparent)
                    .clickable(
                        interactionSource = cancelInteractionSource,
                        indication = ripple(),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onDismiss()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(Res.string.cancel_button),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.Gray
                )
            }
        }
    }

    if (imageUriToCrop != null) {
        ImageCropSheet(
            imageUri = imageUriToCrop!!,
            onResult = { croppedUri ->
                editImage = croppedUri
                imageUriToCrop = null
            },
            onDismiss = { imageUriToCrop = null }
        )
    }
}
