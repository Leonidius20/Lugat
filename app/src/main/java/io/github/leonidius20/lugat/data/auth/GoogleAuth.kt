package io.github.leonidius20.lugat.data.auth

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.leonidius20.lugat.R
import io.github.leonidius20.lugat.domain.entities.User
import io.github.leonidius20.lugat.domain.repository.auth.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Singleton
class GoogleAuth @Inject constructor(
    @ApplicationContext private val context: Context,
): AuthRepository {

    fun isUserLoggedIn() = Firebase.auth.currentUser != null

    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                this.trySend(auth.currentUser?.let {
                    User(
                        id = it.uid,
                        name = it.displayName ?: "Unknown name",
                        email = it.email ?: "Unknown email",
                        pfpUrl = it.photoUrl?.toString(),
                    )
                })
            }

            Firebase.auth.addAuthStateListener(listener)

            // remove listener when consumer unsubscribes from this flow (or if calling channel.close())
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }


    override suspend fun signInWithGoogle(credential: Credential) {
        val firebaseAuth = FirebaseAuth.getInstance()

        //Firebase.auth.signInWithCredential()

        try {


            // Check if the received credential is a valid Google ID Token
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential =
                    GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                //_state.value = LoginState.LoggedIn(authResult)
            } else {
                Log.e("auth", "got invalid cred type")
                throw RuntimeException("Received an invalid credential type")
            }
        } catch (e: GetCredentialCancellationException) {
            //_state.value = LoginState.Error(Exception("Sign-in was canceled. Please try again."))
            Log.e("auth", "sign in was cancelled", e)
        } catch (e: Exception) {
            //_state.value = LoginState.Error(e)
            Log.e("auth", e.message, e)
        }

    }

    override fun signOut() {
        Firebase.auth.signOut()
    }

}