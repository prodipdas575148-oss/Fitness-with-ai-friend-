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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.example.data.AiCallState
import com.example.data.ConsultNote
import com.example.data.ConsultTriageResult
import com.example.data.UiState
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.components.FilterPillChip

@Composable
fun ConsultDoctorScreen(
    triageState: AiCallState<ConsultTriageResult>,
    consultNotesState: UiState<List<ConsultNote>>,
    onRunTriage: (String, String, String) -> Unit,
    onSaveConsultRequest: (String, String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var selectedProblem by remember { mutableStateOf("Chest tightness") }
    var selectedDuration by remember { mutableStateOf("Today") }
    var description by remember { mutableStateOf("") }

    val savedNotes = (consultNotesState as? UiState.Success)?.data.orEmpty()

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
                .imePadding()
        ) {
            BotanicalSubTopBar(
                title = "Consult Doctor",
                subtitle = "Serious symptom triage & doctor request",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Emergency ER Banner
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.LocalHospital, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Emergency? Call 108 / 112 / 911 immediately",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Text(
                                text = "Do not wait for an app response if you have crushing chest pain, severe shortness of breath, sudden weakness, confusion, or heavy bleeding.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                item {
                    BotanicalCard {
                        Text("What feels concerning?", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf("Chest tightness", "High fever", "Breathing trouble", "Severe stomach pain").forEach { chip ->
                                FilterPillChip(
                                    label = chip,
                                    selected = selectedProblem == chip,
                                    onClick = { selectedProblem = chip }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text("How long has this lasted?", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Today", "2–3 days", "Worsening").forEach { dur ->
                                FilterPillChip(
                                    label = dur,
                                    selected = selectedDuration == dur,
                                    onClick = { selectedDuration = dur }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { if (it.length <= 500) description = it },
                            label = { Text("Describe symptoms clearly (age, temperature, medications)") },
                            minLines = 3,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("consult_description_input")
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val text = description.trim().ifEmpty { "$selectedProblem ($selectedDuration)" }
                                onRunTriage(selectedProblem, selectedDuration, text)
                            },
                            enabled = triageState !is AiCallState.Loading,
                            shape = RoundedCornerShape(9999.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("consult_triage_button")
                        ) {
                            Text("Assess Urgency & Specialist")
                        }
                    }
                }

                when (triageState) {
                    is AiCallState.Loading -> {
                        item {
                            BotanicalCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Reviewing triage priority...")
                                }
                            }
                        }
                    }
                    is AiCallState.Success -> {
                        val triage = triageState.data
                        item {
                            BotanicalCard(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)) {
                                Text("Urgency: ${triage.urgencyLevel}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                                Text("Recommended Specialist: ${triage.recommendedSpecialist}", style = MaterialTheme.typography.labelLarge)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(triage.summaryGuidance, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                triage.whileWaitingSteps.forEach { step ->
                                    Text("• $step", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    is AiCallState.Error -> {
                        item {
                            Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(16.dp)) {
                                Text(triageState.message, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(14.dp))
                            }
                        }
                    }
                    else -> {}
                }

                // On-Call Doctors (persist consult request to Firestore)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("On-Call Doctors", style = MaterialTheme.typography.titleLarge)
                        DoctorActionCard(
                            name = "Dr. Meera Nair, MD",
                            specialty = "Internal Medicine · Available now",
                            onRequest = {
                                val summary = description.trim().ifEmpty { "$selectedProblem lasting $selectedDuration" }
                                onSaveConsultRequest(
                                    selectedProblem.lowercase().replace(" ", "_"),
                                    summary,
                                    selectedDuration,
                                    "Internal Medicine",
                                    "Dr. Meera Nair, MD"
                                )
                            }
                        )
                        DoctorActionCard(
                            name = "Dr. Rajesh Kulkarni, DM",
                            specialty = "Urgent Care & Cardiology · 5 min wait",
                            onRequest = {
                                val summary = description.trim().ifEmpty { "$selectedProblem lasting $selectedDuration" }
                                onSaveConsultRequest(
                                    selectedProblem.lowercase().replace(" ", "_"),
                                    summary,
                                    selectedDuration,
                                    "Urgent Care & Cardiology",
                                    "Dr. Rajesh Kulkarni, DM"
                                )
                            }
                        )
                    }
                }

                if (savedNotes.isNotEmpty()) {
                    item {
                        Text("Your Saved Consult Requests", style = MaterialTheme.typography.titleLarge)
                    }
                    items(savedNotes, key = { it.id }) { note ->
                        BotanicalCard {
                            Text(note.doctorName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text("${note.specialist} · ${note.durationLabel}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(note.summary, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DoctorActionCard(
    name: String,
    specialty: String,
    onRequest: () -> Unit
) {
    BotanicalCard {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.MedicalServices, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(name, style = MaterialTheme.typography.titleMedium)
                    Text(specialty, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            OutlinedButton(
                onClick = onRequest,
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Send Note")
            }
        }
    }
}
