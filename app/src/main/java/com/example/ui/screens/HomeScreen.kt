package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.RoutineTask
import com.example.data.UiState
import com.example.data.UserWellnessProfile
import com.example.ui.components.BotanicalCard
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.FilterPillChip
import com.example.ui.components.PrivatePillBadge
import com.example.ui.components.UrgentCareBanner
import com.example.ui.navigation.Routes
import com.example.ui.theme.TabularMetricStyle

private data class HomeGuideCardItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String,
    val tag: String
)

@Composable
fun HomeScreen(
    profileState: UiState<UserWellnessProfile?>,
    routineTasksState: UiState<List<RoutineTask>>,
    onNavigate: (String) -> Unit,
    onOpenProfileDialog: () -> Unit,
    onQuickWaterAdd: (Long, Long) -> Unit,
    onSearchSubmit: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTopicHint by remember { mutableStateOf<String?>(null) }

    val profile = (profileState as? UiState.Success)?.data
    val tasks = (routineTasksState as? UiState.Success)?.data.orEmpty()
    val waterGlasses = profile?.waterGlasses ?: 5L
    val waterGoal = (profile?.waterGoal ?: 8L).coerceAtLeast(1L)
    val steps = profile?.steps ?: 4200L
    val doneCount = tasks.count { it.done }

    val careGuides = remember {
        listOf(
            HomeGuideCardItem("Symptom Check", "Calm AI guidance", Icons.Filled.Favorite, Routes.SYMPTOM_CHAT, "guide_symptom_check"),
            HomeGuideCardItem("Skin & Face", "Photo & care tips", Icons.Filled.Face, Routes.PHOTO_ANALYSIS, "guide_skin_face"),
            HomeGuideCardItem("Hair & Scalp", "Dandruff & shedding", Icons.Filled.ContentCut, Routes.PHOTO_ANALYSIS, "guide_hair_scalp"),
            HomeGuideCardItem("Nutrition & Diet", "Skin & energy meals", Icons.Filled.Restaurant, Routes.NUTRITION_DIET, "guide_nutrition_diet"),
            HomeGuideCardItem("Medicines", "Safe OTC info", Icons.Filled.Medication, Routes.MEDICINE_INFO, "guide_medicines"),
            HomeGuideCardItem("Home Remedies", "Gentle home care", Icons.Filled.Eco, Routes.HOME_REMEDIES, "guide_home_remedies")
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
            // Sticky Header matching home.html
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Spa,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "HealthMate AI",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Private wellness guide",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        PrivatePillBadge()
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onOpenProfileDialog,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("home_profile_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Profile and account",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Search Bar + Quick Topic Chips
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search symptoms, medicines, skin care...") },
                            leadingIcon = {
                                Icon(Icons.Filled.Search, contentDescription = "Search")
                            },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    TextButton(
                                        onClick = {
                                            val q = searchQuery.trim()
                                            searchQuery = ""
                                            onSearchSubmit(q)
                                        },
                                        modifier = Modifier.testTag("home_search_ask_button")
                                    ) {
                                        Text("Ask AI")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(9999.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                if (searchQuery.isNotBlank()) {
                                    val q = searchQuery.trim()
                                    searchQuery = ""
                                    onSearchSubmit(q)
                                }
                            }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_search_input")
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            val quickTopics = listOf(
                                "Acne & dark spots" to "Gentle non-comedogenic cleanser, niacinamide + SPF 50 daily helps calm breakouts and dark marks.",
                                "Hair fall & dandruff" to "Wash scalp 2–3x weekly with lukewarm water and check ferritin, protein, and zinc intake.",
                                "Skin & hair diet" to "Pair walnuts, soaked chia, bell peppers, lentils, and 8 glasses of water daily."
                            )
                            quickTopics.forEach { (label, hint) ->
                                FilterPillChip(
                                    label = label,
                                    selected = selectedTopicHint == hint,
                                    onClick = {
                                        selectedTopicHint = if (selectedTopicHint == hint) null else hint
                                    }
                                )
                            }
                        }

                        if (!selectedTopicHint.isNullOrBlank()) {
                            BotanicalCard(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
                            ) {
                                Text(
                                    text = selectedTopicHint!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                TextButton(
                                    onClick = { onNavigate(Routes.SYMPTOM_CHAT) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Ask follow-up in Symptom Chat →")
                                }
                            }
                        }
                    }
                }

                // AI Wellness Assistant Hero Card
                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AI Wellness Assistant",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text(
                                    text = "Calm guidance for symptoms, skin, hair, and daily care",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { onNavigate(Routes.SYMPTOM_CHAT) },
                                        shape = RoundedCornerShape(9999.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("hero_ask_healthmate_button")
                                    ) {
                                        Text("Ask HealthMate", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    OutlinedButton(
                                        onClick = { onNavigate(Routes.PHOTO_ANALYSIS) },
                                        shape = RoundedCornerShape(9999.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onPrimary
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("hero_upload_photo_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CameraAlt,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Upload photo", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                        }
                    }
                }

                // Today's Care & Hydration Live Synced Card
                item {
                    BotanicalCard {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Today's Hydration & Routine",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$doneCount/${tasks.size.coerceAtLeast(1)} habits done · $steps steps",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(9999.dp)
                            ) {
                                Text(
                                    text = "$waterGlasses/$waterGoal glasses",
                                    style = TabularMetricStyle,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (waterGlasses.toFloat() / waterGoal.toFloat()).coerceIn(0f, 1f) },
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(9999.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onQuickWaterAdd((waterGlasses + 1L).coerceAtMost(20L), steps) },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier.testTag("home_log_water_button")
                            ) {
                                Icon(Icons.Filled.LocalDrink, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log 1 glass")
                            }
                            OutlinedButton(
                                onClick = { onNavigate(Routes.TRACKER) },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier.testTag("home_open_tracker_button")
                            ) {
                                Text("Open Tracker")
                            }
                        }
                    }
                }

                // Quick Smart Tools Row
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Smart Scanners & Checks",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            QuickToolTile("Ask Any Doubt", Icons.Filled.HelpOutline, Routes.ASK_DOUBT, "tool_ask_doubt", onNavigate)
                            QuickToolTile("Food & Product Scan", Icons.Filled.QrCodeScanner, Routes.FOOD_SCANNER, "tool_food_scan", onNavigate)
                            QuickToolTile("BP Checker", Icons.Filled.MonitorHeart, Routes.BP_CHECKER, "tool_bp_checker", onNavigate)
                            QuickToolTile("Gym Guide", Icons.Filled.FitnessCenter, Routes.GYM_GUIDE, "tool_gym_guide", onNavigate)
                            QuickToolTile("Weight & Fat Loss", Icons.AutoMirrored.Filled.DirectionsRun, Routes.WEIGHT_LOSS, "tool_weight_loss", onNavigate)
                        }
                    }
                }

                // 6 Care Guides 2-Column Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Care Guides",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        careGuides.chunked(2).forEach { pair ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                pair.forEach { guide ->
                                    BotanicalCard(
                                        onClick = { onNavigate(guide.route) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag(guide.tag)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                        ) {
                                            Icon(
                                                imageVector = guide.icon,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = guide.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = guide.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Fitness, Style & Plans Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Fitness, Style & Plans",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        BotanicalCard(
                            onClick = { onNavigate(Routes.AI_VIDEO_COACH) },
                            modifier = Modifier.testTag("home_ai_video_coach_card")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Icon(Icons.Filled.OndemandVideo, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("AI Exercise Video Coach", style = MaterialTheme.typography.titleMedium)
                                    Text("Guided Man & Woman form demos with voice cues", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        BotanicalCard(
                            onClick = { onNavigate(Routes.BODY_COLOR_MATCH) },
                            modifier = Modifier.testTag("home_body_color_card")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Icon(Icons.Filled.Checkroom, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Body Color, Dress & Clutch Match", style = MaterialTheme.typography.titleMedium)
                                    Text("Undertone scan & flattering outfit palette", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        BotanicalCard(
                            onClick = { onNavigate(Routes.PLANS) },
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.testTag("home_plans_card")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("₹49 Plus Plan & Free Wellness", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = if (profile?.planTier == "plus_49") "Plus Active · All other core guides free forever"
                                        else "Core wellness free · Upgrade optional for ₹49/mo",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Disclaimer & Urgent Care Banners
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DisclaimerBanner()
                        UrgentCareBanner(onConsultDoctor = { onNavigate(Routes.CONSULT_DOCTOR) })
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickToolTile(
    title: String,
    icon: ImageVector,
    route: String,
    tag: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
        modifier = Modifier
            .width(142.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onNavigate(route) }
            .testTag(tag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
