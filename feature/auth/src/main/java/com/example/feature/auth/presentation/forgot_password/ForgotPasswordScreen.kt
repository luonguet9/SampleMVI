package com.example.feature.auth.presentation.forgot_password

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ForgotPasswordEffect.NavigateBackToLogin -> onNavigateBack()
                is ForgotPasswordEffect.ShowToast -> {
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
            text = "Reset Password",
            color = Color.White,
            fontSize = 28.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Enter your email address and we will send you a link to reset your password.",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Email field
        OutlinedTextField(
            value = state.emailInput,
            onValueChange = { viewModel.processIntent(ForgotPasswordIntent.EmailChanged(it)) },
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
            Button(
                onClick = { viewModel.processIntent(ForgotPasswordIntent.Submit) },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Send Reset Link", color = Color.Black, modifier = Modifier.padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Navigate Back to Login
        Row {
            Text(text = "Remembered your password? ", color = Color.Gray)
            Text(
                text = "Log in",
                color = Color.White,
                modifier = Modifier.clickable {
                    viewModel.processIntent(ForgotPasswordIntent.GoBackToLogin)
                }
            )
        }
    }
}
