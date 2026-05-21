package com.turnforge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.turnforge.model.ArchivedHistory
import com.turnforge.model.Turn
import com.turnforge.model.combat.CombatAction
import com.turnforge.viewmodel.TurnViewModelDriver
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import turnforge.composeapp.generated.resources.Res
import turnforge.composeapp.generated.resources.acertou_msg
import turnforge.composeapp.generated.resources.archive_summary
import turnforge.composeapp.generated.resources.arquivados_tab
import turnforge.composeapp.generated.resources.arquivar_button
import turnforge.composeapp.generated.resources.arquivar_dialog_msg
import turnforge.composeapp.generated.resources.arquivar_dialog_title
import turnforge.composeapp.generated.resources.ataque_history_log
import turnforge.composeapp.generated.resources.atual_tab
import turnforge.composeapp.generated.resources.cancel_button
import turnforge.composeapp.generated.resources.dados_history_log
import turnforge.composeapp.generated.resources.defesa_history_log
import turnforge.composeapp.generated.resources.errou_msg
import turnforge.composeapp.generated.resources.historico_titulo
import turnforge.composeapp.generated.resources.hp_history_log
import turnforge.composeapp.generated.resources.limpar_arquivados_button
import turnforge.composeapp.generated.resources.limpar_arquivados_dialog_msg
import turnforge.composeapp.generated.resources.limpar_arquivados_dialog_title
import turnforge.composeapp.generated.resources.limpar_dialog_msg
import turnforge.composeapp.generated.resources.limpar_dialog_title
import turnforge.composeapp.generated.resources.limpar_historico_button
import turnforge.composeapp.generated.resources.magia_history_log
import turnforge.composeapp.generated.resources.mana_history_log
import turnforge.composeapp.generated.resources.mana_infinito
import turnforge.composeapp.generated.resources.mana_label
import turnforge.composeapp.generated.resources.nenhum_arquivado_msg
import turnforge.composeapp.generated.resources.nenhum_turno_msg
import turnforge.composeapp.generated.resources.turno_label
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    driver: TurnViewModelDriver,
    onClear: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val turns by driver.turns.collectAsState()
    val archivedHistories by driver.archivedHistories.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentView by remember { mutableStateOf("ATUAL") }
    var showArchiveDialog by remember { mutableStateOf(false) }
    var showClearCurrentDialog by remember { mutableStateOf(false) }
    var showClearArchivedDialog by remember { mutableStateOf(false) }
    var expandedArchives by remember { mutableStateOf(setOf<String>()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 24.dp)
        ) {
            Text(
                stringResource(Res.string.historico_titulo),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botões ATUAL e ARQUIVADOS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { currentView = "ATUAL" },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (currentView == "ATUAL")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        containerColor = if (currentView == "ATUAL")
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentView == "ATUAL")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        stringResource(Res.string.atual_tab),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                OutlinedButton(
                    onClick = { currentView = "ARQUIVADOS" },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (currentView == "ARQUIVADOS")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        containerColor = if (currentView == "ARQUIVADOS")
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentView == "ARQUIVADOS")
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        stringResource(Res.string.arquivados_tab),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conteúdo dinâmico baseado na visualização atual
            Box(modifier = Modifier.weight(1f)) {
                when (currentView) {
                    "ATUAL" -> {
                        if (turns.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(Res.string.nenhum_turno_msg),
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(turns.asReversed(), key = { it.id }) { turn ->
                                    TurnHistoryItem(turn)
                                }
                            }
                        }
                    }

                    "ARQUIVADOS" -> {
                        if (archivedHistories.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    stringResource(Res.string.nenhum_arquivado_msg),
                                    color = Color.Gray
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(archivedHistories.asReversed(), key = { it.id }) { archive ->
                                    ArchivedHistoryItem(
                                        archive = archive,
                                        isExpanded = archive.id in expandedArchives,
                                        onToggleExpand = {
                                            expandedArchives = if (archive.id in expandedArchives) {
                                                expandedArchives - archive.id
                                            } else {
                                                expandedArchives + archive.id
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botões de ação baseados na visualização atual
            when (currentView) {
                "ATUAL" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showArchiveDialog = true },
                            enabled = turns.isNotEmpty(),
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary,
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.3f
                                ),
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                    alpha = 0.1f
                                )
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (turns.isNotEmpty())
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                stringResource(Res.string.arquivar_button),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showClearCurrentDialog = true },
                            enabled = turns.isNotEmpty(),
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                                containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.3f
                                ),
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                    alpha = 0.1f
                                )
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (turns.isNotEmpty())
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                            )
                        ) {
                            Text(
                                stringResource(Res.string.limpar_historico_button),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                "ARQUIVADOS" -> {
                    OutlinedButton(
                        onClick = { showClearArchivedDialog = true },
                        enabled = archivedHistories.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.3f
                            ),
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                alpha = 0.1f
                            )
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (archivedHistories.isNotEmpty())
                                MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            stringResource(Res.string.limpar_arquivados_button),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmação para arquivar histórico
    if (showArchiveDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveDialog = false },
            title = { Text(stringResource(Res.string.arquivar_dialog_title)) },
            text = { Text(stringResource(Res.string.arquivar_dialog_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val archiveName = "Histórico ${archivedHistories.size + 1}"
                        driver.archiveHistory(archiveName)
                        onClear()
                        showArchiveDialog = false
                    }
                ) {
                    Text(
                        stringResource(Res.string.arquivar_button),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showArchiveDialog = false }) {
                    Text(stringResource(Res.string.cancel_button))
                }
            }
        )
    }

    // Diálogo de confirmação para limpar histórico atual
    if (showClearCurrentDialog) {
        AlertDialog(
            onDismissRequest = { showClearCurrentDialog = false },
            title = { Text(stringResource(Res.string.limpar_dialog_title)) },
            text = { Text(stringResource(Res.string.limpar_dialog_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        driver.clear()
                        onClear()
                        showClearCurrentDialog = false
                    }
                ) {
                    Text(
                        stringResource(Res.string.limpar_historico_button),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCurrentDialog = false }) {
                    Text(stringResource(Res.string.cancel_button))
                }
            }
        )
    }

    // Diálogo de confirmação para limpar históricos arquivados
    if (showClearArchivedDialog) {
        AlertDialog(
            onDismissRequest = { showClearArchivedDialog = false },
            title = { Text(stringResource(Res.string.limpar_arquivados_dialog_title)) },
            text = { Text(stringResource(Res.string.limpar_arquivados_dialog_msg)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        driver.clearArchivedHistories()
                        showClearArchivedDialog = false
                    }
                ) {
                    Text(
                        stringResource(Res.string.limpar_historico_button),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearArchivedDialog = false }) {
                    Text(stringResource(Res.string.cancel_button))
                }
            }
        )
    }
}

