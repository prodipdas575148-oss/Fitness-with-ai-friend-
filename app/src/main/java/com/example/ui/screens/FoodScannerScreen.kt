package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.AiCallState
import com.example.data.BitmapHelper
import com.example.data.FoodScanResult
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.components.FilterPillChip
import com.example.ui.theme.TabularMetricStyle
import kotlinx.coroutines.launch

@Composable
fun FoodScannerScreen(
    scanState: AiCallState<FoodScanResult>,
    onScanBitmap: (android.graphics.Bitmap, String) -> Unit,
    onSelectPreset: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mode by remember { mutableStateOf("food") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            scope.launch {
                val bitmap = BitmapHelper.decodeScaledBitmap(context, uri, 1024)
                if (bitmap != null) {
                    onScanBitmap(bitmap, mode)
                }
            }
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
                title = "Food & Product Scanner",
                subtitle = "Meals, nutrition labels & skincare ingredients",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterPillChip(
                            label = "Food & Meal",
                            selected = mode == "food",
                            icon = Icons.Filled.Restaurant,
                            onClick = { mode = "food" }
                        )
                        FilterPillChip(
                            label = "Product Label",
                            selected = mode == "product",
                            icon = Icons.Filled.Spa,
                            onClick = { mode = "product" }
                        )
                    }
                }

                item {
                    BotanicalCard {
                        if (selectedUri != null) {
                            AsyncImage(
                                model = selectedUri,
                                contentDescription = "Scanned item photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        Text(
                            text = if (mode == "food") "Upload a meal plate or packaged nutrition label" else "Upload a skincare, cleanser, or hair label",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                picker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(9999.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scanner_pick_photo_button")
                        ) {
                            Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Photo with AI")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Try Sample Presets", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            FilterPillChip(
                                label = "Avocado Toast & Egg",
                                selected = false,
                                onClick = { selectedUri = null; mode = "food"; onSelectPreset("avocado") }
                            )
                            FilterPillChip(
                                label = "Honey Oat Granola Bar",
                                selected = false,
                                onClick = { selectedUri = null; mode = "food"; onSelectPreset("granola") }
                            )
                            FilterPillChip(
                                label = "Gentle Hydrating Cleanser",
                                selected = false,
                                onClick = { selectedUri = null; mode = "product"; onSelectPreset("cleanser") }
                            )
                        }
                    }
                }

                when (scanState) {
                    is AiCallState.Loading -> {
                        item {
                            BotanicalCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Reading nutrition and ingredients...")
                                }
                            }
                        }
                    }
                    is AiCallState.Success -> {
                        val item = scanState.data
                        item {
                            BotanicalCard {
                                Text(item.itemName, style = MaterialTheme.typography.headlineMedium)
                                Text(item.servingLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    MacroBadge("Calories", item.calories, Modifier.weight(1f))
                                    MacroBadge("Protein", item.proteinGrams, Modifier.weight(1f))
                                    MacroBadge("Carbs", item.carbsGrams, Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    MacroBadge("Fats", item.fatGrams, Modifier.weight(1f))
                                    MacroBadge("Fiber", item.fiberGrams, Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Key Ingredients", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.ingredients.joinToString(" · "), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Wellness Insight", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Text(item.wellnessNote, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Watchout: ${item.cautionNote}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
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
                                Text(scanState.message, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(14.dp))
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun MacroBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = TabularMetricStyle, color = MaterialTheme.colorScheme.primary)
        }
    }
}
