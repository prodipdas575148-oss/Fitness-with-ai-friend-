package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.AiCallState
import com.example.data.BitmapHelper
import com.example.data.ColorMatchResult
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.components.FilterPillChip
import kotlinx.coroutines.launch

@Composable
fun BodyColorMatchScreen(
    colorMatchState: AiCallState<ColorMatchResult>,
    onAnalyzePhoto: (android.graphics.Bitmap) -> Unit,
    onSelectUndertone: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedSwatchIndex by remember { mutableIntStateOf(0) }
    var selectedFaceShape by remember { mutableStateOf("Oval") }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            scope.launch {
                val bmp = BitmapHelper.decodeScaledBitmap(context, uri, 1024)
                if (bmp != null) {
                    onAnalyzePhoto(bmp)
                }
            }
        }
    }

    val dressAndClutchCopy = remember(selectedFaceShape) {
        when (selectedFaceShape) {
            "Round" -> "V-necks, wrap dresses, and structured rectangular or envelope clutches elongate your silhouette and sharpen lines."
            "Square" -> "Scoop necklines, flowing A-line dresses, and soft rounded pouch clutches balance strong shoulder and jaw angles."
            else -> "Balanced proportions suit square necklines, belted midis, and both sleek box clutches and soft pleated clutches."
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
                title = "Body & Color Match",
                subtitle = "Undertone palette, dress & clutch guide",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    BotanicalCard {
                        if (selectedUri != null) {
                            AsyncImage(
                                model = selectedUri,
                                contentDescription = "Selected skin tone reference",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        Text("Analyze Natural Skin Undertone", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Upload a daylight wrist or portrait photo, or switch undertones below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            shape = RoundedCornerShape(9999.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("color_match_upload_button")
                        ) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Photo for Undertone")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            listOf("Warm Golden", "Cool Rosy", "Neutral Olive").forEach { tone ->
                                val active = (colorMatchState as? AiCallState.Success)?.data?.undertone == tone
                                FilterPillChip(
                                    label = tone,
                                    selected = active,
                                    onClick = {
                                        selectedSwatchIndex = 0
                                        onSelectUndertone(tone)
                                    }
                                )
                            }
                        }
                    }
                }

                when (colorMatchState) {
                    is AiCallState.Loading -> {
                        item {
                            BotanicalCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Matching undertone & dress palette...")
                                }
                            }
                        }
                    }
                    is AiCallState.Success -> {
                        val res = colorMatchState.data
                        val safeIdx = selectedSwatchIndex.coerceIn(0, (res.recommendedSwatches.size - 1).coerceAtLeast(0))
                        val activeSwatch = res.recommendedSwatches.getOrNull(safeIdx)
                        item {
                            BotanicalCard {
                                Text("Detected Undertone: ${res.undertone}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(res.undertoneNote, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text("Your 5 Signature Dress & Clutch Shades", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    res.recommendedSwatches.forEachIndexed { index, swatch ->
                                        val parsedColor = parseHexSafely(swatch.hex, MaterialTheme.colorScheme.primary)
                                        val isPicked = index == safeIdx
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape)
                                                .background(parsedColor)
                                                .clickable { selectedSwatchIndex = index }
                                                .testTag("color_swatch_$index")
                                        ) {
                                            if (isPicked) {
                                                Icon(Icons.Filled.Check, contentDescription = swatch.name, tint = Color.White)
                                            }
                                        }
                                    }
                                }
                                if (activeSwatch != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("${activeSwatch.name} (${activeSwatch.hex})", style = MaterialTheme.typography.titleMedium)
                                            Text(activeSwatch.reason, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Use Sparingly", style = MaterialTheme.typography.titleMedium)
                                Text(res.avoidColors, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Jewelry & Clutch Finish", style = MaterialTheme.typography.titleMedium)
                                Text(res.jewelryTip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                Text(colorMatchState.message, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(14.dp))
                            }
                        }
                    }
                    else -> {}
                }

                // Face Shape Dress & Clutch Recommender
                item {
                    BotanicalCard {
                        Text("Dress Neckline & Clutch Silhouette", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Oval", "Round", "Square").forEach { shape ->
                                FilterPillChip(
                                    label = shape,
                                    selected = selectedFaceShape == shape,
                                    onClick = { selectedFaceShape = shape }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(dressAndClutchCopy, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

private fun parseHexSafely(hex: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex.trim()))
    } catch (_: Exception) {
        fallback
    }
}
