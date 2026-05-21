package com.turnforge

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.turnforge.auth.GoogleAuthManager
import com.turnforge.backup.BackupManager
import com.turnforge.repository.TurnRepository
import com.turnforge.viewmodel.AndroidTurnViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val turnRepository: TurnRepository by inject()
        val turnViewModel: AndroidTurnViewModel by viewModel()

        val authManager = GoogleAuthManager(this)
        val backupManager = BackupManager(turnRepository, this)
        val prefs = getSharedPreferences("backup_prefs", MODE_PRIVATE)

        val driveAuthLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    lifecycleScope.launch {
                        val token = authManager.authorizeDrive()
                        if (token != null && token != "PENDING_RESOLUTION") {
                            Toast.makeText(
                                this@MainActivity,
                                "Login realizado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Erro ao realizar login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Erro ao realizar login", Toast.LENGTH_SHORT).show()
                }
            }

        val signInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val intent = result.data
                lifecycleScope.launch {
                    if (authManager.handleSignInResult(intent)) {
                        val requestedScopes =
                            listOf(com.google.android.gms.common.api.Scope(com.google.api.services.drive.DriveScopes.DRIVE_APPDATA))
                        val authRequest =
                            com.google.android.gms.auth.api.identity.AuthorizationRequest.builder()
                                .setRequestedScopes(requestedScopes)
                                .build()

                        authManager.getDriveAuthorizationClient()
                            .authorize(authRequest)
                            .addOnSuccessListener { authResult ->
                                if (authResult.hasResolution()) {
                                    try {
                                        val intentSenderRequest =
                                            androidx.activity.result.IntentSenderRequest.Builder(
                                                authResult.pendingIntent!!
                                            ).build()
                                        driveAuthLauncher.launch(intentSenderRequest)
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Erro ao realizar login",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Login realizado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Erro ao realizar login",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Erro ao realizar login",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        setContent {
            val scope = rememberCoroutineScope()
            val userEmail by authManager.userEmail.collectAsState()

            var lastBackupDate by remember {
                mutableStateOf(prefs.getString("last_backup", null))
            }

            // pass the platform-aware driver to the common App composable
            App(
                driver = turnViewModel,
                userEmail = userEmail,
                lastBackupDate = lastBackupDate,
                onLogin = {
                    signInLauncher.launch(authManager.getSignInIntent())
                },
                onLogout = {
                    authManager.signOut()
                    Toast.makeText(this, "Sessão encerrada com sucesso", Toast.LENGTH_SHORT).show()
                },
                onBackup = { _ ->
                    scope.launch {
                        val token = authManager.accessToken.value
                        val success = if (token != null && token != "PENDING_RESOLUTION") {
                            backupManager.performBackup(token)
                        } else {
                            val newToken = authManager.authorizeDrive()
                            if (newToken != null && newToken != "PENDING_RESOLUTION") {
                                backupManager.performBackup(newToken)
                            } else false
                        }

                        if (success) {
                            val now = java.text.SimpleDateFormat(
                                "dd/MM/yyyy HH:mm",
                                java.util.Locale.getDefault()
                            ).format(java.util.Date())
                            prefs.edit { putString("last_backup", now) }
                            lastBackupDate = now
                            Toast.makeText(
                                this@MainActivity,
                                "Backup concluído com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (authManager.accessToken.value == "PENDING_RESOLUTION") {
                            // Silencioso
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Erro ao realizar backup",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                onRestore = { _ ->
                    scope.launch {
                        val token = authManager.accessToken.value
                        val success = if (token != null && token != "PENDING_RESOLUTION") {
                            backupManager.performRestore(token)
                        } else {
                            val newToken = authManager.authorizeDrive()
                            if (newToken != null && newToken != "PENDING_RESOLUTION") {
                                backupManager.performRestore(newToken)
                            } else false
                        }

                        if (success) {
                            Toast.makeText(
                                this@MainActivity,
                                "Dados restaurados com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (authManager.accessToken.value == "PENDING_RESOLUTION") {
                            // Silencioso
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Erro ao restaurar dados",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
