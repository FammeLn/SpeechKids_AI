package com.example.speechkids.ui.therapist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.speechkids.components.*
import com.example.speechkids.theme.*

data class Patient(
    val id: String, val name: String, val age: Int,
    val initials: String, val lastSession: String,
    val wer: Float, val flag: Boolean
)

private val mockPatients = listOf(
    Patient("1", "Алина",  5, "АЛ", "сегодня",     0.18f, false),
    Patient("2", "Максим", 7, "МК", "3 дня назад",  0.36f, true),
    Patient("3", "Карина", 6, "КР", "вчера",        0.22f, false),
    Patient("4", "Тимур",  8, "ТМ", "неделю назад", 0.41f, true),
)

@Composable
fun TherapistScreen(onBack: () -> Unit, onPatientClick: (String) -> Unit) {
    Scaffold(
        topBar = { SpeechKidsTopBar("Кабинет логопеда", onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard("4",    "Пациентов",        modifier = Modifier.weight(1f))
                    StatCard("2 ⚠️", "Требуют внимания", valueColor = Red600, modifier = Modifier.weight(1f))
                    StatCard("94%",  "Посещаемость",     valueColor = Green600, modifier = Modifier.weight(1f))
                }
            }
            item {
                OutlinedTextField(
                    value = "", onValueChange = {},
                    placeholder = { Text("Поиск пациента...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium, singleLine = true
                )
            }
            item { Text("Пациенты", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            items(mockPatients) { patient ->
                PatientCard(patient = patient, onClick = { onPatientClick(patient.id) })
            }
        }
    }
}

@Composable
private fun PatientCard(patient: Patient, onClick: () -> Unit) {
    SectionCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarCircle(
                initials = patient.initials, size = 48.dp,
                bgColor = if (patient.flag) Red100 else Blue100,
                textColor = if (patient.flag) Red600 else Blue600
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(patient.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${patient.age} лет · последняя сессия: ${patient.lastSession}",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                if (patient.flag) BadgePill("⚠️ Регресс", Red100, Red600)
                Spacer(Modifier.height(4.dp))
                Text("WER ${(patient.wer * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (patient.wer > 0.30f) Red600 else Green600,
                    fontWeight = FontWeight.Medium)
            }
        }
        Spacer(Modifier.height(12.dp))
        ProgressRow("Точность (100 - WER)", 1f - patient.wer,
            color = if (patient.wer > 0.30f) Red600 else Green600)
    }
}

data class ExerciseTemplate(val id: String, val title: String, val subtitle: String, val icon: String, val ageRange: String)

private val exerciseTemplates = listOf(
    ExerciseTemplate("tt", "Скороговорки",    "Артикуляция, скорость",      "🗣", "6–12 лет"),
    ExerciseTemplate("pm", "Стихи наизусть",  "Полный анализ произношения", "📜", "5–12 лет"),
    ExerciseTemplate("fs", "Свободная речь",  "Беглость, словарный запас", "💬", "4–12 лет"),
    ExerciseTemplate("rw", "Повтори слово",   "WER, фонемная точность",    "🔁", "2–4 лет"),
    ExerciseTemplate("np", "Назови картинку", "Точность произношения",     "🖼", "3–6 лет"),
)

@Composable
fun AssignExerciseScreen(childId: String, onBack: () -> Unit, onDone: () -> Unit) {
    var selectedId   by remember { mutableStateOf<String?>(null) }
    var selectedDays by remember { mutableIntStateOf(3) }
    var comment      by remember { mutableStateOf("") }
    var showSuccess  by remember { mutableStateOf(false) }

    if (showSuccess) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✅", fontSize = 64.sp)
                Spacer(Modifier.height(12.dp))
                Text("Задание назначено!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(24.dp))
                PrimaryButton("Готово", onDone)
            }
        }
        return
    }

    Scaffold(
        topBar = { SpeechKidsTopBar("Назначить задание", onBack) },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                PrimaryButton(
                    text = "Назначить",
                    onClick = { if (selectedId != null) showSuccess = true },
                    modifier = Modifier.padding(16.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Text("Выберите тип упражнения", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            items(exerciseTemplates) { ex ->
                val selected = selectedId == ex.id
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedId = ex.id },
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = if (selected) Blue100 else MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(if (selected) 2.dp else 0.5.dp, if (selected) Blue600 else MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(ex.icon, fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ex.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(ex.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            BadgePill(ex.ageRange, Amber100, Amber600)
                        }
                        if (selected) Text("✓", color = Blue600, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }
            item {
                Text("Количество дней в неделю", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Slider(value = selectedDays.toFloat(), onValueChange = { selectedDays = it.toInt() },
                    valueRange = 1f..7f, steps = 5,
                    colors = SliderDefaults.colors(thumbColor = Blue600, activeTrackColor = Blue600))
                Text("$selectedDays дней", style = MaterialTheme.typography.bodyMedium, color = Blue600, fontWeight = FontWeight.Medium)
            }
            item {
                OutlinedTextField(value = comment, onValueChange = { comment = it },
                    label = { Text("Комментарий для родителя (необязательно)") },
                    modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.medium, minLines = 3)
            }
        }
    }
}
