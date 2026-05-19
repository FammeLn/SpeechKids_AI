package com.example.speechkids.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Brand colors ──────────────────────────────────────────────
val Blue600   = Color(0xFF2D6CDF)
val Blue100   = Color(0xFFDBEAFE)
val Blue900   = Color(0xFF1E3A6E)

val Green600  = Color(0xFF16A34A)
val Green100  = Color(0xFFDCFCE7)

val Amber600  = Color(0xFFD97706)
val Amber100  = Color(0xFFFEF3C7)

val Red600    = Color(0xFFDC2626)
val Red100    = Color(0xFFFEE2E2)

val Purple600 = Color(0xFF7C3AED)
val Purple100 = Color(0xFFEDE9FE)

val Navy900   = Color(0xFF0D1117)
val Navy800   = Color(0xFF161B22)
val Navy700   = Color(0xFF21262D)
val Navy400   = Color(0xFF30363D)
val Navy200   = Color(0xFF6E7681)
val Navy100   = Color(0xFFE6EDF3)

val Gray50    = Color(0xFFF7F7F9)
val Gray100   = Color(0xFFF0F0F0)
val Gray300   = Color(0xFFE5E7EB)
val Gray500   = Color(0xFF888888)
val Gray900   = Color(0xFF111111)

// ── Light color scheme ─────────────────────────────────────────
private val LightColors = lightColorScheme(
    primary        = Blue600,
    onPrimary      = Color.White,
    primaryContainer    = Blue100,
    onPrimaryContainer  = Blue900,
    secondary      = Green600,
    onSecondary    = Color.White,
    secondaryContainer  = Green100,
    onSecondaryContainer = Color(0xFF14532D),
    tertiary       = Purple600,
    onTertiary     = Color.White,
    tertiaryContainer   = Purple100,
    background     = Gray50,
    onBackground   = Gray900,
    surface        = Color.White,
    onSurface      = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray500,
    error          = Red600,
    onError        = Color.White,
    errorContainer = Red100,
    outline        = Gray300,
)

// ── Dark color scheme ──────────────────────────────────────────
private val DarkColors = darkColorScheme(
    primary        = Blue600,
    onPrimary      = Color.White,
    primaryContainer    = Navy800,
    onPrimaryContainer  = Navy100,
    secondary      = Green600,
    onSecondary    = Color.White,
    background     = Navy900,
    onBackground   = Navy100,
    surface        = Navy800,
    onSurface      = Navy100,
    surfaceVariant = Navy700,
    onSurfaceVariant = Navy200,
    outline        = Navy400,
)

// ── Typography ─────────────────────────────────────────────────
val SpeechKidsTypography = Typography(
    displayLarge  = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold,   lineHeight = 40.sp),
    headlineLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold, lineHeight = 32.sp),
    headlineMedium= TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium,  lineHeight = 24.sp),
    titleLarge    = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium,  lineHeight = 26.sp),
    titleMedium   = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium,  lineHeight = 22.sp),
    titleSmall    = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium,  lineHeight = 20.sp),
    bodyLarge     = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal,  lineHeight = 23.sp),
    bodyMedium    = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal,  lineHeight = 20.sp),
    bodySmall     = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal,  lineHeight = 17.sp),
    labelLarge    = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold,lineHeight = 18.sp),
    labelSmall    = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium,  lineHeight = 15.sp),
)

// ── Shapes ─────────────────────────────────────────────────────
val SpeechKidsShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
    small      = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
    medium     = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    large      = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
)

// ── App theme ──────────────────────────────────────────────────
@Composable
fun SpeechKidsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography  = SpeechKidsTypography,
        shapes      = SpeechKidsShapes,
        content     = content
    )
}
