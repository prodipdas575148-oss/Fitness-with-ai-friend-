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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material3.Button
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

private data class GymExerciseItem(
    val id: String,
    val title: String,
    val category: String,
    val setsReps: String,
    val formTip: String,
    val safetyNote: String
)

@Composable
fun GymGuideScreen(
    onOpenVideoCoach: () -> Unit,
    onBack: () -> Unit
) {
    val exercises = remember {
        listOf(
            GymExerciseItem("g1", "Treadmill Incline Walk", "Cardio", "15–20 mins · 4–6% incline", "Keep chest upright without gripping rails tightly.", "Stop immediately if dizzy or short of breath."),
            GymExerciseItem("g2", "Lat Pulldown (Wide Neutral)", "Upper Body", "3 sets × 12 reps", "Pull bar to upper collarbone while squeezing shoulder blades down.", "Avoid jerking torso backward."),
            GymExerciseItem("g3", "Dumbbell Chest Press", "Upper Body", "3 sets × 10 reps", "Keep wrists stacked directly over elbows and feet planted flat.", "Use light weights first; use a spotter for heavier sets."),
            GymExerciseItem("g4", "Goblet Squat", "Lower Body", "3 sets × 12 reps", "Hold dumbbell close to chest, sit hips back, keep heels grounded.", "Keep knees tracking in line with toes."),
            GymExerciseItem("g5", "Seated Leg Press", "Lower Body", "3 sets × 12 reps", "Lower platform slowly to 90 degrees and press smoothly.", "Never lock out knees forcefully at the top.")
        )
    }

    var filter by remember { mutableStateOf("All") }
    val checked = remember { mutableStateMapOf<String, Boolean>() }
    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    val filtered = remember(filter) {
        if (filter == "All") exercises else exercises.filter { it.category == filter }
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
                title = "Gym Exercise Guide",
                subtitle = "Safe form tips & workout checklist",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    BotanicalCard(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)) {
                        Text("Need visual form demonstrations?", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = onOpenVideoCoach,
                            shape = RoundedCornerShape(9999.dp)
                        ) {
                            Icon(Icons.Filled.OndemandVideo, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open AI Video Coach")
                        }
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf("All", "Cardio", "Upper Body", "Lower Body").forEach { cat ->
                            FilterPillChip(label = cat, selected = filter == cat, onClick = { filter = cat })
                        }
                    }
                }

                items(filtered, key = { it.id }) { ex ->
                    val isExpanded = expanded[ex.id] == true
                    BotanicalCard {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(
                                checked = checked[ex.id] == true,
                                onCheckedChange = { checked[ex.id] = it },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ex.title, style = MaterialTheme.typography.titleMedium)
                                Text("${ex.category} · ${ex.setsReps}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TextButton(onClick = { expanded[ex.id] = !isExpanded }) {
                                Text("Tips")
                                Icon(
                                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                            ) {
                                Text("Form: ${ex.formTip}", style = MaterialTheme.typography.bodySmall)
                                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp)) {
                                    Text(
                                        text = "Safety: ${ex.safetyNote}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
