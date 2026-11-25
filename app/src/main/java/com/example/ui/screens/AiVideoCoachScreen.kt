package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.components.FilterPillChip
import com.example.ui.theme.TabularMetricStyle
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

private data class CoachExerciseDemo(
    val title: String,
    val focus: String,
    val voiceCue: String,
    val targetReps: Int
)

@Composable
fun AiVideoCoachScreen(
    onBack: () -> Unit
) {
    val demos = remember {
        listOf(
            CoachExerciseDemo("Bodyweight Squat", "Legs & glutes", "Sit hips back slowly for 2 seconds, keep chest tall, and push through heels.", 12),
            CoachExerciseDemo("Treadmill Incline Walk", "Fat burn cardio", "Maintain upright posture at a 4–6% incline with brisk natural arm swing.", 20),
            CoachExerciseDemo("Dumbbell Chest Press", "Chest & shoulders", "Lower weights slowly to mid-chest level and press smoothly without locking elbows.", 10),
            CoachExerciseDemo("Forearm Plank Hold", "Core stability", "Keep hips level with shoulders, brace core gently, and breathe steadily.", 30),
            CoachExerciseDemo("Low-Impact Jumping Jacks", "Warm-up cardio", "Step one foot out at a time while sweeping arms overhead smoothly.", 15)
        )
    }

    var coachAvatar by remember { mutableStateOf("Maya") }
    var viewAngle by remember { mutableStateOf("Front") }
    var speedMultiplier by remember { mutableFloatStateOf(1.0f) }
    var selectedDemoIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var phaseProgress by remember { mutableFloatStateOf(0f) }
    var repCount by remember { mutableIntStateOf(1) }

    val activeDemo = demos[selectedDemoIndex]

    LaunchedEffect(isPlaying, speedMultiplier, selectedDemoIndex) {
        while (isPlaying) {
            delay(40L)
            val next = phaseProgress + 0.02f * speedMultiplier
            if (next >= 1f) {
                phaseProgress = 0f
                repCount = if (repCount >= activeDemo.targetReps) 1 else repCount + 1
            } else {
                phaseProgress = next
            }
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val accentColor = MaterialTheme.colorScheme.secondary

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
                title = "AI Exercise Video Coach",
                subtitle = "Guided Man & Woman form demos",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Coach & View Switchers
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterPillChip(
                                label = "AI Woman (Maya)",
                                selected = coachAvatar == "Maya",
                                onClick = { coachAvatar = "Maya" }
                            )
                            FilterPillChip(
                                label = "AI Man (Aarav)",
                                selected = coachAvatar == "Aarav",
                                onClick = { coachAvatar = "Aarav" }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterPillChip(
                                label = "Front view",
                                selected = viewAngle == "Front",
                                onClick = { viewAngle = "Front" }
                            )
                            FilterPillChip(
                                label = "Side view",
                                selected = viewAngle == "Side",
                                onClick = { viewAngle = "Side" }
                            )
                        }
                    }
                }

                // Live Animated Coaching Stage
                item {
                    BotanicalCard {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(activeDemo.title, style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = "Rep $repCount / ${activeDemo.targetReps}",
                                style = TabularMetricStyle,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Coach $coachAvatar · $viewAngle view · ${speedMultiplier}x",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val cx = size.width / 2f
                                val floorY = size.height * 0.85f
                                val pulse = sin(phaseProgress * 2f * PI).toFloat()
                                val squatOffset = if (selectedDemoIndex == 0) (pulse * 18f + 18f) else (pulse * 10f)
                                val headColor = if (coachAvatar == "Maya") primaryColor else accentColor

                                // Floor line
                                drawLine(
                                    color = primaryColor.copy(alpha = 0.25f),
                                    start = Offset(size.width * 0.15f, floorY),
                                    end = Offset(size.width * 0.85f, floorY),
                                    strokeWidth = 6f,
                                    cap = StrokeCap.Round
                                )

                                // Head
                                val headY = size.height * 0.24f + squatOffset
                                drawCircle(
                                    color = headColor,
                                    radius = 20f,
                                    center = Offset(cx, headY)
                                )
                                // Torso
                                val hipY = size.height * 0.54f + squatOffset * 1.2f
                                val torsoShiftX = if (viewAngle == "Side") 14f else 0f
                                drawLine(
                                    color = headColor,
                                    start = Offset(cx, headY + 20f),
                                    end = Offset(cx - torsoShiftX, hipY),
                                    strokeWidth = 14f,
                                    cap = StrokeCap.Round
                                )
                                // Arms
                                val armLift = if (selectedDemoIndex == 4) -35f * (pulse + 1f) * 0.5f else 18f
                                val armSpread = if (viewAngle == "Side") 34f else 54f
                                drawLine(
                                    color = headColor.copy(alpha = 0.85f),
                                    start = Offset(cx, headY + 34f),
                                    end = Offset(cx - armSpread, headY + 46f + armLift),
                                    strokeWidth = 10f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = headColor.copy(alpha = 0.85f),
                                    start = Offset(cx, headY + 34f),
                                    end = Offset(cx + armSpread, headY + 46f + armLift),
                                    strokeWidth = 10f,
                                    cap = StrokeCap.Round
                                )
                                // Legs
                                val legSpread = if (viewAngle == "Side") 18f else 32f
                                drawLine(
                                    color = Color(0xFF2B4C3E),
                                    start = Offset(cx - torsoShiftX, hipY),
                                    end = Offset(cx - legSpread, floorY),
                                    strokeWidth = 12f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = Color(0xFF2B4C3E),
                                    start = Offset(cx - torsoShiftX, hipY),
                                    end = Offset(cx + legSpread, floorY),
                                    strokeWidth = 12f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { phaseProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(9999.dp))
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { isPlaying = !isPlaying },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier.testTag("coach_play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isPlaying) "Pause" else "Play")
                            }
                            listOf(0.5f, 1.0f, 1.25f).forEach { spd ->
                                FilterPillChip(
                                    label = "${spd}x",
                                    selected = speedMultiplier == spd,
                                    onClick = { speedMultiplier = spd }
                                )
                            }
                        }
                    }
                }

                // AI Voice Cue
                item {
                    BotanicalCard(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Coach Cue ($coachAvatar)", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(activeDemo.voiceCue, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Exercise List
                item {
                    Text("Select Exercise Demo", style = MaterialTheme.typography.titleLarge)
                }
                itemsIndexed(demos) { index, demo ->
                    val selected = index == selectedDemoIndex
                    BotanicalCard(
                        containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.surfaceContainerLowest,
                        onClick = {
                            selectedDemoIndex = index
                            repCount = 1
                            phaseProgress = 0f
                        }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(demo.title, style = MaterialTheme.typography.titleMedium)
                                Text(demo.focus, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                shape = RoundedCornerShape(9999.dp)
                            ) {
                                Text(
                                    text = "${demo.targetReps} reps",
                                    style = TabularMetricStyle,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
