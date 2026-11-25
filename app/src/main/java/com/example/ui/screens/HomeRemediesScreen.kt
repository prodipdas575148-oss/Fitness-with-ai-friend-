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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.unit.dp
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.FilterPillChip

private data class RemedyItem(
    val title: String,
    val category: String,
    val safeCare: String,
    val avoidWarning: String
)

@Composable
fun HomeRemediesScreen(onBack: () -> Unit) {
    val remedies = remember {
        listOf(
            RemedyItem("Warm Salt Water Gargle", "Health & Cold", "Dissolve 1/2 tsp salt in warm water and gargle gently 2–3 times daily to soothe throat irritation.", "Do not swallow salty water; skip for very young children."),
            RemedyItem("Steam Inhalation + Honey Tea", "Health & Cold", "Breathe mild warm steam at a safe distance and sip ginger-honey warm water (adults/children over age 1).", "Never give honey to infants under 12 months; keep hot bowls away from faces."),
            RemedyItem("Pure Aloe Vera Soothing Gel", "Skin & Face", "Apply fragrance-free aloe gel after cleansing to calm mild sun warmth or dryness.", "Patch test first; avoid on open or oozing skin."),
            RemedyItem("Colloidal Oat Calm Compress", "Skin & Face", "Steep plain oats in cool water and dab gently on itchy, dry skin patches.", "Avoid scrubbing broken skin or using hot water."),
            RemedyItem("Lukewarm Scalp Rinse + Gentle Massage", "Hair & Scalp", "Massage scalp gently with fingertips before washing with mild cleanser twice weekly.", "Avoid leaving heavy oils overnight on active dandruff."),
            RemedyItem("Warm Jeera / Fennel Water", "Digestion", "Sip mild boiled cumin or fennel seed water slowly after heavy meals to ease mild bloating.", "Seek medical care if abdominal pain is sharp, persistent, or paired with fever.")
        )
    }

    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }

    val filtered = remember(search, category) {
        remedies.filter { item ->
            (category == "All" || item.category == category) &&
                (search.isBlank() || item.title.contains(search, true) || item.safeCare.contains(search, true))
        }
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
                title = "Safe Home Remedies",
                subtitle = "Gentle home comfort with clear safety limits",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = { Text("Search remedies (throat, skin, bloating)...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(9999.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf("All", "Health & Cold", "Skin & Face", "Hair & Scalp", "Digestion").forEach { cat ->
                            FilterPillChip(label = cat, selected = category == cat, onClick = { category = cat })
                        }
                    }
                }

                items(filtered, key = { it.title }) { item ->
                    BotanicalCard {
                        Text(item.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(item.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Safe care: ${item.safeCare}", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Avoid: ${item.avoidWarning}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                item { DisclaimerBanner() }
            }
        }
    }
}
