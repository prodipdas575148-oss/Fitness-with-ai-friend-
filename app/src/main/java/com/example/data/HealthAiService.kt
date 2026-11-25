package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val AI_TAG = "HealthAiService"
private const val AI_TIMEOUT_MS = 60_000L

object BitmapHelper {
    suspend fun decodeScaledBitmap(
        context: Context,
        uri: Uri,
        maxDimension: Int = 1024
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOptions)
            }
            val origW = boundsOptions.outWidth
            val origH = boundsOptions.outHeight
            if (origW <= 0 || origH <= 0) return@withContext null

            var sampleSize = 1
            while (origW / sampleSize > maxDimension || origH / sampleSize > maxDimension) {
                sampleSize *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)
            }
        } catch (e: Exception) {
            Log.e(AI_TAG, "Failed to decode bitmap from Uri", e)
            null
        }
    }
}

class HealthAiService {

    private val chatModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    "You are HealthMate AI, a warm, calm, reassuring personal wellness, skin, hair, and nutrition guide. " +
                        "Write in clear everyday English (never alarmist or overly technical). " +
                        "Always frame advice as educational wellness guidance, NEVER a medical diagnosis or prescription. " +
                        "Structure answers cleanly with short headings: 'Possible causes', 'Safe home care', and 'When to see a doctor'. " +
                        "If red-flag symptoms appear (chest pain, shortness of breath, fainting, severe bleeding, high fever >103F, facial swelling), clearly urge immediate medical care."
                )
            }
        )
    }

    private val photoAnalysisSchema = Schema.obj(
        properties = mapOf(
            "observationSummary" to Schema.string("Short friendly 1-2 sentence summary of visible wellness cues (not a diagnosis)"),
            "visualCues" to Schema.array(Schema.string(), "2-4 plain-English visible cues"),
            "gentleCareSteps" to Schema.array(Schema.string(), "3-4 safe everyday care steps"),
            "whenToSeeDoctor" to Schema.string("Clear guidance on when to visit a dermatologist or doctor"),
            "urgentAlert" to Schema.boolean("True only if severe blistering, spreading infection, or allergic swelling appears")
        )
    )

    private val photoAnalysisModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = photoAnalysisSchema
            }
        )
    }

    private val foodScanSchema = Schema.obj(
        properties = mapOf(
            "itemName" to Schema.string("Short name of the meal or skincare/food product"),
            "servingLabel" to Schema.string("Serving or pack unit description"),
            "calories" to Schema.string("Estimated calories or 'N/A (Topical)' for cosmetic products"),
            "proteinGrams" to Schema.string("Protein estimate e.g. '14g' or 'N/A'"),
            "carbsGrams" to Schema.string("Carbs estimate e.g. '26g' or 'N/A'"),
            "fatGrams" to Schema.string("Healthy/total fats e.g. '11g' or 'N/A'"),
            "fiberGrams" to Schema.string("Fiber estimate e.g. '6g' or 'N/A'"),
            "ingredients" to Schema.array(Schema.string(), "3-5 key ingredients or components spotted"),
            "wellnessNote" to Schema.string("Calm plain-English note on skin/hair/energy impact"),
            "cautionNote" to Schema.string("Allergen, added sugar, fragrance, or sensitivity watchout")
        )
    )

    private val foodScanModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = foodScanSchema
            }
        )
    }

    private val colorMatchSchema = Schema.obj(
        properties = mapOf(
            "undertone" to Schema.string("One of: Warm Golden, Cool Rosy, Neutral Olive"),
            "undertoneNote" to Schema.string("Friendly 1-2 sentence explanation of how to complement this undertone"),
            "swatches" to Schema.array(
                Schema.obj(
                    properties = mapOf(
                        "name" to Schema.string("Color name"),
                        "hex" to Schema.string("6-digit hex color like #006445"),
                        "reason" to Schema.string("Why this color flatters the user's dress or clutch look")
                    )
                ),
                "Exactly 5 flattering outfit and clutch color swatches"
            ),
            "avoidColors" to Schema.string("Colors to use sparingly or pair away from the face"),
            "jewelryTip" to Schema.string("Metal tone, neckline, and clutch finish suggestion")
        )
    )

    private val colorMatchModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = colorMatchSchema
            }
        )
    }

    private val triageSchema = Schema.obj(
        properties = mapOf(
            "urgencyLevel" to Schema.string("One of: Routine, Prompt Today, Immediate Emergency"),
            "summaryGuidance" to Schema.string("Calm, practical 2-sentence triage guidance"),
            "recommendedSpecialist" to Schema.string("Relevant specialist (e.g., General Physician, Cardiologist, Gastroenterologist)"),
            "whileWaitingSteps" to Schema.array(Schema.string(), "3 safe actions to take while waiting for medical review")
        )
    )

    private val triageModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = triageSchema
            }
        )
    }

    fun streamSymptomChat(
        userPrompt: String,
        history: List<ChatMessageItem>,
        bitmap: Bitmap? = null
    ): Flow<String> = flow {
        val sb = StringBuilder()
        val recentHistory = history.takeLast(6).map { msg ->
            content(if (msg.isUser) "user" else "model") {
                text(msg.text)
            }
        }
        val promptContent = content("user") {
            if (bitmap != null) {
                image(bitmap)
            }
            text(userPrompt)
        }
        withTimeout(AI_TIMEOUT_MS) {
            if (bitmap == null && recentHistory.isNotEmpty()) {
                val chat = chatModel.startChat(history = recentHistory)
                chat.sendMessageStream(userPrompt).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        sb.append(chunkText)
                        emit(sb.toString())
                    }
                }
            } else {
                chatModel.generateContentStream(promptContent).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        sb.append(chunkText)
                        emit(sb.toString())
                    }
                }
            }
        }
        if (sb.isBlank()) {
            throw IllegalStateException("AI returned an empty response. Please try again.")
        }
    }

    fun streamDoubtAnswer(question: String): Flow<String> = flow {
        val prompt = """
            Answer this everyday wellness, skin, hair, or diet question clearly for a non-medical user:
            "$question"

            Include:
            1) Clear answer (2 sentences)
            2) 3 practical safe steps numbered 1, 2, 3
            3) When to see a doctor (1 sentence)
            Keep it warm, brief, and educational (not a diagnosis).
        """.trimIndent()
        val sb = StringBuilder()
        withTimeout(AI_TIMEOUT_MS) {
            chatModel.generateContentStream(prompt).collect { chunk ->
                val chunkText = chunk.text.orEmpty()
                if (chunkText.isNotEmpty()) {
                    sb.append(chunkText)
                    emit(sb.toString())
                }
            }
        }
        if (sb.isBlank()) {
            throw IllegalStateException("AI returned an empty answer. Please try again.")
        }
    }

    suspend fun analyzeSkinOrHairPhoto(bitmap: Bitmap, mode: String): PhotoAnalysisResult {
        val focusLabel = if (mode == "hair") "hair and scalp" else "facial skin"
        val prompt = """
            Review this $focusLabel wellness photo.
            Provide educational observations only (explicitly not a medical diagnosis).
            Identify visible cues (like dryness, oil shine, mild flaking, uneven tone, redness),
            suggest gentle daily barrier/scalp habits, and note when a dermatologist visit helps.
        """.trimIndent()

        val response = withTimeout(AI_TIMEOUT_MS) {
            photoAnalysisModel.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )
        }
        val jsonText = response.text
            ?: throw IllegalStateException("Photo analysis returned empty output.")
        val json = JSONObject(jsonText)
        val cuesArr = json.getJSONArray("visualCues")
        val stepsArr = json.getJSONArray("gentleCareSteps")
        return PhotoAnalysisResult(
            observationSummary = json.getString("observationSummary"),
            visualCues = (0 until cuesArr.length()).map { cuesArr.getString(it) },
            gentleCareSteps = (0 until stepsArr.length()).map { stepsArr.getString(it) },
            whenToSeeDoctor = json.getString("whenToSeeDoctor"),
            urgentAlert = json.optBoolean("urgentAlert", false)
        )
    }

    suspend fun scanFoodOrProduct(bitmap: Bitmap, mode: String): FoodScanResult {
        val kind = if (mode == "product") "skincare/hair product ingredient label" else "meal or packaged food item"
        val prompt = """
            Analyze this $kind photo.
            For meals/food, estimate calories, protein, carbs, fat, and fiber per typical serving, list key ingredients, and note skin/gut/energy benefits and any allergens or added sugars.
            For skincare/hair products, set calories/macros to 'N/A (Topical)', extract key active & base ingredients, and share gentle skin/scalp compatibility notes.
        """.trimIndent()

        val response = withTimeout(AI_TIMEOUT_MS) {
            foodScanModel.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )
        }
        val jsonText = response.text
            ?: throw IllegalStateException("Scanner returned empty output.")
        val json = JSONObject(jsonText)
        val ingArr = json.getJSONArray("ingredients")
        return FoodScanResult(
            itemName = json.getString("itemName"),
            servingLabel = json.getString("servingLabel"),
            calories = json.getString("calories"),
            proteinGrams = json.getString("proteinGrams"),
            carbsGrams = json.getString("carbsGrams"),
            fatGrams = json.getString("fatGrams"),
            fiberGrams = json.getString("fiberGrams"),
            ingredients = (0 until ingArr.length()).map { ingArr.getString(it) },
            wellnessNote = json.getString("wellnessNote"),
            cautionNote = json.getString("cautionNote")
        )
    }

    suspend fun analyzeBodyColorMatch(bitmap: Bitmap): ColorMatchResult {
        val prompt = """
            Analyze the skin undertone visible in this natural-light portrait/wrist photo (Warm Golden, Cool Rosy, or Neutral Olive).
            Recommend 5 flattering dress and clutch color swatches with valid 6-digit hex codes (#RRGGBB),
            plus colors to use sparingly and metal/jewelry pairing tips.
        """.trimIndent()

        val response = withTimeout(AI_TIMEOUT_MS) {
            colorMatchModel.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )
        }
        val jsonText = response.text
            ?: throw IllegalStateException("Color match returned empty output.")
        val json = JSONObject(jsonText)
        val swatchesArr = json.getJSONArray("swatches")
        val swatches = (0 until swatchesArr.length()).map { idx ->
            val item = swatchesArr.getJSONObject(idx)
            ColorSwatchItem(
                name = item.getString("name"),
                hex = item.getString("hex"),
                reason = item.getString("reason")
            )
        }
        return ColorMatchResult(
            undertone = json.getString("undertone"),
            undertoneNote = json.getString("undertoneNote"),
            recommendedSwatches = swatches,
            avoidColors = json.getString("avoidColors"),
            jewelryTip = json.getString("jewelryTip")
        )
    }

    suspend fun triageSeriousProblem(
        problemKey: String,
        durationLabel: String,
        description: String
    ): ConsultTriageResult {
        val prompt = """
            A user reports: Category='$problemKey', Duration='$durationLabel', Notes='$description'.
            Provide calm, safety-first triage guidance (NOT a diagnosis).
            Classify urgencyLevel as 'Routine', 'Prompt Today', or 'Immediate Emergency'.
            Suggest the best specialist and 3 safe steps while awaiting doctor contact.
        """.trimIndent()

        val response = withTimeout(AI_TIMEOUT_MS) {
            triageModel.generateContent(prompt)
        }
        val jsonText = response.text
            ?: throw IllegalStateException("Triage returned empty output.")
        val json = JSONObject(jsonText)
        val stepsArr = json.getJSONArray("whileWaitingSteps")
        return ConsultTriageResult(
            urgencyLevel = json.getString("urgencyLevel"),
            summaryGuidance = json.getString("summaryGuidance"),
            recommendedSpecialist = json.getString("recommendedSpecialist"),
            whileWaitingSteps = (0 until stepsArr.length()).map { stepsArr.getString(it) }
        )
    }

    companion object {
        fun detectEmergencyKeywords(text: String): Boolean {
            val lower = text.lowercase()
            return listOf(
                "chest pain", "chest tightness", "can't breathe", "shortness of breath",
                "difficulty breathing", "fainting", "faint", "severe bleeding",
                "unconscious", "stroke", "sudden numbness", "180/120"
            ).any { lower.contains(it) }
        }

        fun formatAiError(e: Exception): String {
            if (e is CancellationException) throw e
            Log.e(AI_TAG, "Firebase AI call failed", e)
            return e.localizedMessage ?: "Unable to reach HealthMate AI right now. Please try again."
        }
    }
}
