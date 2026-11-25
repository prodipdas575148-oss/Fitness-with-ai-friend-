package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.UiState
import com.example.data.UserWellnessProfile
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.theme.TabularMetricStyle

@Composable
fun PlansScreen(
    profileState: UiState<UserWellnessProfile?>,
    onSelectPlan: (String) -> Unit,
    onBack: () -> Unit
) {
    val currentTier = (profileState as? UiState.Success)?.data?.planTier ?: "free"

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
                title = "Plans & Guidance Fee",
                subtitle = "Transparent ₹49 Plus & Free Forever tools",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // ₹49 Plus Plan Card
                item {
                    BotanicalCard(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                        borderColor = MaterialTheme.colorScheme.primary
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("HealthMate Plus", style = MaterialTheme.typography.headlineMedium)
                            }
                            Text("₹49 / mo", style = TabularMetricStyle, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Covers the 3 specialized AI & personal guidance modules:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        listOf(
                            "AI Exercise Video Coach (Man & Woman form demos)",
                            "Body Color, Dress & Clutch Undertone Match",
                            "Skin & Hair Photo Care Deep Reports"
                        ).forEach { feat ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(feat, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        if (currentTier == "plus_49") {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Active Plan · HealthMate Plus",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = { onSelectPlan("plus_49") },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("activate_plus_49_button")
                            ) {
                                Text("Activate HealthMate Plus (₹49/month)")
                            }
                        }
                    }
                }

                // Free Forever Plan Card
                item {
                    BotanicalCard {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("All Other Features Free", style = MaterialTheme.typography.headlineMedium)
                            Text("₹0 Forever", style = TabularMetricStyle, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf(
                            "AI Symptom Checker & Ask Any Doubt Q&A",
                            "Blood Pressure Checker & Urgent Doctor Triage",
                            "Hydration, Footsteps, Stopwatch & Routine Tracker",
                            "Daily Exercise & Diet Charts, Gym & Fat Loss Guides",
                            "Home Remedies, Medicines Info & Nutrition Plans"
                        ).forEach { freeFeat ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(freeFeat, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        if (currentTier == "plus_49") {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { onSelectPlan("free") },
                                shape = RoundedCornerShape(9999.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("switch_to_free_button")
                            ) {
                                Text("Switch back to Free Plan")
                            }
                        }
                    }
                }
            }
        }
    }
}
