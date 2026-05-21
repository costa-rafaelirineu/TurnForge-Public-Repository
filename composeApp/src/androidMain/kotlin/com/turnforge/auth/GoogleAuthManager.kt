package com.turnforge.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) {

    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken

    private val WEB_CLIENT_ID =
        "170678112905-qotfjhhmql1dgh1ql68jgejpgj228esh.apps.googleusercontent.com"

    fun getSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestServerAuthCode(WEB_CLIENT_ID)
            .requestIdToken(WEB_CLIENT_ID)
            .build()

        val client = GoogleSignIn.getClient(context, gso)
        return client.signInIntent
    }

    fun handleSignInResult(intent: Intent?): Boolean {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            _userEmail.value = account.email
            true
        } catch (e: ApiException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun authorizeDrive(): String? {
        val requestedScopes = listOf(Scope(DriveScopes.DRIVE_APPDATA))
        val authRequest = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)
            .build()

        return try {
            val result = Identity.getAuthorizationClient(context)
                .authorize(authRequest)
                .await()

            if (result.hasResolution()) {
                "PENDING_RESOLUTION"
            } else {
                _accessToken.value = result.accessToken
                result.accessToken
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getDriveAuthorizationClient() = Identity.getAuthorizationClient(context)

    fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(context, gso).signOut()
        _userEmail.value = null
        _accessToken.value = null
    }
}
