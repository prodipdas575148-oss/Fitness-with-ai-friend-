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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.components.FilterPillChip

@Composable
fun NutritionDietScreen(onBack: () -> Unit) {
    var focus by remember { mutableStateOf("Clear Skin") }

    val meals = remember(focus) {
        when (focus) {
            "Hair Strength" -> listOf(
                "Breakfast" to "Moong dal chilla or 2 eggs with spinach & roasted pumpkin seeds (iron + biotin).",
                "Lunch" to "Lentil dal, brown rice, sautéed palak, and probiotic curd.",
                "Dinner" to "Grilled paneer or fish with steamed broccoli and warm turmeric vegetable soup."
            )
            "Energy & Immunity" -> listOf(
                "Breakfast" to "Warm oats bowl with soaked almonds, berries, and chia seeds.",
                "Lunch" to "Rajma or chickpea bowl with millet roti, cucumber salad, and lemon.",
                "Dinner" to "Light vegetable khichdi with ginger and sautéed seasonal greens."
            )
            else -> listOf(
                "Breakfast" to "Avocado & whole-grain toast with boiled egg or tofu + green tea.",
                "Lunch" to "Colorful bell pepper, chickpea & quinoa/millet salad with olive-lemon dressing.",
                "Dinner" to "Baked tofu or salmon with steamed greens and soothing carrot-ginger soup."
            )
        }
    }

    val keyNutrients = remember {
        listOf(
            "Walnuts & Flaxseeds" to "Plant Omega-3 fatty acids support calm skin barrier hydration.",
            "Red & Yellow Bell Peppers" to "High Vitamin C aids natural collagen synthesis.",
            "Spinach & Lentils" to "Bioavailable iron and folate support healthy hair roots.",
            "Pumpkin Seeds" to "Zinc-rich snack that helps balance skin oil and scalp repair."
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
            BotanicalSubTopBar(
                title = "Nutrition & Diet Plans",
                subtitle = "Everyday skin, hair & energy foods",
                onBack = onBack
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        listOf("Clear Skin", "Hair Strength", "Energy & Immunity").forEach { f ->
                            FilterPillChip(label = f, selected = focus == f, onClick = { focus = f })
                        }
                    }
                }

                item {
                    Text("$focus Daily Meals", style = MaterialTheme.typography.titleLarge)
                }

                meals.forEach { (slot, desc) ->
                    item {
                        BotanicalCard {
                            Text(slot, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(desc, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                item {
                    Text("Key Wellness Nutrients", style = MaterialTheme.typography.titleLarge)
                }

                keyNutrients.forEach { (title, benefit) ->
                    item {
                        BotanicalCard(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                            Text(title, style = MaterialTheme.typography.titleMedium)
                            Text(benefit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
