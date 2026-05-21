package com.turnforge.ui.components

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.turnforge.model.combat.Enemy
import org.jetbrains.compose.resources.stringResource
import turnforge.composeapp.generated.resources.Res
import turnforge.composeapp.generated.resources.adicionar_button
import turnforge.composeapp.generated.resources.adicionar_oponente_titulo
import turnforge.composeapp.generated.resources.cancel_button
import turnforge.composeapp.generated.resources.editar_oponente_titulo
import turnforge.composeapp.generated.resources.hp_atual_label
import turnforge.composeapp.generated.resources.hp_maximo_label
import turnforge.composeapp.generated.resources.nome_oponente_label
import turnforge.composeapp.generated.resources.oponente_default_name
import turnforge.composeapp.generated.resources.save_button

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnemyEditSheet(
    enemy: Enemy? = null,
    onSave: (name: String, currentHp: Int, maxHp: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(enemy?.name ?: "") }
    var currentHp by remember { mutableStateOf(enemy?.currentHp?.toString() ?: "100") }
    var maxHp by remember { mutableStateOf(enemy?.maxHp?.toString() ?: "100") }

    val defaultEnemyName = stringResource(Res.string.oponente_default_name)

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
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
                if (enemy == null) stringResource(Res.string.adicionar_oponente_titulo) else stringResource(
                    Res.string.editar_oponente_titulo
                ),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(
                        stringResource(Res.string.nome_oponente_label),
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = currentHp,
                    onValueChange = {
                        if (it.isEmpty() || it.toIntOrNull() != null) currentHp = it
                    },
                    label = {
                        Text(
                            stringResource(Res.string.hp_atual_label),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF66BB6A),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    )
                )
                OutlinedTextField(
                    value = maxHp,
                    onValueChange = { if (it.isEmpty() || it.toIntOrNull() != null) maxHp = it },
                    label = {
                        Text(
                            stringResource(Res.string.hp_maximo_label),
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
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
                        focusedBorderColor = Color(0xFF66BB6A),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                                name.ifEmpty { defaultEnemyName },
                                currentHp.toIntOrNull() ?: 100,
                                maxHp.toIntOrNull() ?: 100
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
                                    Color(0xFF3B82F6),
                                    Color(0xFF2DD4BF)
                                )
                            )
                        )
                        .background(if (isSavePressed) Color.White.copy(alpha = 0.1f) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (enemy == null) stringResource(Res.string.adicionar_button) else stringResource(
                            Res.string.save_button
                        ),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
}
