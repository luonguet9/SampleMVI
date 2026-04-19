package com.example.feature.auth.presentation.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.core.util.Constants
import com.example.core.util.AppLogger
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToMain -> onNavigateToMain()
                is LoginEffect.NavigateToRegister -> onNavigateToRegister()
                is LoginEffect.NavigateToForgotPassword -> onNavigateToForgotPassword()
                is LoginEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome Back",
            color = Color.White,
            fontSize = 28.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = state.emailInput,
            onValueChange = { viewModel.processIntent(LoginIntent.EmailChanged(it)) },
            label = { Text("Email", color = Color.Gray) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = state.passwordInput,
            onValueChange = { viewModel.processIntent(LoginIntent.PasswordChanged(it)) },
            label = { Text("Password", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Forgot Password text
        Box(
            modifier = Modifier.fillMaxWidth().padding(end = 4.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Forgot Password?",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .clickable { viewModel.processIntent(LoginIntent.GoToForgotPassword) }
            )
        }

        if (state.error != null) {
            Text(
                text = state.error!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (state.isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            // Login with Email Button
            Button(
                onClick = { viewModel.processIntent(LoginIntent.SubmitEmailLogin) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Log In", color = Color.White, modifier = Modifier.padding(8.dp))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "OR", color = Color.Gray)
            
            Spacer(modifier = Modifier.height(16.dp))

            // Google Button
            Button(
                onClick = {
                    AppLogger.d("LoginScreen: User clicked 'Sign in with Google'")
                    coroutineScope.launch {
                        performGoogleSignIn(context, viewModel)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sign in with Google", color = Color.Black, modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigate to Register
        Row {
            Text(text = "Don't have an account? ", color = Color.Gray)
            Text(
                text = "Sign up",
                color = Color.White,
                modifier = Modifier.clickable {
                    viewModel.processIntent(LoginIntent.GoToRegister)
                }
            )
        }
    }
}

private suspend fun performGoogleSignIn(context: Context, viewModel: LoginViewModel) {
    val credentialManager = CredentialManager.create(context)
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(Constants.WEB_CLIENT_ID)
        .setAutoSelectEnabled(false)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    AppLogger.d("performGoogleSignIn: Requesting credential from Google...")
    try {
        val result: GetCredentialResponse = credentialManager.getCredential(
            request = request,
            context = context
        )
        
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            AppLogger.d("performGoogleSignIn: Token received, dispatching Intent...")
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            viewModel.processIntent(LoginIntent.SignInWithGoogle(googleIdTokenCredential.idToken))
        }
    } catch (e: Exception) {
        AppLogger.e("performGoogleSignIn: Exception caught during sign in", e)
        Toast.makeText(context, "Sign-in cancelled or failed.", Toast.LENGTH_SHORT).show()
    }
}
