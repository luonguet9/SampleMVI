package com.example.core.domain.repository

import kotlinx.coroutines.flow.Flow
import com.example.core.domain.result.DomainResult

interface AuthRepository {
    val isUserLoggedIn: Flow<Boolean>
    suspend fun signInWithGoogle(idToken: String): DomainResult<Unit>
    suspend fun signInWithEmail(email: String, password: String): DomainResult<Unit>
    suspend fun signUpWithEmail(email: String, password: String): DomainResult<Unit>
    suspend fun sendPasswordResetEmail(email: String): DomainResult<Unit>
    suspend fun signOut()
    fun getCurrentUserId(): String?
}
