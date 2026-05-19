package com.example.speechkids.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.speechkids.theme.*

// ── Stat card ──────────────────────────────────────────────────
@Composable
fun StatCard(
    value: String,
    label: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = MaterialTheme.shapes.medium,
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        border   = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineLarge, color = valueColor, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

// ── Progress row ───────────────────────────────────────────────
@Composable
fun ProgressRow(label: String, progress: Float, color: Color = Blue600) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, color = color)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
            color    = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

// ── Avatar circle ──────────────────────────────────────────────
@Composable
fun AvatarCircle(
    initials: String,
    size: Dp = 44.dp,
    bgColor: Color = Blue100,
    textColor: Color = Blue600
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initials,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Badge pill ─────────────────────────────────────────────────
@Composable
fun BadgePill(
    text: String,
    bgColor: Color = Blue100,
    textColor: Color = Blue600
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall, color = textColor, fontWeight = FontWeight.Medium)
    }
}

// ── Section card wrapper ───────────────────────────────────────
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = MaterialTheme.shapes.large,
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// ── Phoneme chip ───────────────────────────────────────────────
enum class PhonemeStatus { CORRECT, PARTIAL, WRONG }

@Composable
fun PhonemeChip(phoneme: String, status: PhonemeStatus) {
    val (bg, fg, suffix) = when (status) {
        PhonemeStatus.CORRECT -> Triple(Green100, Green600, " ✓")
        PhonemeStatus.PARTIAL -> Triple(Amber100, Amber600, " ~")
        PhonemeStatus.WRONG   -> Triple(Red100,   Red600,   " ✗")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(phoneme + suffix, style = MaterialTheme.typography.labelLarge, color = fg)
    }
}

// ── Pulsing mic button ─────────────────────────────────────────
@Composable
fun PulsingMicButton(isRecording: Boolean, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = if (isRecording) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue  = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring"
    )

    Box(contentAlignment = Alignment.Center) {
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Blue600.copy(alpha = ringAlpha))
            )
        }
        Button(
            onClick  = onClick,
            modifier = Modifier.size(76.dp),
            shape    = CircleShape,
            colors   = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) Red600 else Blue600
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(if (isRecording) "⏹" else "🎙", fontSize = 28.sp)
        }
    }
}

// ── XP bar ────────────────────────────────────────────────────
@Composable
fun XpBar(currentXp: Int, maxXp: Int, level: Int) {
    val progress = (currentXp.toFloat() / maxXp).coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgePill("Уровень $level", Purple100, Purple600)
            Text("$currentXp / $maxXp XP", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
            color    = Purple600,
            trackColor = Purple100,
        )
    }
}

// ── Primary action button ──────────────────────────────────────
@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick   = onClick,
        modifier  = modifier.fillMaxWidth().height(52.dp),
        shape     = MaterialTheme.shapes.medium,
        colors    = ButtonDefaults.buttonColors(containerColor = Blue600)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

// ── Top bar ───────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechKidsTopBar(title: String, onBack: (() -> Unit)? = null) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Text("←", fontSize = 20.sp)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
