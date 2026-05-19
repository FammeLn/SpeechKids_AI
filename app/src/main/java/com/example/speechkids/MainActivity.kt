package com.example.speechkids

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.speechkids.theme.*
import com.example.speechkids.ui.admin.AdminPanelScreen
import com.example.speechkids.ui.child.*
import com.example.speechkids.ui.parent.*
import com.example.speechkids.ui.therapist.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpeechKidsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: String, val label: String) {
    object Home      : BottomNavItem("dashboard",  "🏠", "Главная")
    object Children  : BottomNavItem("children",   "👶", "Дети")
    object Games     : BottomNavItem("child_home", "🎮", "Игры")
    object Therapist : BottomNavItem("therapist",  "👨‍⚕️", "Логопед")
    object Admin     : BottomNavItem("admin",      "⚙️", "Админ")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Children,
    BottomNavItem.Games,
    BottomNavItem.Therapist,
    BottomNavItem.Admin
)

val bottomBarRoutes = setOf("dashboard", "children", "child_home", "therapist", "admin")

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Text(item.icon, fontSize = 20.sp) },
                            label = {
                                Text(
                                    item.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedTextColor = Blue600,
                                indicatorColor = Blue100,
                                unselectedTextColor = Gray500
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                ParentDashboardScreen(
                    onChildClick = { id -> navController.navigate("child_profile/$id") },
                    onTherapistClick = { navController.navigate("therapist") },
                    onNotificationsClick = { navController.navigate("notifications") },
                    onAdminClick = { navController.navigate("admin") }
                )
            }
            composable("children") {
                ChildrenListScreen(onChildClick = { id -> navController.navigate("child_profile/$id") })
            }
            composable("child_profile/{childId}") { back ->
                val childId = back.arguments?.getString("childId") ?: "1"
                ChildProfileScreen(
                    childId = childId,
                    onBack = { navController.popBackStack() },
                    onPhonemeMap = { navController.navigate("phoneme_map/$childId") },
                    onAssignExercise = { navController.navigate("assign/$childId") },
                    onReports = { navController.navigate("reports/$childId") }
                )
            }
            composable("phoneme_map/{childId}") { back ->
                val childId = back.arguments?.getString("childId") ?: "1"
                PhonemeMapScreen(childId = childId, onBack = { navController.popBackStack() })
            }
            composable("reports/{childId}") { back ->
                val childId = back.arguments?.getString("childId") ?: "1"
                ReportsScreen(childId = childId, onBack = { navController.popBackStack() })
            }
            composable("assign/{childId}") { back ->
                val childId = back.arguments?.getString("childId") ?: "1"
                AssignExerciseScreen(
                    childId = childId,
                    onBack = { navController.popBackStack() },
                    onDone = { navController.popBackStack() }
                )
            }
            composable("therapist") {
                TherapistScreen(
                    onBack = { navController.popBackStack() },
                    onPatientClick = { id -> navController.navigate("child_profile/$id") }
                )
            }
            composable("notifications") {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }
            composable("admin") {
                AdminPanelScreen(onBack = { navController.popBackStack() })
            }
            composable("child_home") {
                ChildHomeScreen(
                    onTongueTwister = { navController.navigate("game_tongue") },
                    onPoem          = { navController.navigate("game_poem") },
                    onFreeSpeech    = { navController.navigate("game_free") }
                )
            }
            composable("game_tongue") {
                TongueTwisterScreen(onFinish = { score, xp -> navController.navigate("result/$score/$xp") })
            }
            composable("game_poem") {
                PoemScreen(onFinish = { score, xp -> navController.navigate("result/$score/$xp") })
            }
            composable("game_free") {
                FreeSpeechScreen(onFinish = { score, xp -> navController.navigate("result/$score/$xp") })
            }
            composable("result/{score}/{xp}") { back ->
                val score = back.arguments?.getString("score")?.toIntOrNull() ?: 0
                val xp    = back.arguments?.getString("xp")?.toIntOrNull() ?: 0
                GameResultScreen(
                    score    = score,
                    xpEarned = xp,
                    onHome   = { navController.navigate("child_home") { popUpTo("child_home") { inclusive = true } } },
                    onNext   = { navController.navigate("game_tongue") }
                )
            }
        }
    }
}

@Composable
fun ChildrenListScreen(onChildClick: (String) -> Unit) {
    val children = listOf(
        Triple("1", "Алина",  "5 лет · Уровень 4 · 🔥 3 дня"),
        Triple("2", "Максим", "7 лет · Уровень 2 · ⚠️ Регресс"),
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Мои дети", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }
        items(children) { (id, name, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onChildClick(id) },
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(Blue100),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name.take(2), fontWeight = FontWeight.SemiBold, color = Blue600)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("→", fontSize = 20.sp, color = Blue600)
                }
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth().height(50.dp), shape = MaterialTheme.shapes.medium) {
                Text("+ Добавить ребёнка")
            }
        }
    }
}