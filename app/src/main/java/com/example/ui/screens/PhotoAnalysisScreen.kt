package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.PhotoAnalysisResult
import com.example.ui.components.BotanicalCard
import com.example.ui.components.BotanicalSubTopBar
import com.example.ui.components.DisclaimerBanner
import com.example.ui.components.FilterPillChip
import com.example.ui.components.UrgentCareBanner
import kotlinx.coroutines.launch

@Composable
fun PhotoAnalysisScreen(
    photoState: AiCallState<PhotoAnalysisResult>,
    onAnalyzeBitmap: (android.graphics.Bitmap, String) -> Unit,
    onLoadSampleObservation: (String) -> Unit,
    onConsultDoctor: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mode by remember { mutableStateOf("skin") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            scope.launch {
                val bitmap = BitmapHelper.decodeScaledBitmap(context, uri, 1024)
                if (bitmap != null) {
                    onAnalyzeBitmap(bitmap, mode)
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
                title = "Skin & Hair Photo Check",
                subtitle = "Educational visual cues · Not a diagnosis",
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
                            label = "Skin & Face",
                            selected = mode == "skin",
                            icon = Icons.Filled.Face,
                            onClick = {
                                mode = "skin"
                                if (selectedUri == null) onLoadSampleObservation("skin")
                            },
                            modifier = Modifier.testTag("photo_mode_skin")
                        )
                        FilterPillChip(
                            label = "Hair & Scalp",
                            selected = mode == "hair",
                            icon = Icons.Filled.ContentCut,
                            onClick = {
                                mode = "hair"
                                if (selectedUri == null) onLoadSampleObservation("hair")
                            },
                            modifier = Modifier.testTag("photo_mode_hair")
                        )
                    }
                }

                item {
                    BotanicalCard {
                        if (selectedUri != null) {
                            AsyncImage(
                                model = selectedUri,
                                contentDescription = "Uploaded wellness photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        Text(
                            text = if (mode == "skin") "Upload a clear daylight face photo" else "Upload a clear daylight scalp or hair photo",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Photos are processed securely by AI for gentle educational care tips and are not stored permanently.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    picker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("photo_upload_button")
                            ) {
                                Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Choose Photo")
                            }
                            OutlinedButton(
                                onClick = {
                                    selectedUri = null
                                    onLoadSampleObservation(mode)
                                },
                                shape = RoundedCornerShape(9999.dp),
                                modifier = Modifier.testTag("photo_sample_button")
                            ) {
                                Text("Sample")
                            }
                        }
                    }
                }

                when (photoState) {
                    is AiCallState.Loading -> {
                        item {
                            BotanicalCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Reviewing visual cues with HealthMate AI...")
                                }
                            }
                        }
                    }
                    is AiCallState.Success -> {
                        val res = photoState.data
                        if (res.urgentAlert) {
                            item {
                                UrgentCareBanner(
                                    title = "Dermatologist review advised",
                                    body = res.whenToSeeDoctor,
                                    onConsultDoctor = onConsultDoctor
                                )
                            }
                        }
                        item {
                            BotanicalCard {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(9999.dp)
                                ) {
                                    Text(
                                        text = "Educational Observation · Not a Diagnosis",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(res.observationSummary, style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Visible Cues", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(6.dp))
                                res.visualCues.forEach { cue ->
                                    Text("• $cue", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Gentle Daily Care", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(6.dp))
                                res.gentleCareSteps.forEach { step ->
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircleOutline,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .padding(top = 2.dp)
                                                .size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(step, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("When to see a doctor", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(res.whenToSeeDoctor, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    text = photoState.message,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                    else -> {}
                }

                item { DisclaimerBanner() }
            }
        }
    }
}
