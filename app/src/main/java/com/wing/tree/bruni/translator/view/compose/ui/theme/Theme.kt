package com.wing.tree.bruni.translator.view.compose.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF76D1FF),
    onPrimary = Color(0xFF003548),
    primaryContainer = Color(0xFF004D67),
    onPrimaryContainer = Color(0xFFC2E8FF),
    inversePrimary = Color(0xFF006688),
    secondary = Color(0xFFB5C9D7),
    onSecondary = Color(0xFF20333D),
    secondaryContainer = Color(0xFF364954),
    onSecondaryContainer = Color(0xFFD1E5F3),
    tertiary = Color(0xFFC9C1EA),
    onTertiary = Color(0xFF312C4C),
    tertiaryContainer = Color(0xFF484364),
    onTertiaryContainer = Color(0xFFE5DEFF),
    background = Color(0xFF191C1E),
    onBackground = Color(0xFFE1E2E5),
    surface = Color(0xFF191C1E),
    onSurface = Color(0xFFC5C6C9),
    surfaceVariant = Color(0xFF40484D),
    onSurfaceVariant = Color(0xFFC0C7CD),
    surfaceTint = Color(0xFF76D1FF),
    inverseSurface = Color(0xFFE1E2E5),
    inverseOnSurface = Color(0xFF191C1E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = Color(0xFF8A9297),
    outlineVariant = Color(0xFF40484D),
    scrim = Color(0x000000)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006688),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC2E8FF),
    onPrimaryContainer = Color(0xFF001E2B),
    inversePrimary = Color(0xFF76D1FF),
    secondary = Color(0xFF4E616D),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1E5F3),
    onSecondaryContainer = Color(0xFF091E28),
    tertiary = Color(0xFF5F5A7D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE5DEFF),
    onTertiaryContainer = Color(0xFF1C1736),
    background = Color(0xFFFBFCFE),
    onBackground = Color(0xFF191C1E),
    surface = Color(0xFFFBFCFE),
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFDCE3E9),
    onSurfaceVariant = Color(0xFF40484D),
    surfaceTint = Color(0xFF006688),
    inverseSurface = Color(0xFF2E3133),
    inverseOnSurface = Color(0xFFF0F1F3),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Color(0xFF71787D),
    outlineVariant = Color(0xFFC0C7CD),
    scrim = Color(0x000000)
)

@Composable
fun TranslatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}