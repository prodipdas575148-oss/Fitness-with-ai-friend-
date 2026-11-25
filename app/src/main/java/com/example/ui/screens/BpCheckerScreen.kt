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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.BpReading
import com.example.data.UiState
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.components.FilterPillChip
import com.example.ui.components.UrgentCareBanner
import com.example.ui.theme.TabularMetricStyle

@Composable
fun BpCheckerScreen(
    bpReadingsState: UiState<List<BpReading>>,
    onSaveReading: (Int, Int, Int?, String) -> Unit,
    onConsultDoctor: () -> Unit,
    onBack: () -> Unit
) {
    var systolicText by remember { mutableStateOf("118") }
    var diastolicText by remember { mutableStateOf("76") }
    var pulseText by remember { mutableStateOf("72") }

    val sys = systolicText.toIntOrNull()
    val dia = diastolicText.toIntOrNull()
    val pulse = pulseText.toIntOrNull()?.takeIf { it in 30..220 }

    val evaluation = remember(sys, dia) { evaluateBp(sys, dia) }
    val readings = (bpReadingsState as? UiState.Success)?.data.orEmpty()

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
                title = "Blood Pressure Checker",
                subtitle = "AHA category check & private reading log",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        FilterPillChip(
                            label = "118/76 Normal",
                            selected = sys == 118 && dia == 76,
                            onClick = { systolicText = "118"; diastolicText = "76"; pulseText = "72" }
                        )
                        FilterPillChip(
                            label = "134/86 High",
                            selected = sys == 134 && dia == 86,
                            onClick = { systolicText = "134"; diastolicText = "86"; pulseText = "78" }
                        )
                        FilterPillChip(
                            label = "184/122 Urgent",
                            selected = sys == 184 && dia == 122,
                            onClick = { systolicText = "184"; diastolicText = "122"; pulseText = "94" }
                        )
                    }
                }

                item {
                    BotanicalCard {
                        Text("Enter Cuff Reading", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = systolicText,
                                onValueChange = { if (it.length <= 3) systolicText = it.filter(Char::isDigit) },
                                label = { Text("Systolic") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("bp_systolic_input")
                            )
                            OutlinedTextField(
                                value = diastolicText,
                                onValueChange = { if (it.length <= 3) diastolicText = it.filter(Char::isDigit) },
                                label = { Text("Diastolic") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("bp_diastolic_input")
                            )
                            OutlinedTextField(
                                value = pulseText,
                                onValueChange = { if (it.length <= 3) pulseText = it.filter(Char::isDigit) },
                                label = { Text("Pulse") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("bp_pulse_input")
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                if (sys != null && dia != null && sys in 40..300 && dia in 30..200) {
                                    onSaveReading(sys, dia, pulse, evaluation.first)
                                }
                            },
                            enabled = sys != null && dia != null && sys in 40..300 && dia in 30..200,
                            shape = RoundedCornerShape(9999.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bp_save_button")
                        ) {
                            Text("Save Reading to Private Log")
                        }
                    }
                }

                item {
                    if (evaluation.third) {
                        UrgentCareBanner(
                            title = "Hypertensive Crisis Range (≥ 180/120)",
                            body = evaluation.second,
                            onConsultDoctor = onConsultDoctor
                        )
                    } else {
                        BotanicalCard(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)) {
                            Text(
                                text = "Category: ${evaluation.first}",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(evaluation.second, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                item {
                    Text("Saved BP Readings", style = MaterialTheme.typography.titleLarge)
                }

                if (readings.isEmpty()) {
                    item {
                        BotanicalCard(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                            Text(
                                text = "No saved readings yet. Tap Save Reading above to track your numbers over time.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(readings, key = { it.id }) { reading ->
                        BotanicalCard {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = "${reading.systolic}/${reading.diastolic} mmHg",
                                        style = TabularMetricStyle,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    val pulseNote = reading.pulse?.let { " · Pulse $it bpm" } ?: ""
                                    Text(
                                        text = "${reading.category}$pulseNote",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Surface(
                                    color = if (reading.category.contains("Crisis"))
                                        MaterialTheme.colorScheme.errorContainer
                                    else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(9999.dp)
                                ) {
                                    Text(
                                        text = reading.category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (reading.category.contains("Crisis"))
                                            MaterialTheme.colorScheme.onErrorContainer
                                        else MaterialTheme.colorScheme.onSecondaryContainer,
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
}

private fun evaluateBp(sys: Int?, dia: Int?): Triple<String, String, Boolean> {
    if (sys == null || dia == null) {
        return Triple("Enter reading", "Enter valid systolic (40–300) and diastolic (30–200) numbers.", false)
    }
    return when {
        sys >= 180 || dia >= 120 -> Triple(
            "Hypertensive Crisis",
            "Wait 5 minutes and re-check if calm. If BP remains 180/120 or higher—especially with chest pain, headache, or vision changes—seek emergency care right away.",
            true
        )
        sys >= 140 || dia >= 90 -> Triple(
            "High (Stage 2)",
            "Consistently elevated range. Share a 7-day reading log with your doctor and avoid high-salt meals or missed doses.",
            false
        )
        sys >= 130 || dia >= 80 -> Triple(
            "High (Stage 1)",
            "Mildly elevated. Daily brisk walking, lower sodium intake, stress breathing, and regular cuff tracking help.",
            false
        )
        sys >= 120 && dia < 80 -> Triple(
            "Elevated",
            "Slightly above ideal. Keep resting 5 minutes seated before measuring, hydrate well, and limit processed snacks.",
            false
        )
        sys < 90 || dia < 60 -> Triple(
            "Low Reading",
            "If accompanied by dizziness or fainting, sit/lie down, sip fluids, and consult a clinician.",
            false
        )
        else -> Triple(
            "Normal",
            "Healthy blood pressure range. Maintain steady sleep, daily movement, and balanced hydration.",
            false
        )
    }
}
