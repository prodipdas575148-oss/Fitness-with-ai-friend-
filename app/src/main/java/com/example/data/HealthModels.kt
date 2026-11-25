package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface AiCallState<out T> {
    data object Idle : AiCallState<Nothing>
    data object Loading : AiCallState<Nothing>
    data class Streaming(val text: String) : AiCallState<Nothing>
    data class Success<T>(val data: T) : AiCallState<T>
    data class Error(val message: String) : AiCallState<Nothing>
}

@Keep
@IgnoreExtraProperties
data class UserWellnessProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Wellness Member",
    val planTier: String = "free",
    val waterGlasses: Long = 4L,
    val waterGoal: Long = 8L,
    val steps: Long = 4200L,
    val stepsGoal: Long = 8000L,
    val streakDays: Long = 5L,
    val activeCheckIn: String = "Feeling steady",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class BpReading(
    @DocumentId val id: String = "",
    val userId: String = "",
    val systolic: Long = 118L,
    val diastolic: Long = 76L,
    val pulse: Long? = null,
    val category: String = "Normal",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class RoutineTask(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subtitle: String = "",
    val period: String = "morning",
    val done: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ConsultNote(
    @DocumentId val id: String = "",
    val userId: String = "",
    val problemKey: String = "",
    val summary: String = "",
    val durationLabel: String = "",
    val specialist: String = "",
    val doctorName: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class ChatMessageItem(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val isUrgent: Boolean = false,
    val imageUri: String? = null
)

data class PhotoAnalysisResult(
    val observationSummary: String,
    val visualCues: List<String>,
    val gentleCareSteps: List<String>,
    val whenToSeeDoctor: String,
    val urgentAlert: Boolean
)

data class FoodScanResult(
    val itemName: String,
    val servingLabel: String,
    val calories: String,
    val proteinGrams: String,
    val carbsGrams: String,
    val fatGrams: String,
    val fiberGrams: String,
    val ingredients: List<String>,
    val wellnessNote: String,
    val cautionNote: String
)

data class ColorSwatchItem(
    val name: String,
    val hex: String,
    val reason: String
)

data class ColorMatchResult(
    val undertone: String,
    val undertoneNote: String,
    val recommendedSwatches: List<ColorSwatchItem>,
    val avoidColors: String,
    val jewelryTip: String
)

data class ConsultTriageResult(
    val urgencyLevel: String,
    val summaryGuidance: String,
    val recommendedSpecialist: String,
    val whileWaitingSteps: List<String>
)
