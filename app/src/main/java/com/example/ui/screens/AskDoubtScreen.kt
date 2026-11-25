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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.example.data.AiCallState
import com.example.data.HealthAiService
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.FilterPillChip
import com.example.ui.components.UrgentCareBanner

@Composable
fun AskDoubtScreen(
    doubtState: AiCallState<String>,
    onAskDoubt: (String) -> Unit,
    onConsultDoctor: () -> Unit,
    onBack: () -> Unit
) {
    var question by remember { mutableStateOf("") }
    var lastAsked by remember { mutableStateOf("") }

    val popularDoubts = remember {
        listOf(
            "Why does my skin feel dry after cleansing?",
            "How much protein helps hair growth daily?",
            "Can I combine niacinamide and vitamin C?",
            "Best evening drink for bloating and acidity?"
        )
    }

    val isUrgent = HealthAiService.detectEmergencyKeywords(lastAsked)

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
                title = "Ask Any Doubt",
                subtitle = "Clear everyday health & skin Q&A",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    BotanicalCard {
                        Text("Ask HealthMate AI", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = question,
                            onValueChange = { question = it },
                            placeholder = { Text("Type any wellness, skincare, diet, or routine doubt...") },
                            minLines = 3,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("doubt_question_input")
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val q = question.trim()
                                if (q.isNotEmpty()) {
                                    lastAsked = q
                                    onAskDoubt(q)
                                }
                            },
                            enabled = question.isNotBlank() && doubtState !is AiCallState.Loading,
                            shape = RoundedCornerShape(9999.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("doubt_submit_button")
                        ) {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Get Clear Answer")
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Popular Doubts", style = MaterialTheme.typography.titleMedium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            popularDoubts.forEach { preset ->
                                FilterPillChip(
                                    label = preset,
                                    selected = lastAsked == preset,
                                    onClick = {
                                        question = preset
                                        lastAsked = preset
                                        onAskDoubt(preset)
                                    }
                                )
                            }
                        }
                    }
                }

                if (isUrgent) {
                    item { UrgentCareBanner(onConsultDoctor = onConsultDoctor) }
                }

                when (doubtState) {
                    is AiCallState.Idle -> {
                        item {
                            BotanicalCard(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                                Text(
                                    text = "Tap a popular question or enter your own above to receive a calm, plain-English answer with 3 safe daily steps.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    is AiCallState.Loading -> {
                        item {
                            BotanicalCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Preparing clear guidance...", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                    is AiCallState.Streaming -> {
                        item {
                            BotanicalCard {
                                Text("Answer", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(doubtState.text, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    is AiCallState.Success -> {
                        item {
                            BotanicalCard {
                                Text("Clear Wellness Answer", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(doubtState.data, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    is AiCallState.Error -> {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = doubtState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }

                item { DisclaimerBanner() }
            }
        }
    }
}
