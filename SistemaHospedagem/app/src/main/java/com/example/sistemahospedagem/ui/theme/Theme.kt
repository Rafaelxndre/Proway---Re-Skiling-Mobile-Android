package com.example.sistemahospedagem.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD7AC92),
    onPrimary = Color(0xFF3A1F12),
    secondary = Color(0xFFA8B98A),
    onSecondary = Color.White,
    tertiary = Color(0xFFD0B182),
    onTertiary = Color(0xFF3D2407),
    background = NightBackground,
    onBackground = NightText,
    surface = NightSurface,
    onSurface = NightText,
    surfaceVariant = Color(0xFF141414),
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = Color(0xFF5E5E5E)
)

private val LightColorScheme = lightColorScheme(
    primary = WoodRose,
    onPrimary = Color.White,
    secondary = OlivePine,
    onSecondary = Color.White,
    tertiary = AmberFire,
    onTertiary = Color.White,
    background = Cream,
    onBackground = Walnut,
    surface = Linen,
    onSurface = Walnut,
    surfaceVariant = Sand,
    onSurfaceVariant = Mocha,
    outline = Color(0xFFB09B88)
)

@Composable
fun SistemaHospedagemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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