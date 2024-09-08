package io.github.leonidius20.lugat.features.auth.views

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import io.github.leonidius20.lugat.R
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@ActivityScoped
class CredentialSelector @Inject constructor(
    @ActivityContext private val activityContext: Context,
) {

    /**
     * @throws GetCredentialCancellationException if the user dismisses the credential selector
     * @throws NoCredentialException if there are no matching credentials on device
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun selectCredential(): Credential {
        val credentialManager: CredentialManager = CredentialManager.create(activityContext)

        // Generate a nonce (a random number used once)
        val ranNonce: String = UUID.randomUUID().toString()
        val bytes: ByteArray = ranNonce.toByteArray()
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        val digest: ByteArray = md.digest(bytes)
        val hashedNonce: String = digest.fold("") { str, it -> str + "%02x".format(it) }

        // Set up Google ID option
        val clientId = String(Base64.decode(activityContext.getString(R.string.google_client_id_base64))) // in base64 to avoid scrapping
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            // .setAutoSelectEnabled(true)
            .setServerClientId(clientId) // maybe it should be populated by gms gradle plugin but it is not
            //.setNonce(hashedNonce)
            .build()

        // Request credentials
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // Get the credential result
        val result = credentialManager.getCredential(activityContext, request)
        val credential = result.credential

        return credential
    }

}