package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val BotanicalDarkColorScheme = darkColorScheme(
  primary = BotanicalPrimaryDark,
  onPrimary = BotanicalOnPrimaryDark,
  primaryContainer = BotanicalPrimaryContainerDark,
  onPrimaryContainer = BotanicalOnPrimaryContainerDark,
  secondary = BotanicalSecondaryDark,
  onSecondary = BotanicalOnSecondaryDark,
  secondaryContainer = BotanicalSecondaryContainerDark,
  onSecondaryContainer = BotanicalOnSecondaryContainerDark,
  tertiary = BotanicalTertiaryDark,
  onTertiary = BotanicalOnTertiaryDark,
  tertiaryContainer = BotanicalTertiaryContainerDark,
  onTertiaryContainer = BotanicalOnTertiaryContainerDark,
  error = BotanicalErrorDark,
  onError = BotanicalOnErrorDark,
  errorContainer = BotanicalErrorContainerDark,
  onErrorContainer = BotanicalOnErrorContainerDark,
  background = BotanicalBackgroundDark,
  onBackground = BotanicalOnBackgroundDark,
  surface = BotanicalSurfaceDark,
  onSurface = BotanicalOnSurfaceDark,
  surfaceVariant = BotanicalSurfaceVariantDark,
  onSurfaceVariant = BotanicalOnSurfaceVariantDark,
  surfaceContainerLowest = BotanicalSurfaceContainerLowestDark,
  surfaceContainerLow = BotanicalSurfaceContainerLowDark,
  surfaceContainer = BotanicalSurfaceContainerDark,
  surfaceContainerHigh = BotanicalSurfaceContainerHighDark,
  surfaceContainerHighest = BotanicalSurfaceContainerHighestDark,
  outline = BotanicalOutlineDark,
  outlineVariant = BotanicalOutlineVariantDark,
  inverseSurface = BotanicalInverseSurfaceDark,
  inverseOnSurface = BotanicalInverseOnSurfaceDark,
  inversePrimary = BotanicalInversePrimaryDark,
)

private val BotanicalLightColorScheme = lightColorScheme(
  primary = BotanicalPrimaryLight,
  onPrimary = BotanicalOnPrimaryLight,
  primaryContainer = BotanicalPrimaryContainerLight,
  onPrimaryContainer = BotanicalOnPrimaryContainerLight,
  secondary = BotanicalSecondaryLight,
  onSecondary = BotanicalOnSecondaryLight,
  secondaryContainer = BotanicalSecondaryContainerLight,
  onSecondaryContainer = BotanicalOnSecondaryContainerLight,
  tertiary = BotanicalTertiaryLight,
  onTertiary = BotanicalOnTertiaryLight,
  tertiaryContainer = BotanicalTertiaryContainerLight,
  onTertiaryContainer = BotanicalOnTertiaryContainerLight,
  error = BotanicalErrorLight,
  onError = BotanicalOnErrorLight,
  errorContainer = BotanicalErrorContainerLight,
  onErrorContainer = BotanicalOnErrorContainerLight,
  background = BotanicalBackgroundLight,
  onBackground = BotanicalOnBackgroundLight,
  surface = BotanicalSurfaceLight,
  onSurface = BotanicalOnSurfaceLight,
  surfaceVariant = BotanicalSurfaceVariantLight,
  onSurfaceVariant = BotanicalOnSurfaceVariantLight,
  surfaceContainerLowest = BotanicalSurfaceContainerLowestLight,
  surfaceContainerLow = BotanicalSurfaceContainerLowLight,
  surfaceContainer = BotanicalSurfaceContainerLight,
  surfaceContainerHigh = BotanicalSurfaceContainerHighLight,
  surfaceContainerHighest = BotanicalSurfaceContainerHighestLight,
  outline = BotanicalOutlineLight,
  outlineVariant = BotanicalOutlineVariantLight,
  inverseSurface = BotanicalInverseSurfaceLight,
  inverseOnSurface = BotanicalInverseOnSurfaceLight,
  inversePrimary = BotanicalInversePrimaryLight,
)

@Composable
fun HealthMateTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> BotanicalDarkColorScheme
      else -> BotanicalLightColorScheme
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    shapes = BotanicalShapes,
    content = content,
  )
}
