package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.GoogleSignInButton
import com.example.ui.components.BotanicalCard
import com.example.ui.components.PrivatePillBadge

@Composable
fun WelcomeAuthScreen(
    onAuthSuccess: () -> Unit
) {
    var authError by remember { mutableStateOf<String?>(null) }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Top bar: Brand + 100% Private pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Spa,
                            contentDescription = "HealthMate AI emblem",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "HealthMate AI",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                PrivatePillBadge(text = "100% Private")
            }

            // Botanical hero card with subtle Canvas ambient leaf/glow art
            val primaryColor = MaterialTheme.colorScheme.primary
            val containerColor = MaterialTheme.colorScheme.primaryContainer
            val softHighlight = MaterialTheme.colorScheme.secondaryContainer
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Canvas(
                        modifier = Modifier
                            .matchParentSize()
                    ) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(softHighlight.copy(alpha = 0.55f), Color.Transparent),
                                center = Offset(size.width * 0.86f, size.height * 0.18f),
                                radius = size.minDimension * 0.65f
                            ),
                            center = Offset(size.width * 0.86f, size.height * 0.18f),
                            radius = size.minDimension * 0.65f
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(containerColor.copy(alpha = 0.14f), Color.Transparent),
                                center = Offset(size.width * 0.14f, size.height * 0.88f),
                                radius = size.minDimension * 0.5f
                            ),
                            center = Offset(size.width * 0.14f, size.height * 0.88f),
                            radius = size.minDimension * 0.5f
                        )
                    }
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(9999.dp)
                        ) {
                            Text(
                                text = "BOTANICAL CLARITY WELLNESS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                        Text(
                            text = "Your daily guide to clear health, skin, and fitness",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Check symptoms calmly, scan meals and skincare labels, track daily habits, and sync everything privately with your Google account.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 3 Feature highlights matching welcome.html
            WelcomeFeatureRow(
                icon = Icons.Filled.AutoAwesome,
                title = "Instant AI & Doctor Support",
                subtitle = "Symptom guidance chat, Ask Any Doubt Q&A, BP checks, and urgent doctor triage.",
                tint = primaryColor
            )
            WelcomeFeatureRow(
                icon = Icons.Filled.Palette,
                title = "Skin, Fitness & Style Match",
                subtitle = "Skin & hair photo observations, AI exercise coach, and Body Color, Dress & Clutch match.",
                tint = MaterialTheme.colorScheme.secondary
            )
            WelcomeFeatureRow(
                icon = Icons.Filled.VerifiedUser,
                title = "Core Wellness Tools Free",
                subtitle = "Symptom check, nutrition plans, remedies, medicines & routine tracker free forever. Optional ₹49/mo Plus plan.",
                tint = MaterialTheme.colorScheme.primaryContainer
            )

            if (!authError.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_error_banner")
                ) {
                    Text(
                        text = authError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            GoogleSignInButton(
                label = "Get Started — Sign in with Google",
                onAuthSuccess = {
                    authError = null
                    onAuthSuccess()
                },
                onAuthError = { msg ->
                    authError = msg
                }
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Educational wellness guidance · Not a medical diagnosis",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun WelcomeFeatureRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color
) {
    BotanicalCard {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
