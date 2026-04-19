package com.example.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = AppDarkBackground,
    surface = AppDarkBackground
    // Define primary, secondary, tertiary here when needed
)

@Composable
fun SampleMVITheme(
    content: @Composable () -> Unit
) {
    // For now, we enforce dark theme.
    // In the future, you can dynamically switch between light and dark by checking isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
