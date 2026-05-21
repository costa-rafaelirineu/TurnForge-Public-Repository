package com.turnforge.backup

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.gson.Gson
import com.turnforge.model.ArchivedHistory
import com.turnforge.model.Turn
import com.turnforge.repository.TurnRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.InputStreamReader

data class BackupData(
    val turns: List<Turn>,
    val archivedHistories: List<ArchivedHistory>,
    val timestamp: Long = System.currentTimeMillis()
)

class BackupManager(
    private val repository: TurnRepository,
    private val context: android.content.Context
) {
    private val gson = Gson()

    private fun getDriveService(accessToken: String): Drive {
        val credential = GoogleCredential().setAccessToken(accessToken)
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        ).setApplicationName("TurnForge").build()
    }

    suspend fun performBackup(accessToken: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val service = getDriveService(accessToken)

            // 1. Collect Data
            val turns = repository.getTurns()
            val histories = repository.getArchivedHistories()

            val backupData = BackupData(
                turns = turns,
                archivedHistories = histories
            )
            val json = gson.toJson(backupData)

            // 2. Save locally first
            val localFile = java.io.File(context.cacheDir, "backup.json")
            FileOutputStream(localFile).use { it.write(json.toByteArray()) }

            // 3. Search for existing backup
            val existingFiles = service.files().list()
                .setSpaces("appDataFolder")
                .setQ("name = 'backup.json'")
                .execute()
                .files

            val fileMetadata = File().apply {
                name = "backup.json"
                parents = listOf("appDataFolder")
            }
            val mediaContent = FileContent("application/json", localFile)

            if (existingFiles.isNullOrEmpty()) {
                service.files().create(fileMetadata, mediaContent).execute()
            } else {
                val fileId = existingFiles[0].id
                service.files().update(fileId, null, mediaContent).execute()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun performRestore(accessToken: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val service = getDriveService(accessToken)

            val files = service.files().list()
                .setSpaces("appDataFolder")
                .setQ("name = 'backup.json'")
                .execute()
                .files

            if (files.isNullOrEmpty()) {
                return@withContext false
            }

            val fileId = files[0].id
            val inputStream = service.files().get(fileId).executeMediaAsInputStream()

            val backupData = InputStreamReader(inputStream).use {
                gson.fromJson(it, BackupData::class.java)
            }

            // 4. Restore to repository
            repository.importData(backupData.turns, backupData.archivedHistories)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
