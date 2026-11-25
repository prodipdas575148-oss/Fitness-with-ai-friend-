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

private data class MedicineReference(
    val name: String,
    val category: String,
    val commonUse: String,
    val safetyNote: String,
    val doctorRequired: Boolean = false
)

@Composable
fun MedicineInfoScreen(onBack: () -> Unit) {
    val items = remember {
        listOf(
            MedicineReference("Paracetamol (Acetaminophen)", "Pain & Fever", "Commonly used for mild fever, headache, or body ache.", "Follow pack dosing strictly; avoid doubling up with combination cold syrups to protect liver health."),
            MedicineReference("Cetirizine", "Allergy", "Non-sedating antihistamine for seasonal sneezing, hives, or mild allergy itching.", "May cause mild drowsiness in some adults; check pharmacist before driving."),
            MedicineReference("Ketoconazole 2% Shampoo", "Skin & Scalp", "Medicated antifungal wash often recommended for stubborn flaky dandruff.", "Apply to scalp for 3–5 minutes 2x/week and rinse gently; avoid eye contact."),
            MedicineReference("Antibiotics & Oral/Topical Steroids", "Doctor-only", "Prescription-only therapies for confirmed bacterial infections or severe inflammation.", "Never self-start leftover antibiotics or strong steroid creams on facial skin.", doctorRequired = true)
        )
    }

    var search by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }

    val filtered = remember(search, category) {
        items.filter { item ->
            (category == "All" || item.category == category) &&
                (search.isBlank() || item.name.contains(search, true) || item.commonUse.contains(search, true))
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
                title = "Medicine Information",
                subtitle = "Educational reference · Never a prescription",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    DisclaimerBanner(
                        text = "Educational reference only. Always verify doses with your doctor or pharmacist—especially during pregnancy, liver/kidney conditions, or for children."
                    )
                }

                item {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = { Text("Search medicine reference...") },
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
                        listOf("All", "Pain & Fever", "Allergy", "Skin & Scalp", "Doctor-only").forEach { cat ->
                            FilterPillChip(label = cat, selected = category == cat, onClick = { category = cat })
                        }
                    }
                }

                items(filtered, key = { it.name }) { med ->
                    BotanicalCard {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(med.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            if (med.doctorRequired) {
                                Surface(color = MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(9999.dp)) {
                                    Text(
                                        "Prescription Only",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(med.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(med.commonUse, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Safety: ${med.safetyNote}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
