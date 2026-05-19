package com.example.speechkids.ui.child

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.speechkids.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════
// ChildHomeScreen — главный экран игр
// ══════════════════════════════════════════════════════════════
@Composable
fun ChildHomeScreen(
    onTongueTwister: () -> Unit,
    onPoem: () -> Unit,
    onFreeSpeech: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0D1117))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text("Привет, Алина! 👋", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(4.dp))
                Text("Выбери игру!", fontSize = 15.sp, color = Navy200, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(Purple600).padding(horizontal = 14.dp, vertical = 6.dp)) {
                        Text("⭐ 340 XP", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(Amber600).padding(horizontal = 14.dp, vertical = 6.dp)) {
                        Text("🔥 3 дня", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            item { GameCard(emoji = "🖼", title = "Угадай слово",  desc = "Смотри на картинку и выбирай!", color = Blue600,   bg = Navy800, onClick = onTongueTwister) }
            item { GameCard(emoji = "🔤", title = "Собери слово",  desc = "Собери буквы по порядку!",    color = Purple600, bg = Navy800, onClick = onPoem) }
            item { GameCard(emoji = "🔍", title = "Найди лишнее", desc = "Какое слово не подходит?",     color = Green600,  bg = Navy800, onClick = onFreeSpeech) }
            item {
                Spacer(Modifier.height(8.dp))
                Text("🐉", fontSize = 64.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Text("Дракончик Рекси ждёт тебя!", fontSize = 13.sp, color = Navy200, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun GameCard(emoji: String, title: String, desc: String, color: Color, bg: Color, onClick: () -> Unit) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxWidth().scale(scale.value)
            .clip(RoundedCornerShape(24.dp)).background(bg)
            .clickable {
                scope.launch {
                    scale.animateTo(0.95f, spring(stiffness = Spring.StiffnessHigh))
                    scale.animateTo(1f, spring(stiffness = Spring.StiffnessMedium))
                    onClick()
                }
            }.padding(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).background(color.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 32.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text(desc, fontSize = 13.sp, color = Navy200)
            }
            Spacer(Modifier.weight(1f))
            Text("→", fontSize = 22.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

// ══════════════════════════════════════════════════════════════
// Игра 1: Угадай слово по картинке
// ══════════════════════════════════════════════════════════════
data class GuessQuestion(val picture: String, val correct: String, val options: List<String>)

private val guessQuestions = listOf(
    GuessQuestion("🦁", "Лев",    listOf("Кот", "Лев", "Волк", "Тигр")),
    GuessQuestion("🐘", "Слон",   listOf("Слон", "Бегемот", "Носорог", "Жираф")),
    GuessQuestion("🍎", "Яблоко", listOf("Груша", "Апельсин", "Яблоко", "Банан")),
    GuessQuestion("🚀", "Ракета", listOf("Самолёт", "Ракета", "Вертолёт", "Корабль")),
    GuessQuestion("🌈", "Радуга", listOf("Гроза", "Туча", "Солнце", "Радуга")),
)

@Composable
fun TongueTwisterScreen(onFinish: (score: Int, xp: Int) -> Unit) {
    var index     by remember { mutableIntStateOf(0) }
    var selected  by remember { mutableStateOf<String?>(null) }
    var score     by remember { mutableIntStateOf(0) }
    val scope     = rememberCoroutineScope()
    val q = guessQuestions[index]

    FullScreenGameLayout(title = "Угадай слово 🖼", progress = (index + 1).toFloat() / guessQuestions.size) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier.size(160.dp).clip(RoundedCornerShape(32.dp)).background(Navy800),
            contentAlignment = Alignment.Center
        ) { Text(q.picture, fontSize = 80.sp) }
        Spacer(Modifier.height(24.dp))
        Text("Что это?", fontSize = 18.sp, color = Navy200, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        val shuffled = remember(index) { q.options.shuffled() }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            shuffled.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    row.forEach { option ->
                        val isSelected = selected == option
                        val isCorrect  = option == q.correct
                        val bgColor = when {
                            isSelected && isCorrect  -> Green600
                            isSelected && !isCorrect -> Red600
                            else                     -> Navy800
                        }
                        Box(
                            modifier = Modifier.weight(1f).height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgColor)
                                .clickable(enabled = selected == null) {
                                    selected = option
                                    if (isCorrect) score++
                                    scope.launch {
                                        delay(800)
                                        if (index < guessQuestions.size - 1) {
                                            index++; selected = null
                                        } else onFinish(score * 20, score * 8)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(option, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White, textAlign = TextAlign.Center)
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

// ══════════════════════════════════════════════════════════════
// Игра 2: Собери слово из букв
// ══════════════════════════════════════════════════════════════
private val wordTasks = listOf("КОТ", "ДОМ", "СОН", "ЛЕС", "МИР", "РОТ")

@Composable
fun PoemScreen(onFinish: (score: Int, xp: Int) -> Unit) {
    var wordIndex  by remember { mutableIntStateOf(0) }
    var selected   by remember { mutableStateOf(listOf<Int>()) }
    var score      by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf<Boolean?>(null) }
    val scope      = rememberCoroutineScope()
    val word       = wordTasks[wordIndex]
    val letters    = remember(wordIndex) { word.toList().mapIndexed { i, c -> i to c }.shuffled() }
    val assembled  = selected.map { idx -> word[idx] }.joinToString("")

    FullScreenGameLayout(title = "Собери слово 🔤", progress = (wordIndex + 1).toFloat() / wordTasks.size) {
        Spacer(Modifier.height(16.dp))
        Text("Собери слово из ${word.length} букв", fontSize = 16.sp, color = Navy200, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))

        // Assembled word display
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.align(Alignment.CenterHorizontally)) {
            repeat(word.length) { i ->
                val char = if (i < assembled.length) assembled[i].toString() else "_"
                val bg = when {
                    showResult == true  -> Green600
                    showResult == false -> Red600
                    i < assembled.length -> Blue600
                    else -> Navy700
                }
                Box(
                    modifier = Modifier.size(52.dp).clip(RoundedCornerShape(12.dp)).background(bg),
                    contentAlignment = Alignment.Center
                ) { Text(char, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White) }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Letter buttons
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.align(Alignment.CenterHorizontally)) {
            letters.forEach { (origIdx, char) ->
                val used = origIdx in selected
                Box(
                    modifier = Modifier.size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (used) Navy400 else Amber600)
                        .clickable(enabled = !used && showResult == null) {
                            val newSelected = selected + origIdx
                            selected = newSelected
                            if (newSelected.size == word.length) {
                                val correct = newSelected.map { word[it] }.joinToString("") == word
                                showResult = correct
                                if (correct) score++
                                scope.launch {
                                    delay(900)
                                    if (wordIndex < wordTasks.size - 1) {
                                        wordIndex++; selected = listOf(); showResult = null
                                    } else onFinish(score * 17, score * 7)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) { Text(char.toString(), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = if (used) Navy200 else Color.White) }
            }
        }

        Spacer(Modifier.height(16.dp))
        if (selected.isNotEmpty()) {
            TextButton(onClick = { selected = listOf(); showResult = null }) {
                Text("Сбросить", color = Navy200, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

// ══════════════════════════════════════════════════════════════
// Игра 3: Найди лишнее слово
// ══════════════════════════════════════════════════════════════
data class OddOneOut(val words: List<String>, val odd: String, val hint: String)

private val oddQuestions = listOf(
    OddOneOut(listOf("Кошка", "Собака", "Роза", "Корова"),   "Роза",    "Роза — это растение, а не животное"),
    OddOneOut(listOf("Красный", "Синий", "Стол", "Зелёный"), "Стол",    "Стол — это предмет, а не цвет"),
    OddOneOut(listOf("Яблоко", "Банан", "Морковь", "Груша"), "Морковь", "Морковь — это овощ, а не фрукт"),
    OddOneOut(listOf("Самолёт", "Машина", "Корабль", "Птица"),"Птица",  "Птица — живая, а не транспорт"),
    OddOneOut(listOf("Один", "Два", "Пять", "Много"),        "Много",   "Много — не число, а остальные числа"),
)

@Composable
fun FreeSpeechScreen(onFinish: (score: Int, xp: Int) -> Unit) {
    var index     by remember { mutableIntStateOf(0) }
    var selected  by remember { mutableStateOf<String?>(null) }
    var score     by remember { mutableIntStateOf(0) }
    val scope     = rememberCoroutineScope()
    val q         = oddQuestions[index]

    FullScreenGameLayout(title = "Найди лишнее 🔍", progress = (index + 1).toFloat() / oddQuestions.size) {
        Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Navy800).padding(20.dp)) {
            Text("Какое слово лишнее?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            q.words.forEach { word ->
                val isSelected = selected == word
                val isOdd      = word == q.odd
                val bgColor = when {
                    isSelected && isOdd  -> Green600
                    isSelected && !isOdd -> Red600
                    else                 -> Navy800
                }
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .border(if (!isSelected) 0.5.dp else 0.dp, Navy400, RoundedCornerShape(16.dp))
                        .clickable(enabled = selected == null) {
                            selected = word
                            if (isOdd) score++
                            scope.launch {
                                delay(1000)
                                if (index < oddQuestions.size - 1) {
                                    index++; selected = null
                                } else onFinish(score * 20, score * 9)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) { Text(word, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White) }
            }
        }
        if (selected != null) {
            Spacer(Modifier.height(16.dp))
            val isCorrect = selected == q.odd
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(if (isCorrect) Green600.copy(alpha = 0.2f) else Red600.copy(alpha = 0.2f))
                    .padding(14.dp)
            ) {
                Text(
                    if (isCorrect) "✅ Правильно! ${q.hint}" else "❌ Неверно. ${q.hint}",
                    fontSize = 13.sp,
                    color = if (isCorrect) Green600 else Red600,
                    lineHeight = 20.sp
                )
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

// ══════════════════════════════════════════════════════════════
// GameResultScreen
// ══════════════════════════════════════════════════════════════
@Composable
fun GameResultScreen(score: Int, xpEarned: Int, onHome: () -> Unit, onNext: () -> Unit) {
    val bounceAnim = rememberInfiniteTransition(label = "bounce")
    val bounce by bounceAnim.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(600, easing = EaseInOut), RepeatMode.Reverse),
        label = "bounce"
    )
    val (title, color) = when { score >= 80 -> "Отлично! 🏆" to Green600; score >= 50 -> "Хорошо! 🌟" to Amber600; else -> "Молодец! 💪" to Blue600 }

    Box(modifier = Modifier.fillMaxSize().background(Navy900), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🐉", fontSize = 72.sp, modifier = Modifier.scale(bounce))
            Spacer(Modifier.height(16.dp))
            Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(Modifier.height(16.dp))
            Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(color.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$score%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = color)
                    Text("результат", fontSize = 12.sp, color = Navy200)
                }
            }
            Spacer(Modifier.height(20.dp))
            Box(modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(Purple600.copy(alpha = 0.2f)).padding(horizontal = 24.dp, vertical = 12.dp)) {
                Text("+$xpEarned XP", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Purple600)
            }
            Spacer(Modifier.height(40.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onHome, modifier = Modifier.weight(1f).height(50.dp), shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)) { Text("🏠 Домой") }
                Button(onClick = onNext, modifier = Modifier.weight(1f).height(50.dp), shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600)) { Text("Ещё раз →") }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════
// Shared layout
// ══════════════════════════════════════════════════════════════
@Composable
fun FullScreenGameLayout(title: String, progress: Float, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Navy900).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
            Text("${(progress * 100).toInt()}%", fontSize = 13.sp, color = Navy200)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)), color = Blue600, trackColor = Navy700)
        content()
    }
}

@Composable
fun ChildWaveform() {}