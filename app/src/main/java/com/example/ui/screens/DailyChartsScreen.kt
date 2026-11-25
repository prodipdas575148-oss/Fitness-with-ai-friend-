package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.BotanicalCard
import com.example.ui.components.FilterPillChip
import com.example.ui.components.PrivatePillBadge
import com.example.ui.navigation.Routes
import com.example.ui.theme.TabularMetricStyle

private data class DailyPlanGoal(
    val key: String,
    val label: String,
    val dailyKcal: String,
    val weeklyMoveMins: String,
    val proteinPct: Float,
    val carbsPct: Float,
    val fatPct: Float,
    val barHeights: List<Float>,
    val meals: List<Triple<String, String, String>>
)

@Composable
fun DailyChartsScreen(
    onNavigate: (String) -> Unit
) {
    val goals = remember {
        listOf(
            DailyPlanGoal(
                key = "fat_loss",
                label = "Fat Loss",
                dailyKcal = "1,650 kcal",
                weeklyMoveMins = "210 mins",
                proteinPct = 0.35f,
                carbsPct = 0.40f,
                fatPct = 0.25f,
                barHeights = listOf(0.75f, 0.90f, 0.65f, 0.85f, 0.95f, 0.70f, 0.50f),
                meals = listOf(
                    Triple("7:30 AM", "Warm lemon water + 2 boiled eggs or moong chilla", "310 kcal · High protein"),
                    Triple("10:30 AM", "Guava or apple + 5 soaked almonds", "140 kcal · Low-GI fiber"),
                    Triple("1:15 PM", "2 multigrain rotis, dal, sautéed greens & cucumber salad", "490 kcal · Balanced"),
                    Triple("4:30 PM", "Roasted chana + unsweetened green tea", "160 kcal · Light crunch"),
                    Triple("7:30 PM", "Grilled paneer/tofu stir-fry & clear vegetable soup", "450 kcal · Early light dinner")
                )
            ),
            DailyPlanGoal(
                key = "skin_hair",
                label = "Skin & Hair",
                dailyKcal = "1,850 kcal",
                weeklyMoveMins = "180 mins",
                proteinPct = 0.30f,
                carbsPct = 0.42f,
                fatPct = 0.28f,
                barHeights = listOf(0.65f, 0.80f, 0.70f, 0.85f, 0.75f, 0.90f, 0.60f),
                meals = listOf(
                    Triple("7:30 AM", "Soaked chia water + oats bowl with berries & walnuts", "360 kcal · Omega-3"),
                    Triple("10:30 AM", "Amla / orange slices + pumpkin seeds", "160 kcal · Vitamin C & Zinc"),
                    Triple("1:15 PM", "Brown rice or millet khichdi + spinach palak & curd", "540 kcal · Iron & probiotics"),
                    Triple("4:30 PM", "Carrot & bell pepper sticks with hummus", "180 kcal · Beta-carotene"),
                    Triple("7:30 PM", "Lentil soup, grilled fish or tofu + steamed broccoli", "490 kcal · Collagen support")
                )
            ),
            DailyPlanGoal(
                key = "balanced",
                label = "Balanced Energy",
                dailyKcal = "2,000 kcal",
                weeklyMoveMins = "195 mins",
                proteinPct = 0.30f,
                carbsPct = 0.45f,
                fatPct = 0.25f,
                barHeights = listOf(0.70f, 0.75f, 0.80f, 0.85f, 0.80f, 0.95f, 0.65f),
                meals = listOf(
                    Triple("7:30 AM", "Whole-grain toast + avocado + boiled egg or paneer bhurji", "390 kcal · Steady morning"),
                    Triple("10:30 AM", "Banana & handful of mixed nuts", "190 kcal · Potassium"),
                    Triple("1:15 PM", "1 bowl rajma/chole, brown rice, cabbage salad & buttermilk", "590 kcal · Complete meal"),
                    Triple("4:30 PM", "Greek yogurt or spiced buttermilk + roasted makhana", "190 kcal · Gut friendly"),
                    Triple("7:30 PM", "Vegetable millet roti wrap + tomato basil soup", "520 kcal · Restful night")
                )
            )
        )
    }

    var selectedGoalKey by remember { mutableStateOf("fat_loss") }
    val activeGoal = goals.first { it.key == selectedGoalKey }
    val exerciseChecked = remember { mutableStateMapOf("ex1" to true, "ex2" to false, "ex3" to false) }
    val exerciseTipsExpanded = remember { mutableStateMapOf<String, Boolean>() }

    val exercises = remember {
        listOf(
            Triple("ex1", "7:00 AM · Brisk Morning Walk (25 mins)", "Keep shoulders relaxed and swing arms naturally. Maintain a pace where you can speak in short sentences."),
            Triple("ex2", "12:30 PM · Desk Posture & Hip Mobility (8 mins)", "Stand tall every hour, roll shoulders back 10 times, and hold gentle hip flexor stretches for 30s per side."),
            Triple("ex3", "6:00 PM · Bodyweight Squats & Core (20 mins)", "Push hips back as if sitting into a chair, keep knees aligned over toes, and exhale as you stand.")
        )
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize()
        ) {
            Surface(color = MaterialTheme.colorScheme.surfaceContainerLowest) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column {
                            Text("Daily Exercise & Diet Charts", style = MaterialTheme.typography.headlineMedium)
                            Text("Time-blocked meal & movement plan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        PrivatePillBadge()
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f))
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Goal Switcher
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        goals.forEach { goal ->
                            FilterPillChip(
                                label = goal.label,
                                selected = goal.key == selectedGoalKey,
                                onClick = { selectedGoalKey = goal.key }
                            )
                        }
                    }
                }

                // Weekly Movement Bar Chart Card
                item {
                    val primary = MaterialTheme.colorScheme.primary
                    val secondary = MaterialTheme.colorScheme.secondaryContainer
                    BotanicalCard {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("Weekly Movement Rhythm", style = MaterialTheme.typography.titleMedium)
                                Text("${activeGoal.weeklyMoveMins} weekly target", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(activeGoal.dailyKcal, style = TabularMetricStyle, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                        ) {
                            val daysCount = 7
                            val spacing = size.width / daysCount
                            val barW = spacing * 0.46f
                            activeGoal.barHeights.forEachIndexed { idx, ratio ->
                                val barH = size.height * ratio
                                val x = idx * spacing + (spacing - barW) / 2f
                                val y = size.height - barH
                                drawRoundRect(
                                    color = if (idx == 4) primary else secondary,
                                    topLeft = Offset(x, y),
                                    size = Size(barW, barH),
                                    cornerRadius = CornerRadius(12f, 12f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("M", "T", "W", "T", "F", "S", "S").forEach { d ->
                                Text(d, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Macro Split Bar Card
                item {
                    BotanicalCard {
                        Text("Daily Macro Balance", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(9999.dp))
                        ) {
                            Box(Modifier.weight(activeGoal.proteinPct).height(14.dp).background(MaterialTheme.colorScheme.primary))
                            Box(Modifier.weight(activeGoal.carbsPct).height(14.dp).background(MaterialTheme.colorScheme.primaryContainer))
                            Box(Modifier.weight(activeGoal.fatPct).height(14.dp).background(Color(0xFFD99B26)))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Protein ${(activeGoal.proteinPct * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text("Carbs ${(activeGoal.carbsPct * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                            Text("Healthy Fats ${(activeGoal.fatPct * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, color = Color(0xFF916100))
                        }
                    }
                }

                // Time-Blocked Exercise Chart
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Today's Exercise Blocks", style = MaterialTheme.typography.titleLarge)
                        exercises.forEach { (id, title, tip) ->
                            val isChecked = exerciseChecked[id] == true
                            val expanded = exerciseTipsExpanded[id] == true
                            BotanicalCard {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { exerciseChecked[id] = it },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    )
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(
                                        onClick = { exerciseTipsExpanded[id] = !expanded },
                                        modifier = Modifier.testTag("chart_tips_toggle_$id")
                                    ) {
                                        Text("Tips")
                                        Icon(
                                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                            contentDescription = null
                                        )
                                    }
                                }
                                AnimatedVisibility(visible = expanded) {
                                    Text(
                                        text = tip,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(start = 12.dp, top = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Time-Blocked Diet Meals
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Time-Blocked Meal Chart", style = MaterialTheme.typography.titleLarge)
                        activeGoal.meals.forEach { (time, dish, tag) ->
                            BotanicalCard {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(time, style = TabularMetricStyle, color = MaterialTheme.colorScheme.primary)
                                    Text(tag, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(dish, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // Deep-Link Cards to Gym, Weight Loss & AI Video Coach
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { onNavigate(Routes.GYM_GUIDE) },
                            shape = RoundedCornerShape(9999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("charts_gym_guide_button")
                        ) {
                            Icon(Icons.Filled.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gym Guide")
                        }
                        OutlinedButton(
                            onClick = { onNavigate(Routes.AI_VIDEO_COACH) },
                            shape = RoundedCornerShape(9999.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("charts_video_coach_button")
                        ) {
                            Icon(Icons.Filled.OndemandVideo, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Video Coach")
                        }
                    }
                }
                item {
                    OutlinedButton(
                        onClick = { onNavigate(Routes.WEIGHT_LOSS) },
                        shape = RoundedCornerShape(9999.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("charts_weight_loss_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Weight & Fat Loss Guide")
                    }
                }
            }
        }
    }
}
