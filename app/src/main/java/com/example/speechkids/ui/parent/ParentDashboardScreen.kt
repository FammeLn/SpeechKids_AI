package com.example.speechkids.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.speechkids.components.*
import com.example.speechkids.theme.*

// ── Mock data ─────────────────────────────────────────────────
data class ChildSummary(
    val id: String, val name: String, val age: Int,
    val streak: Int, val accuracy: Float, val initials: String,
    val todayXp: Int, val pendingExercises: Int
)

private val mockChildren = listOf(
    ChildSummary("1", "Алина",  5, 3, 0.82f, "АЛ", 47, 2),
    ChildSummary("2", "Максим", 7, 0, 0.64f, "МК", 0,  3),
)

data class NotificationItem(val text: String, val isAlert: Boolean, val time: String)
private val mockNotifications = listOf(
    NotificationItem("Алина завершила «Скороговорки»", false, "2 ч назад"),
    NotificationItem("Регресс у Максима — звук «Р»", true, "вчера"),
)

// ── Screen ────────────────────────────────────────────────────
@Composable
fun ParentDashboardScreen(
    onChildClick: (String) -> Unit,
    onTherapistClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("SpeechKids", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = onTherapistClick)  { Text("Логопед") }
                    TextButton(onClick = onAdminClick)      { Text("Админ") }
                    IconButton(onClick = onNotificationsClick) { Text("🔔", fontSize = 20.sp) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Greeting
            item {
                Text("Добрый день, Аиша 👋", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                Text("Сегодня вторник, 20 мая", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Global stats
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("2",   "Детей",      modifier = Modifier.weight(1f))
                    StatCard("3🔥", "Макс. стрик",modifier = Modifier.weight(1f))
                    StatCard("47",  "XP сегодня", valueColor = Purple600, modifier = Modifier.weight(1f))
                }
            }

            // Children cards
            item {
                Text("Профили детей", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            items(mockChildren) { child ->
                ChildCard(child = child, onClick = { onChildClick(child.id) })
            }

            // Notifications
            item {
                Text("Уведомления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            items(mockNotifications) { notif ->
                NotifCard(notif)
            }
        }
    }
}

@Composable
private fun ChildCard(child: ChildSummary, onClick: () -> Unit) {
    SectionCard(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarCircle(child.initials, size = 52.dp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(child.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${child.age} лет", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                if (child.streak > 0)
                    BadgePill("${child.streak} дней 🔥", Amber100, Amber600)
                Spacer(Modifier.height(4.dp))
                Text("${child.pendingExercises} задан.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(Modifier.height(14.dp))
        ProgressRow("Точность речи", child.accuracy)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BadgePill("+${child.todayXp} XP сегодня", Purple100, Purple600)
        }
    }
}

@Composable
private fun NotifCard(notif: NotificationItem) {
    val bgColor = if (notif.isAlert) Red100 else MaterialTheme.colorScheme.surface
    val borderColor = if (notif.isAlert) Red600.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = MaterialTheme.shapes.medium,
        colors    = CardDefaults.cardColors(containerColor = bgColor),
        border    = androidx.compose.foundation.BorderStroke(0.5.dp, borderColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (notif.isAlert) "⚠️" else "✅", fontSize = 20.sp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notif.text, style = MaterialTheme.typography.bodyMedium)
                Text(notif.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
