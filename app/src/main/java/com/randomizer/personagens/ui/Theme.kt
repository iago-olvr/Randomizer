package com.randomizer.personagens.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.randomizer.personagens.design.MedievalColors

private val CharacterColors = lightColorScheme(
    primary = Color(MedievalColors.Wine),
    onPrimary = Color(MedievalColors.Paper),
    primaryContainer = Color(MedievalColors.Wine),
    onPrimaryContainer = Color(MedievalColors.Paper),
    secondary = Color(MedievalColors.Wine),
    onSecondary = Color(MedievalColors.Paper),
    secondaryContainer = Color(MedievalColors.Rose),
    onSecondaryContainer = Color(MedievalColors.Wine),
    background = Color(MedievalColors.Parchment),
    onBackground = Color(MedievalColors.Ink),
    surface = Color(MedievalColors.Paper),
    onSurface = Color(MedievalColors.Ink),
    surfaceVariant = Color(MedievalColors.Rose),
    onSurfaceVariant = Color(MedievalColors.FadedInk),
    outline = Color(MedievalColors.Bronze),
    outlineVariant = Color(MedievalColors.Bronze).copy(alpha = 0.4f),
    error = Color(0xFF8B2621),
)

private val BaseType = Typography()
private val ChronicleType = Typography(
    displayLarge = BaseType.displayLarge.copy(fontFamily = FontFamily.Serif),
    displayMedium = BaseType.displayMedium.copy(fontFamily = FontFamily.Serif),
    displaySmall = BaseType.displaySmall.copy(fontFamily = FontFamily.Serif),
    headlineLarge = BaseType.headlineLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
    headlineMedium = BaseType.headlineMedium.copy(fontFamily = FontFamily.Serif),
    headlineSmall = BaseType.headlineSmall.copy(fontFamily = FontFamily.Serif),
    titleLarge = BaseType.titleLarge.copy(fontFamily = FontFamily.Serif),
    titleMedium = BaseType.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
    titleSmall = BaseType.titleSmall.copy(fontFamily = FontFamily.Serif),
    labelLarge = BaseType.labelLarge.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
    labelMedium = BaseType.labelMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
    labelSmall = BaseType.labelSmall.copy(fontFamily = FontFamily.Serif),
)

@Composable
fun RandomizerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CharacterColors, typography = ChronicleType,
        shapes = Shapes(
            small = CutCornerShape(4.dp), medium = CutCornerShape(8.dp),
            large = CutCornerShape(12.dp), extraLarge = CutCornerShape(16.dp),
        ), content = content)
}