@Composable
fun TurnHistoryItem(turn: Turn) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            )
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                stringResource(Res.string.turno_label, turn.id.replace("turn_", "")),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            turn.actions.forEach { action ->
                val actionText = when (action) {
                    is CombatAction.Attack -> stringResource(
                        Res.string.ataque_history_log,
                        action.weaponName,
                        if (action.hit) stringResource(Res.string.acertou_msg) else stringResource(
                            Res.string.errou_msg
                        ),
                        action.rollValue,
                        action.damage
                    )

                    is CombatAction.Spell -> {
                        val costText =
                            if (turn.stateAtStart.isInfiniteMana) stringResource(Res.string.mana_infinito) else stringResource(
                                Res.string.mana_label,
                                action.manaCost
                            )
                        stringResource(
                            Res.string.magia_history_log,
                            action.name,
                            action.value,
                            costText
                        )
                    }

                    is CombatAction.Defense -> stringResource(
                        Res.string.defesa_history_log,
                        action.icon,
                        action.name,
                        action.duration
                    )

                    is CombatAction.DiceRoll -> stringResource(
                        Res.string.dados_history_log,
                        action.expression,
                        action.result
                    )
                }

                Text(
                    text = actionText,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    stringResource(
                        Res.string.hp_history_log,
                        turn.stateAtStart.playerHp,
                        turn.stateAtEnd.playerHp
                    ),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (turn.stateAtStart.isInfiniteMana) stringResource(
                        Res.string.mana_history_log,
                        stringResource(Res.string.mana_infinito),
                        stringResource(Res.string.mana_infinito)
                    ) else stringResource(
                        Res.string.mana_history_log,
                        turn.stateAtStart.playerMana.toString(),
                        turn.stateAtEnd.playerMana.toString()
                    ),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ArchivedHistoryItem(
    archive: ArchivedHistory,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header do histórico arquivado (clicável para expandir/retrair)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        archive.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        stringResource(
                            Res.string.archive_summary,
                            archive.turns.size,
                            formatDate(archive.archivedAt)
                        ),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Ícone de expandir/retrair
                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(40.dp)
                ) {
                    Text(
                        text = if (isExpanded) "▼" else "▶",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Conteúdo expandido (turnos do histórico)
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    archive.turns.asReversed().forEach { turn ->
                        TurnHistoryItem(turn)
                    }
                }
            }
        }
    }
}

// Função auxiliar para formatar data
private fun formatDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.day.toString().padStart(2, '0')}/${
        localDateTime.month.number.toString().padStart(2, '0')
    } ${localDateTime.hour.toString().padStart(2, '0')}:${
        localDateTime.minute.toString().padStart(2, '0')
    }"
}
