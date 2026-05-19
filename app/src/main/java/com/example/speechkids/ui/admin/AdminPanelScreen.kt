package com.example.speechkids.ui.admin

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.speechkids.components.*
import com.example.speechkids.theme.*

@Composable
fun AdminPanelScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Обзор", "Пользователи", "Модели", "Логи")

    Scaffold(
        topBar = { SpeechKidsTopBar("Админ-панель", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface, contentColor = Blue600) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i },
                        text = { Text(title, style = MaterialTheme.typography.labelLarge) })
                }
            }
            when (selectedTab) {
                0 -> AdminOverviewTab()
                1 -> AdminUsersTab()
                2 -> AdminModelsTab()
                3 -> AdminLogsTab()
            }
        }
    }
}

@Composable
private fun AdminOverviewTab() {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("1 247", "Пользователей", modifier = Modifier.weight(1f))
                StatCard("89",    "Логопедов",     modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("2 341", "Сессий сегодня", modifier = Modifier.weight(1f))
                StatCard("98.4%", "Uptime сервера", valueColor = Green600, modifier = Modifier.weight(1f))
            }
        }
        item {
            SectionCard {
                Text("Системные показатели", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                AdminMetricRow("Avg WER (базовая модель)",  "28.4%",  false)
                AdminMetricRow("Avg WER (после адаптации)", "9.1%",   true)
                AdminMetricRow("Latency (медиана)",          "380 мс", true)
                AdminMetricRow("Latency (P95)",              "620 мс", false)
                AdminMetricRow("Активных сессий сейчас",     "143",    true)
            }
        }
        item {
            SectionCard {
                Text("Активность за 7 дней", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                MiniBarChart(
                    data   = listOf(210f, 340f, 290f, 410f, 380f, 450f, 234f),
                    labels = listOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс")
                )
            }
        }
    }
}

@Composable
private fun AdminMetricRow(label: String, value: String, isGood: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold,
            color = if (isGood) Green600 else MaterialTheme.colorScheme.onSurface)
    }
    Divider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}

@Composable
private fun MiniBarChart(data: List<Float>, labels: List<String>) {
    val max = data.max()
    Row(modifier = Modifier.fillMaxWidth().height(80.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Bottom) {
        data.forEachIndexed { i, v ->
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                Box(modifier = Modifier.fillMaxWidth().height(((v / max) * 60).dp)
                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(Blue600))
                Spacer(Modifier.height(3.dp))
                Text(labels[i], style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun AdminUsersTab() {
    val users = listOf(
        Triple("Аиша Нурова",    "Родитель",  "сегодня"),
        Triple("Карим Сейткали", "Логопед",   "вчера"),
        Triple("Дмитрий Сидоров","Родитель",  "2 дня назад"),
        Triple("Гульнар Асанова","Педагог",   "3 дня назад"),
    )
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Последние регистрации", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
        items(users) { (name, role, date) ->
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarCircle(name.take(2).uppercase())
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(role, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun AdminModelsTab() {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            SectionCard {
                Text("Текущая модель", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                AdminMetricRow("Версия",              "whisper-kids-v2.3",   true)
                AdminMetricRow("Базовая",             "Whisper Large v3",    true)
                AdminMetricRow("Параметры",           "1.5B (LoRA rank 16)", true)
                AdminMetricRow("Размер на устройстве","148 МБ",              true)
                AdminMetricRow("Дата обновления",     "15 мая 2025",         true)
            }
        }
        item {
            SectionCard {
                Text("Производительность", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                ProgressRow("WER снижение",          0.76f, Green600); Spacer(Modifier.height(10.dp))
                ProgressRow("Фонемная точность",     0.88f, Blue600);  Spacer(Modifier.height(10.dp))
                ProgressRow("Latency цель ≤400 мс",  0.95f, Green600)
            }
        }
        item {
            Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(containerColor = Purple600)) {
                Text("🔄 Запустить дообучение", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun AdminLogsTab() {
    val logs = listOf(
        "[INFO]  2025-05-20 09:14 — 143 активных сессии",
        "[INFO]  2025-05-20 09:10 — Модель v2.3 загружена",
        "[WARN]  2025-05-20 08:55 — Latency P95 = 680 мс (порог 600)",
        "[INFO]  2025-05-20 08:30 — Federated Learning раунд завершён",
        "[ERROR] 2025-05-20 07:22 — Таймаут cloud fallback для user_4821",
        "[INFO]  2025-05-19 23:00 — Дневной бэкап завершён успешно",
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(logs) { log ->
            val color = when {
                log.startsWith("[ERROR]") -> Red600
                log.startsWith("[WARN]")  -> Amber600
                else                      -> Green600
            }
            Text(log, style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace), color = color)
        }
    }
}
