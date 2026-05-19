package com.example.speechkids.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.speechkids.components.*
import com.example.speechkids.theme.*

// ══════════════════════════════════════════════════════════════
// ChildProfileScreen
// ══════════════════════════════════════════════════════════════
@Composable
fun ChildProfileScreen(
    childId: String,
    onBack: () -> Unit,
    onPhonemeMap: () -> Unit,
    onAssignExercise: () -> Unit,
    onReports: () -> Unit
) {
    Scaffold(
        topBar = { SpeechKidsTopBar("Профиль ребёнка", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AvatarCircle("АЛ", size = 64.dp)
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Алина", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text("5 лет · Русский язык", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                BadgePill("3 дня 🔥", Amber100, Amber600)
                                BadgePill("Уровень 4", Purple100, Purple600)
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    XpBar(currentXp = 340, maxXp = 500, level = 4)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("82%",  "Точность",   valueColor = Green600, modifier = Modifier.weight(1f))
                    StatCard("12",   "Сессий",     modifier = Modifier.weight(1f))
                    StatCard("8 мин","Ср. сессия", modifier = Modifier.weight(1f))
                }
            }
            item {
                SectionCard {
                    Text("Прогресс по звукам", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    ProgressRow("Звук «Р»", 0.68f, Blue600);  Spacer(Modifier.height(10.dp))
                    ProgressRow("Звук «Ш»", 0.91f, Green600); Spacer(Modifier.height(10.dp))
                    ProgressRow("Звук «Л»", 0.75f, Amber600); Spacer(Modifier.height(10.dp))
                    ProgressRow("Звук «С»", 0.55f, Red600)
                }
            }
            item {
                Text("Действия", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionButton("🗺 Фонемная карта",    Blue600,   onPhonemeMap)
                    ActionButton("📋 Назначить задание", Green600,  onAssignExercise)
                    ActionButton("📊 Отчёты",            Purple600, onReports)
                }
            }
        }
    }
}

@Composable
private fun ActionButton(label: String, color: Color, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) { Text(label, style = MaterialTheme.typography.labelLarge) }
}

// ══════════════════════════════════════════════════════════════
// PhonemeMapScreen
// ══════════════════════════════════════════════════════════════
data class PhonemeData(val phoneme: String, val accuracy: Float, val attempts: Int)

private val russianPhonemes = listOf(
    PhonemeData("А", 0.97f, 45), PhonemeData("О", 0.95f, 40), PhonemeData("У", 0.93f, 38),
    PhonemeData("Э", 0.90f, 32), PhonemeData("И", 0.88f, 35), PhonemeData("Б", 0.85f, 28),
    PhonemeData("В", 0.72f, 22), PhonemeData("Г", 0.80f, 20), PhonemeData("Д", 0.78f, 25),
    PhonemeData("Ж", 0.65f, 18), PhonemeData("З", 0.70f, 19), PhonemeData("К", 0.88f, 30),
    PhonemeData("Л", 0.75f, 35), PhonemeData("М", 0.92f, 40), PhonemeData("Н", 0.89f, 38),
    PhonemeData("П", 0.91f, 35), PhonemeData("Р", 0.68f, 42), PhonemeData("С", 0.55f, 38),
    PhonemeData("Т", 0.87f, 33), PhonemeData("Ф", 0.82f, 20), PhonemeData("Х", 0.79f, 18),
    PhonemeData("Ц", 0.61f, 15), PhonemeData("Ч", 0.66f, 14), PhonemeData("Ш", 0.91f, 35),
    PhonemeData("Щ", 0.58f, 12), PhonemeData("Й", 0.84f, 20),
)

