package com.example.core.data.repository

import com.example.core.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.example.core.util.AppLogger
import com.example.core.domain.result.DomainResult
import com.example.core.domain.exception.DomainException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val isUserLoggedIn: Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        
        // Initial state
        trySend(firebaseAuth.currentUser != null)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): DomainResult<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await()
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e("Google Sign-In failed", e)
            DomainResult.Error(DomainException.UnknownException(e.message ?: "Google Sign-In failed", e))
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): DomainResult<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e("Email Sign-In failed", e)
            DomainResult.Error(DomainException.UnknownException(e.message ?: "Email Sign-In failed", e))
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): DomainResult<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e("Email Sign-Up failed", e)
            DomainResult.Error(DomainException.UnknownException(e.message ?: "Email Sign-Up failed", e))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): DomainResult<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e("Password Reset failed", e)
            DomainResult.Error(DomainException.UnknownException(e.message ?: "Password Reset failed", e))
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}
