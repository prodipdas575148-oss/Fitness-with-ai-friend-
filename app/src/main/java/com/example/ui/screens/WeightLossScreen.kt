package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.components.FilterPillChip

private data class FatLossExercise(
    val id: String,
    val title: String,
    val category: String,
    val duration: String,
    val tip: String
)

@Composable
fun WeightLossScreen(onBack: () -> Unit) {
    val workouts = remember {
        listOf(
            FatLossExercise("w1", "Brisk Post-Meal Walk", "Low Impact", "20–30 mins", "Walking 15 mins after lunch or dinner smooths blood sugar spikes without joint strain."),
            FatLossExercise("w2", "March-in-Place + Step Jacks", "Cardio", "12 mins", "Low-impact rhythmic movement keeps heart rate in a steady fat-burning zone."),
            FatLossExercise("w3", "Wall Push-ups + Glute Bridges", "Core & Strength", "3 sets × 12 reps", "Preserves lean muscle while in a mild, healthy calorie deficit."),
            FatLossExercise("w4", "Slow Bird-Dog & Dead Bug", "Core & Strength", "2 sets × 10 reps", "Builds deep abdominal stability gently without lower-back strain.")
        )
    }

    var filter by remember { mutableStateOf("All") }
    val checked = remember { mutableStateMapOf<String, Boolean>() }
    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    val filtered = remember(filter) {
        if (filter == "All") workouts else workouts.filter { it.category == filter }
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
            BotanicalSubTopBar(
                title = "Weight & Fat Loss Guide",
                subtitle = "Sustainable habits & joint-friendly movement",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    BotanicalCard(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)) {
                        Text("Safe Everyday Habits", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• Pair protein + high-fiber veggies at every meal to stay full longer.", style = MaterialTheme.typography.bodySmall)
                        Text("• Aim for 7–8 hours of sleep; poor sleep raises evening cravings.", style = MaterialTheme.typography.bodySmall)
                        Text("• Target steady 0.3–0.5 kg weekly change rather than crash diets.", style = MaterialTheme.typography.bodySmall)
                    }
                }

                item {
                    Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Avoid unverified fat-burner pills, laxative teas, or extreme fasting—they risk dehydration and nutrient loss.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf("All", "Low Impact", "Cardio", "Core & Strength").forEach { cat ->
                            FilterPillChip(label = cat, selected = filter == cat, onClick = { filter = cat })
                        }
                    }
                }

                items(filtered, key = { it.id }) { item ->
                    val isExpanded = expanded[item.id] == true
                    BotanicalCard {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(
                                checked = checked[item.id] == true,
                                onCheckedChange = { checked[item.id] = it },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, style = MaterialTheme.typography.titleMedium)
                                Text("${item.category} · ${item.duration}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(onClick = { expanded[item.id] = !isExpanded }) {
                                Text("Tips")
                                Icon(
                                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility(visible = isExpanded) {
                            Text(
                                text = item.tip,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 12.dp, top = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