@Composable
fun PhonemeMapScreen(childId: String, onBack: () -> Unit) {
    Scaffold(
        topBar = { SpeechKidsTopBar("Фонемная карта", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LegendDot(Green600, "Хорошо >85%")
                    LegendDot(Amber600, "Средне 65–85%")
                    LegendDot(Red600,   "Слабо <65%")
                }
            }
            item {
                SectionCard {
                    Text("Все фонемы", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    russianPhonemes.chunked(5).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            row.forEach { ph -> PhonemeCell(ph, modifier = Modifier.weight(1f)) }
                            repeat(5 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
            item {
                SectionCard {
                    Text("⚠️ Требуют внимания", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Red600)
                    Spacer(Modifier.height(10.dp))
                    russianPhonemes.filter { it.accuracy < 0.70f }.forEach { ph ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Звук «${ph.phoneme}»", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text("${(ph.accuracy * 100).toInt()}%  · ${ph.attempts} попыток", style = MaterialTheme.typography.bodySmall, color = Red600)
                        }
                        Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhonemeCell(ph: PhonemeData, modifier: Modifier) {
    val color = when { ph.accuracy >= 0.85f -> Green600; ph.accuracy >= 0.65f -> Amber600; else -> Red600 }
    val bg    = when { ph.accuracy >= 0.85f -> Green100; ph.accuracy >= 0.65f -> Amber100; else -> Red100 }
    Column(modifier = modifier.clip(RoundedCornerShape(10.dp)).background(bg).padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(ph.phoneme, fontSize = androidx.compose.ui.unit.TextUnit(18f, androidx.compose.ui.unit.TextUnitType.Sp), fontWeight = FontWeight.Bold, color = color)
        Text("${(ph.accuracy * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = color)
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(99.dp)).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ══════════════════════════════════════════════════════════════
// ReportsScreen
// ══════════════════════════════════════════════════════════════
@Composable
fun ReportsScreen(childId: String, onBack: () -> Unit) {
    Scaffold(
        topBar = { SpeechKidsTopBar("Отчёты — Алина", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("12",    "Сессий за месяц", modifier = Modifier.weight(1f))
                    StatCard("↑ 14%", "Рост точности",   valueColor = Green600, modifier = Modifier.weight(1f))
                }
            }
            item {
                SectionCard {
                    Text("Еженедельный прогресс", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    WeeklyChart()
                }
            }
            item {
                SectionCard {
                    Text("История сессий", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    val sessions = listOf(
                        Triple("20 мая", "Скороговорки",   "95%"),
                        Triple("19 мая", "Стихи наизусть", "87%"),
                        Triple("18 мая", "Свободная речь", "76%"),
                        Triple("17 мая", "Скороговорки",   "81%"),
                    )
                    sessions.forEach { (date, type, acc) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(type, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            BadgePill(acc, Green100, Green600)
                        }
                        Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
                    }
                }
            }
            item {
                Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600)
                ) { Text("📄 Экспорт PDF", style = MaterialTheme.typography.labelLarge) }
            }
        }
    }
}

@Composable
private fun WeeklyChart() {
    val weeks = listOf("Нед 1" to 0.62f, "Нед 2" to 0.70f, "Нед 3" to 0.76f, "Нед 4" to 0.82f)
    Row(modifier = Modifier.fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
        weeks.forEach { (label, pct) ->
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                Text("${(pct * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = Blue600)
                Spacer(Modifier.height(3.dp))
                Box(modifier = Modifier.fillMaxWidth().height((pct * 72).dp).clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)).background(Blue600))
                Spacer(Modifier.height(4.dp))
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════
// NotificationsScreen
// ══════════════════════════════════════════════════════════════
data class NotifItem(val text: String, val isAlert: Boolean, val time: String)

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    val notifs = listOf(
        NotifItem("⚠️ Регресс у Максима — звук «Р» ухудшился на 12%", true,  "2 часа назад"),
        NotifItem("✅ Алина завершила «Скороговорки» с результатом 95%", false, "3 часа назад"),
        NotifItem("📋 Логопед Карим назначил новое упражнение", false, "вчера"),
        NotifItem("🏆 Алина достигла уровня 4!", false, "2 дня назад"),
        NotifItem("⚠️ Максим пропустил 2 занятия подряд", true, "3 дня назад"),
    )
    Scaffold(
        topBar = { SpeechKidsTopBar("Уведомления", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(notifs) { notif ->
                val bgColor     = if (notif.isAlert) Red100 else MaterialTheme.colorScheme.surface
                val borderColor = if (notif.isAlert) Red600.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline
                Card(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, borderColor),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(notif.text, style = MaterialTheme.typography.bodyMedium)
                            Text(notif.time, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
