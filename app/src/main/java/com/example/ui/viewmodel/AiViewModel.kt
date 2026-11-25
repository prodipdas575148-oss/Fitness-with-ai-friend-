package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiCallState
import com.example.data.ChatMessageItem
import com.example.data.ColorMatchResult
import com.example.data.ColorSwatchItem
import com.example.data.ConsultTriageResult
import com.example.data.FoodScanResult
import com.example.data.HealthAiService
import com.example.data.PhotoAnalysisResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AiViewModel(
    private val aiService: HealthAiService = HealthAiService()
) : ViewModel() {

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessageItem(
                id = "welcome_msg",
                isUser = false,
                text = "Hello! Share how you feel, skin or hair concerns, or daily diet doubts. I provide calm educational wellness guidance—never a diagnosis."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessageItem>> = _chatMessages.asStateFlow()

    private val _isChatStreaming = MutableStateFlow(false)
    val isChatStreaming: StateFlow<Boolean> = _isChatStreaming.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    private val _doubtState = MutableStateFlow<AiCallState<String>>(AiCallState.Idle)
    val doubtState: StateFlow<AiCallState<String>> = _doubtState.asStateFlow()

    private val _photoAnalysisState = MutableStateFlow<AiCallState<PhotoAnalysisResult>>(
        AiCallState.Success(defaultSkinObservation())
    )
    val photoAnalysisState: StateFlow<AiCallState<PhotoAnalysisResult>> = _photoAnalysisState.asStateFlow()

    private val _foodScanState = MutableStateFlow<AiCallState<FoodScanResult>>(
        AiCallState.Success(presetAvocadoToast())
    )
    val foodScanState: StateFlow<AiCallState<FoodScanResult>> = _foodScanState.asStateFlow()

    private val _colorMatchState = MutableStateFlow<AiCallState<ColorMatchResult>>(
        AiCallState.Success(undertonePreset("Warm Golden"))
    )
    val colorMatchState: StateFlow<AiCallState<ColorMatchResult>> = _colorMatchState.asStateFlow()

    private val _triageState = MutableStateFlow<AiCallState<ConsultTriageResult>>(AiCallState.Idle)
    val triageState: StateFlow<AiCallState<ConsultTriageResult>> = _triageState.asStateFlow()

    fun sendSymptomMessage(prompt: String, bitmap: Bitmap? = null, imageUri: String? = null) {
        val trimmed = prompt.trim()
        if (trimmed.isEmpty() || _isChatStreaming.value) return

        val isUrgent = HealthAiService.detectEmergencyKeywords(trimmed)
        val userMsg = ChatMessageItem(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = trimmed,
            isUrgent = isUrgent,
            imageUri = imageUri
        )
        val botId = UUID.randomUUID().toString()
        val historySnapshot = _chatMessages.value

        _chatError.value = null
        _chatMessages.value = historySnapshot + userMsg + ChatMessageItem(
            id = botId,
            isUser = false,
            text = "",
            isUrgent = isUrgent
        )
        _isChatStreaming.value = true

        viewModelScope.launch {
            try {
                aiService.streamSymptomChat(trimmed, historySnapshot, bitmap).collect { partial ->
                    _chatMessages.value = _chatMessages.value.map { msg ->
                        if (msg.id == botId) msg.copy(text = partial) else msg
                    }
                }
            } catch (e: TimeoutCancellationException) {
                _chatError.value = "HealthMate AI timed out after 60 seconds. Please retry."
                _chatMessages.value = _chatMessages.value.filterNot { it.id == botId && it.text.isBlank() }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val msg = HealthAiService.formatAiError(e)
                _chatError.value = msg
                _chatMessages.value = _chatMessages.value.filterNot { it.id == botId && it.text.isBlank() }
            } finally {
                _isChatStreaming.value = false
            }
        }
    }

    fun askDoubt(question: String) {
        val trimmed = question.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            _doubtState.value = AiCallState.Loading
            try {
                aiService.streamDoubtAnswer(trimmed).collect { partial ->
                    _doubtState.value = AiCallState.Streaming(partial)
                }
                val finalState = _doubtState.value
                if (finalState is AiCallState.Streaming) {
                    _doubtState.value = AiCallState.Success(finalState.text)
                }
            } catch (e: TimeoutCancellationException) {
                _doubtState.value = AiCallState.Error("Request timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _doubtState.value = AiCallState.Error(HealthAiService.formatAiError(e))
            }
        }
    }

    fun analyzeSkinOrHairPhoto(bitmap: Bitmap, mode: String) {
        viewModelScope.launch {
            _photoAnalysisState.value = AiCallState.Loading
            try {
                val result = aiService.analyzeSkinOrHairPhoto(bitmap, mode)
                _photoAnalysisState.value = AiCallState.Success(result)
            } catch (e: TimeoutCancellationException) {
                _photoAnalysisState.value = AiCallState.Error("Photo check timed out. Please try a clearer image.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _photoAnalysisState.value = AiCallState.Error(HealthAiService.formatAiError(e))
            }
        }
    }

    fun loadSamplePhotoObservation(mode: String) {
        _photoAnalysisState.value = AiCallState.Success(
            if (mode == "hair") defaultHairObservation() else defaultSkinObservation()
        )
    }

    fun scanFoodOrProduct(bitmap: Bitmap, mode: String) {
        viewModelScope.launch {
            _foodScanState.value = AiCallState.Loading
            try {
                val result = aiService.scanFoodOrProduct(bitmap, mode)
                _foodScanState.value = AiCallState.Success(result)
            } catch (e: TimeoutCancellationException) {
                _foodScanState.value = AiCallState.Error("Scan timed out. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _foodScanState.value = AiCallState.Error(HealthAiService.formatAiError(e))
            }
        }
    }

    fun selectFoodPreset(presetKey: String) {
        val result = when (presetKey) {
            "granola" -> FoodScanResult(
                itemName = "Honey Oat Granola Bar",
                servingLabel = "1 bar (40g)",
                calories = "185 kcal",
                proteinGrams = "4g",
                carbsGrams = "27g",
                fatGrams = "7g",
                fiberGrams = "3g",
                ingredients = listOf("Rolled Oats", "Honey", "Almonds", "Sunflower Oil", "Sea Salt"),
                wellnessNote = "Quick pre-walk carbs and modest fiber; pair with Greek yogurt or boiled egg to steady blood sugar.",
                cautionNote = "Contains 9g added sugars from honey and tree nuts (almonds)."
            )
            "cleanser" -> FoodScanResult(
                itemName = "Gentle Hydrating Cleanser",
                servingLabel = "Topical skincare label · 200ml",
                calories = "N/A (Topical)",
                proteinGrams = "N/A",
                carbsGrams = "N/A",
                fatGrams = "N/A",
                fiberGrams = "N/A",
                ingredients = listOf("Glycerin", "Ceramide NP", "Niacinamide 2%", "Panthenol", "Fragrance-free base"),
                wellnessNote = "Supports skin moisture barrier without stripping natural lipids; suitable for daily acne-prone or dry skin.",
                cautionNote = "Patch test along jawline for 24 hours if skin is currently inflamed or peeling."
            )
            else -> presetAvocadoToast()
        }
        _foodScanState.value = AiCallState.Success(result)
    }

    fun analyzeBodyColorPhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            _colorMatchState.value = AiCallState.Loading
            try {
                val result = aiService.analyzeBodyColorMatch(bitmap)
                _colorMatchState.value = AiCallState.Success(result)
            } catch (e: TimeoutCancellationException) {
                _colorMatchState.value = AiCallState.Error("Color analysis timed out. Please retry.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _colorMatchState.value = AiCallState.Error(HealthAiService.formatAiError(e))
            }
        }
    }

    fun selectUndertonePreset(undertone: String) {
        _colorMatchState.value = AiCallState.Success(undertonePreset(undertone))
    }

    fun runConsultTriage(problemKey: String, durationLabel: String, description: String) {
        val cleanDesc = description.trim()
        if (cleanDesc.isEmpty()) return
        viewModelScope.launch {
            _triageState.value = AiCallState.Loading
            try {
                val result = aiService.triageSeriousProblem(problemKey, durationLabel, cleanDesc)
                _triageState.value = AiCallState.Success(result)
            } catch (e: TimeoutCancellationException) {
                _triageState.value = AiCallState.Error("Doctor triage timed out. If symptoms feel urgent, seek immediate emergency care.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _triageState.value = AiCallState.Error(HealthAiService.formatAiError(e))
            }
        }
    }

    private fun defaultSkinObservation() = PhotoAnalysisResult(
        observationSummary = "Mild surface dehydration and slight T-zone oil shine with faint post-blemish marks (educational cue, not a diagnosis).",
        visualCues = listOf(
            "Light congestion around nose and chin",
            "Dryness lines near outer cheeks",
            "Uneven sun-exposed tone"
        ),
        gentleCareSteps = listOf(
            "Wash twice daily with a pH-balanced, fragrance-free gentle cleanser.",
            "Apply niacinamide (4–5%) + ceramide moisturizer on damp skin.",
            "Wear broad-spectrum SPF 50 every morning to help fade dark marks."
        ),
        whenToSeeDoctor = "See a dermatologist if spots become painful cysts, bleed, or do not calm after 6 weeks.",
        urgentAlert = false
    )

    private fun defaultHairObservation() = PhotoAnalysisResult(
        observationSummary = "Mild scalp dryness with light flaking near the crown and fragile hair ends (educational cue, not a diagnosis).",
        visualCues = listOf(
            "Fine dry white flakes along the parting",
            "Slight oil buildup near roots",
            "Dry mid-lengths needing conditioning"
        ),
        gentleCareSteps = listOf(
            "Cleanse scalp 2–3 times weekly with lukewarm (not hot) water.",
            "Keep heavy oils off inflamed scalp; focus lightweight conditioner on ends.",
            "Add daily protein + iron-rich foods like lentils, eggs, and pumpkin seeds."
        ),
        whenToSeeDoctor = "Consult a doctor if you notice patchy hair loss, thick yellow crusts, or scalp soreness.",
        urgentAlert = false
    )

    private fun presetAvocadoToast() = FoodScanResult(
        itemName = "Avocado Toast & Boiled Egg",
        servingLabel = "1 plated breakfast (240g)",
        calories = "340 kcal",
        proteinGrams = "14g",
        carbsGrams = "28g",
        fatGrams = "19g",
        fiberGrams = "7g",
        ingredients = listOf("Whole-grain toast", "Mashed avocado", "1 soft-boiled egg", "Lemon juice", "Chia sprinkle"),
        wellnessNote = "Balanced omega fats, protein, and zinc support skin barrier repair and steady morning energy.",
        cautionNote = "Contains gluten (whole-grain bread) and egg; watch added salt if monitoring blood pressure."
    )

    private fun undertonePreset(undertone: String): ColorMatchResult {
        return when (undertone) {
            "Cool Rosy" -> ColorMatchResult(
                undertone = "Cool Rosy",
                undertoneNote = "Subtle pink/blue skin undertones glow in crisp jewel tones, soft berry shades, and cool silver accents.",
                recommendedSwatches = listOf(
                    ColorSwatchItem("Emerald Pine", "#006445", "Deep blue-green contrast brightens rosy cheeks."),
                    ColorSwatchItem("Berry Plum", "#6D2E46", "Rich cool berry dresses flatter cool undertones."),
                    ColorSwatchItem("Slate Navy", "#1D3557", "Crisp structured tailoring without washing you out."),
                    ColorSwatchItem("Soft Lavender", "#B8A9C9", "Calm daytime pastel for tops and silk scarves."),
                    ColorSwatchItem("Pearl Silver", "#D6DCE2", "Refined evening clutch & trim shade.")
                ),
                avoidColors = "Bright orange-yellows and brassy neon mustard near the collarbone.",
                jewelryTip = "Silver, white gold, or platinum jewelry; structured rectangular clutches."
            )
            "Neutral Olive" -> ColorMatchResult(
                undertone = "Neutral Olive",
                undertoneNote = "Balanced green-beige undertones pair effortlessly with earthy sages, muted berries, and brushed champagne.",
                recommendedSwatches = listOf(
                    ColorSwatchItem("Botanical Sage", "#4A6B5A", "Echoes natural olive warmth cleanly."),
                    ColorSwatchItem("Deep Teal", "#0B5563", "Sharpens complexion clarity for evening wear."),
                    ColorSwatchItem("Dusty Rose", "#B57279", "Softens muted olive skin without looking harsh."),
                    ColorSwatchItem("Espresso Umber", "#4E342E", "Grounded neutral for trousers and leather clutches."),
                    ColorSwatchItem("Warm Ivory", "#F4EFE6", "Flattering light neutral over stark optic white.")
                ),
                avoidColors = "Acidic neon greens or pale chalky yellows.",
                jewelryTip = "Brushed champagne gold or mixed metals; woven box clutches."
            )
            else -> ColorMatchResult(
                undertone = "Warm Golden",
                undertoneNote = "Golden-peach warmth looks luminous in botanical greens, terracotta corals, warm creams, and antique gold.",
                recommendedSwatches = listOf(
                    ColorSwatchItem("Botanical Emerald", "#006445", "Rich emerald creates vibrant golden-skin harmony."),
                    ColorSwatchItem("Terracotta Coral", "#C85A3F", "Brings instant healthy glow to warm complexions."),
                    ColorSwatchItem("Warm Cream", "#F5EEDC", "Soft ivory lifts outfits better than stark blue-white."),
                    ColorSwatchItem("Olive Moss", "#5C6B3C", "Effortless everyday tone for kurtas, shirts, and linen."),
                    ColorSwatchItem("Champagne Bronze", "#B08D57", "Luminous festive dress & clutch accent.")
                ),
                avoidColors = "Icy pastel blues and cool neon greys right under the chin.",
                jewelryTip = "Warm yellow gold, rose gold, or bronze hardware clutches."
            )
        }
    }
}
