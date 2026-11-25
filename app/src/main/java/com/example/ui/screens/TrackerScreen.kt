package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.RoutineTask
import com.example.data.UiState
import com.example.data.UserWellnessProfile
import com.example.ui.components.BotanicalCard
import com.example.ui.components.FilterPillChip
import com.example.ui.components.PrivatePillBadge
import com.example.ui.theme.TabularMetricStyle
import com.example.ui.viewmodel.HealthViewModel
import java.util.Locale

@Composable
fun TrackerScreen(
    profileState: UiState<UserWellnessProfile?>,
    routineTasksState: UiState<List<RoutineTask>>,
    stopwatchElapsedSec: Long,
    stopwatchRunning: Boolean,
    stopwatchLaps: List<String>,
    onUpdateWater: (Long, Long) -> Unit,
    onUpdateSteps: (Long, Long) -> Unit,
    onSelectCheckIn: (String) -> Unit,
    onToggleTask: (RoutineTask) -> Unit,
    onAddTask: (String, String, String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onToggleStopwatch: () -> Unit,
    onLapStopwatch: () -> Unit,
    onResetStopwatch: () -> Unit
) {
    val profile = (profileState as? UiState.Success)?.data
    val tasks = (routineTasksState as? UiState.Success)?.data.orEmpty()

    val waterGlasses = profile?.waterGlasses ?: 5L
    val waterGoal = (profile?.waterGoal ?: 8L).coerceAtLeast(1L)
    val steps = profile?.steps ?: 4200L
    val stepsGoal = (profile?.stepsGoal ?: 8000L).coerceAtLeast(1000L)
    val streakDays = profile?.streakDays ?: 5L
    val activeCheckIn = profile?.activeCheckIn ?: "Feeling steady"

    val doneCount = tasks.count { it.done }
    val completionPct = if (tasks.isEmpty()) 0 else (doneCount * 100) / tasks.size

    var selectedPeriod by remember { mutableStateOf("all") }
    var showAddHabitDialog by remember { mutableStateOf(false) }

    val filteredTasks = remember(tasks, selectedPeriod) {
        if (selectedPeriod == "all") tasks else tasks.filter { it.period == selectedPeriod }
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
                            Text("Health & Routine Tracker", style = MaterialTheme.typography.headlineMedium)
                            Text(
                                text = "Daily hydration, footsteps & care checklist",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                // Streak + 3-column summary
                item {
                    BotanicalCard(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$streakDays-day gentle care streak",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SummaryStatTile("Routines", "$completionPct%", Modifier.weight(1f))
                            SummaryStatTile("Water", "$waterGlasses/$waterGoal", Modifier.weight(1f))
                            SummaryStatTile("Check-in", activeCheckIn.split(" ").firstOrNull() ?: "Steady", Modifier.weight(1f))
                        }
                    }
                }

                // Hydration Tracker
                item {
                    BotanicalCard {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocalDrink, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Daily Hydration", style = MaterialTheme.typography.titleMedium)
                                    Text("Goal: $waterGoal glasses (250ml each)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text("$waterGlasses / $waterGoal", style = TabularMetricStyle, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (waterGlasses.toFloat() / waterGoal.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(9999.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { onUpdateWater((waterGlasses - 1L).coerceAtLeast(0L), steps) },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("water_minus_button")
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "Decrease water")
                            }
                            Button(
                                onClick = { onUpdateWater((waterGlasses + 1L).coerceAtMost(20L), steps) },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("water_plus_button")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log 1 glass")
                            }
                        }
                    }
                }

                // Footsteps Card
                item {
                    val distanceKm = String.format(Locale.US, "%.1f", steps * 0.00075)
                    val estKcal = (steps * 0.04).toInt()
                    BotanicalCard {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Filled.DirectionsWalk, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Daily Footsteps", style = MaterialTheme.typography.titleMedium)
                                    Text("$distanceKm km · $estKcal kcal active", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text("$steps / $stepsGoal", style = TabularMetricStyle, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (steps.toFloat() / stepsGoal.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(9999.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onUpdateSteps(waterGlasses, (steps + 500L).coerceAtMost(100000L)) },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("add_500_steps_button")
                            ) {
                                Text("+500 steps")
                            }
                            OutlinedButton(
                                onClick = { onUpdateSteps(waterGlasses, 0L) },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier.testTag("reset_steps_button")
                            ) {
                                Text("Reset")
                            }
                        }
                    }
                }

                // Live Running Stopwatch
                item {
                    BotanicalCard {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Walk & Workout Stopwatch", style = MaterialTheme.typography.titleMedium)
                            }
                            Text(
                                text = HealthViewModel.formatSeconds(stopwatchElapsedSec),
                                style = TabularMetricStyle.copy(fontSize = MaterialTheme.typography.headlineMedium.fontSize),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.testTag("stopwatch_display")
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onToggleStopwatch,
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("stopwatch_toggle_button")
                            ) {
                                Icon(
                                    imageVector = if (stopwatchRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (stopwatchRunning) "Pause" else if (stopwatchElapsedSec > 0L) "Resume" else "Start")
                            }
                            OutlinedButton(
                                onClick = onLapStopwatch,
                                enabled = stopwatchElapsedSec > 0L,
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier.testTag("stopwatch_lap_button")
                            ) {
                                Icon(Icons.Filled.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Lap")
                            }
                            OutlinedButton(
                                onClick = onResetStopwatch,
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier.testTag("stopwatch_reset_button")
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = "Reset stopwatch", modifier = Modifier.size(16.dp))
                            }
                        }
                        if (stopwatchLaps.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            ) {
                                stopwatchLaps.forEach { lap ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                                        shape = RoundedCornerShape(9999.dp)
                                    ) {
                                        Text(
                                            text = lap,
                                            style = TabularMetricStyle,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Check-in Chips
                item {
                    BotanicalCard {
                        Text("How are you feeling today?", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf("Feeling steady", "Skin calmer", "Less hair fall", "Mild fatigue").forEach { chip ->
                                FilterPillChip(
                                    label = chip,
                                    selected = activeCheckIn == chip,
                                    onClick = { onSelectCheckIn(chip) }
                                )
                            }
                        }
                    }
                }

                // Care Routine Checklist Header + Period Filter
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Care Routine Checklist", style = MaterialTheme.typography.titleLarge)
                            TextButton(
                                onClick = { showAddHabitDialog = true },
                                modifier = Modifier.testTag("add_habit_button")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add habit")
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf("all" to "All", "morning" to "Morning", "evening" to "Evening", "nutrition" to "Nutrition")
                                .forEach { (key, label) ->
                                    FilterPillChip(
                                        label = label,
                                        selected = selectedPeriod == key,
                                        onClick = { selectedPeriod = key }
                                    )
                                }
                        }
                    }
                }

                items(filteredTasks, key = { it.id }) { task ->
                    BotanicalCard(
                        onClick = { onToggleTask(task) },
                        modifier = Modifier.testTag("routine_task_card_${task.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = task.done,
                                onCheckedChange = { onToggleTask(task) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (task.done) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )
                                if (task.subtitle.isNotBlank()) {
                                    Text(
                                        text = task.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = { onDeleteTask(task.id) },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteOutline,
                                    contentDescription = "Delete habit",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddHabitDialog) {
        AddHabitDialog(
            onConfirm = { title, subtitle, period ->
                showAddHabitDialog = false
                onAddTask(title, subtitle, period)
            },
            onDismiss = { showAddHabitDialog = false }
        )
    }
}

@Composable
private fun SummaryStatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = TabularMetricStyle, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun AddHabitDialog(
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var period by remember { mutableStateOf("morning") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add daily wellness habit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Timing note (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("morning", "evening", "nutrition").forEach { p ->
                        FilterPillChip(
                            label = p.replaceFirstChar { it.uppercase() },
                            selected = period == p,
                            onClick = { period = p }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onConfirm(title, subtitle, period) },
                enabled = title.isNotBlank()
            ) {
                Text("Save Habit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
