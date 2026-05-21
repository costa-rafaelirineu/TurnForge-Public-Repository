package com.turnforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import turnforge.composeapp.generated.resources.Res
import turnforge.composeapp.generated.resources.backup_convite
import turnforge.composeapp.generated.resources.backup_fazer_agora
import turnforge.composeapp.generated.resources.backup_logado
import turnforge.composeapp.generated.resources.backup_nenhum
import turnforge.composeapp.generated.resources.backup_restaurar
import turnforge.composeapp.generated.resources.backup_sair
import turnforge.composeapp.generated.resources.backup_titulo
import turnforge.composeapp.generated.resources.backup_ultimo
import turnforge.composeapp.generated.resources.fechar_button
import turnforge.composeapp.generated.resources.links_uteis_titulo
import turnforge.composeapp.generated.resources.login_google_button
import turnforge.composeapp.generated.resources.patreon_label
import turnforge.composeapp.generated.resources.play_store_label
import turnforge.composeapp.generated.resources.privacy_policy_label
import turnforge.composeapp.generated.resources.sobre_titulo
import turnforge.composeapp.generated.resources.sugestoes_label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSheet(
    onDismiss: () -> Unit,
    userEmail: String? = null,
    lastBackupDate: String? = null,
    onLogin: () -> Unit = {},
    onLogout: () -> Unit = {},
    onBackup: (suspend () -> Boolean) -> Unit = {},
    onRestore: (suspend () -> Boolean) -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(
                stringResource(Res.string.sobre_titulo),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Backup & Cloud Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        stringResource(Res.string.backup_titulo),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (userEmail == null) {
                        Text(
                            stringResource(Res.string.backup_convite),
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        LinkButton(
                            label = stringResource(Res.string.login_google_button),
                            onClick = {
                                onLogin()
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(
                                    0xFF3B82F6
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    } else {
                        Text(
                            stringResource(Res.string.backup_logado, userEmail),
                            fontSize = 13.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(
                                Res.string.backup_ultimo,
                                lastBackupDate ?: stringResource(Res.string.backup_nenhum)
                            ),
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LinkButton(
                                label = stringResource(Res.string.backup_fazer_agora),
                                onClick = {
                                    scope.launch {
                                        onBackup {
                                            // Este callback recebe o token e executa o backup
                                            // A lógica real está na MainActivity
                                            true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(
                                        0xFF2DD4BF
                                    )
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            LinkButton(
                                label = stringResource(Res.string.backup_restaurar),
                                onClick = {
                                    scope.launch {
                                        onRestore {
                                            true
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                shape = RoundedCornerShape(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinkButton(
                            label = stringResource(Res.string.backup_sair),
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth().height(36.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red.copy(
                                    alpha = 0.7f
                                )
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Links Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(
                        1.dp,
                        Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        stringResource(Res.string.links_uteis_titulo),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val linkModifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)

                    val linkButtonColors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White.copy(alpha = 0.9f)
                    )

                    val linkShape = RoundedCornerShape(14.dp)

                    // Play Store (Highlighted)
                    LinkButton(
                        label = stringResource(Res.string.play_store_label),
                        onClick = { scope.launch { uriHandler.openUri("https://play.google.com/store/apps/details?id=com.turnforge") } },
                        modifier = linkModifier,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFACC15)
                        ),
                        shape = linkShape,
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            Brush.horizontalGradient(listOf(Color(0xFFFACC15), Color(0xFFFB923C)))
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Patreon (Highlighted)
                    LinkButton(
                        label = stringResource(Res.string.patreon_label),
                        onClick = { scope.launch { uriHandler.openUri("https://patreon.com/RafaelIrineuAndroidEngineer?utm_medium=unknown&utm_source=join_link&utm_campaign=creatorshare_creator&utm_content=copyLink") } },
                        modifier = linkModifier,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF424D)
                        ),
                        shape = linkShape,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFFF424D).copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // LinkedIn (Suggestions)
                    LinkButton(
                        label = stringResource(Res.string.sugestoes_label),
                        onClick = { scope.launch { uriHandler.openUri("https://www.linkedin.com/in/rafael-irineu") } },
                        modifier = linkModifier,
                        colors = linkButtonColors,
                        shape = linkShape
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Privacy Policy
                    LinkButton(
                        label = stringResource(Res.string.privacy_policy_label),
                        onClick = { scope.launch { uriHandler.openUri("https://github.com/rafaelineu/turnforge/blob/main/PRIVACY.md") } },
                        modifier = linkModifier,
                        colors = linkButtonColors,
                        shape = linkShape
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Close Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
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
                        stringResource(Res.string.fechar_button),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LinkButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    colors: androidx.compose.material3.ButtonColors,
    shape: RoundedCornerShape,
    border: androidx.compose.foundation.BorderStroke? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        colors = colors,
        border = border ?: androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.1f)
        )
    ) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
