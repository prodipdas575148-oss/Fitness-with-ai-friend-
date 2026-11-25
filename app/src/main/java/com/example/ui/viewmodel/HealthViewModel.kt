package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BpReading
import com.example.data.ConsultNote
import com.example.data.HealthRepository
import com.example.data.RoutineTask
import com.example.data.UiState
import com.example.data.UserWellnessProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HealthViewModel(
    private val repository: HealthRepository,
    private val currentUserId: String,
    initialDisplayName: String?
) : ViewModel() {

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    private val _stopwatchElapsedSec = MutableStateFlow(0L)
    val stopwatchElapsedSec: StateFlow<Long> = _stopwatchElapsedSec.asStateFlow()

    private val _stopwatchRunning = MutableStateFlow(false)
    val stopwatchRunning: StateFlow<Boolean> = _stopwatchRunning.asStateFlow()

    private val _stopwatchLaps = MutableStateFlow<List<String>>(emptyList())
    val stopwatchLaps: StateFlow<List<String>> = _stopwatchLaps.asStateFlow()

    private var stopwatchJob: Job? = null

    val profileState: StateFlow<UiState<UserWellnessProfile?>> =
        repository.observeProfile(currentUserId)
            .map<UserWellnessProfile?, UiState<UserWellnessProfile?>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing profile", root)
                emit(UiState.Error(root.message ?: "Unable to load profile"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val bpReadingsState: StateFlow<UiState<List<BpReading>>> =
        repository.observeBpReadings(currentUserId)
            .map<List<BpReading>, UiState<List<BpReading>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing BP readings", root)
                emit(UiState.Error(root.message ?: "Unable to load BP readings"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val routineTasksState: StateFlow<UiState<List<RoutineTask>>> =
        repository.observeRoutineTasks(currentUserId)
            .map<List<RoutineTask>, UiState<List<RoutineTask>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing routine tasks", root)
                emit(UiState.Error(root.message ?: "Unable to load routine tasks"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val consultNotesState: StateFlow<UiState<List<ConsultNote>>> =
        repository.observeConsultNotes(currentUserId)
            .map<List<ConsultNote>, UiState<List<ConsultNote>>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val root = firestoreError ?: error
                Log.w(TAG, "Error observing consult notes", root)
                emit(UiState.Error(root.message ?: "Unable to load consult notes"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    init {
        viewModelScope.launch {
            repository.ensureProfileAndStarterRoutines(currentUserId, initialDisplayName)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Failed to initialize profile" }
        }
    }

    fun updateWater(newGlasses: Long, currentSteps: Long) {
        viewModelScope.launch {
            repository.updateWaterAndSteps(currentUserId, newGlasses, currentSteps)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Could not save water intake" }
        }
    }

    fun updateSteps(currentWater: Long, newSteps: Long) {
        viewModelScope.launch {
            repository.updateWaterAndSteps(currentUserId, currentWater, newSteps)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Could not save steps" }
        }
    }

    fun selectCheckIn(label: String) {
        viewModelScope.launch {
            repository.updateCheckIn(currentUserId, label)
                .onSuccess { _actionMessage.value = "Check-in saved: $label" }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Could not save check-in" }
        }
    }

    fun updatePlanTier(planTier: String) {
        viewModelScope.launch {
            repository.updatePlanTier(currentUserId, planTier)
                .onSuccess {
                    _actionMessage.value = if (planTier == "plus_49") {
                        "HealthMate Plus (₹49/month) active"
                    } else {
                        "Switched to Free Wellness Plan"
                    }
                }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Could not update plan" }
        }
    }

    fun toggleTask(task: RoutineTask) {
        viewModelScope.launch {
            repository.toggleRoutineTask(currentUserId, task.id, !task.done)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Could not update task" }
        }
    }

    fun addRoutineTask(title: String, subtitle: String, period: String) {
        viewModelScope.launch {
            repository.addRoutineTask(currentUserId, title, subtitle, period)
                .onSuccess { _actionMessage.value = "Added habit to your care checklist" }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Could not add habit" }
        }
    }

    fun deleteRoutineTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteRoutineTask(currentUserId, taskId)
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Could not remove habit" }
        }
    }

    fun saveBpReading(systolic: Int, diastolic: Int, pulse: Int?, category: String) {
        viewModelScope.launch {
            repository.addBpReading(currentUserId, systolic, diastolic, pulse, category)
                .onSuccess { _actionMessage.value = "BP reading ($systolic/$diastolic) logged" }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Could not save BP reading" }
        }
    }

    fun saveConsultNote(
        problemKey: String,
        summary: String,
        durationLabel: String,
        specialist: String,
        doctorName: String
    ) {
        viewModelScope.launch {
            repository.addConsultNote(
                currentUserId,
                problemKey,
                summary,
                durationLabel,
                specialist,
                doctorName
            )
                .onSuccess { _actionMessage.value = "Consultation note sent to $doctorName" }
                .onFailure { _actionMessage.value = it.localizedMessage ?: "Could not send consult request" }
        }
    }

    fun toggleStopwatch() {
        if (_stopwatchRunning.value) {
            _stopwatchRunning.value = false
            stopwatchJob?.cancel()
            stopwatchJob = null
        } else {
            _stopwatchRunning.value = true
            stopwatchJob = viewModelScope.launch {
                while (true) {
                    delay(1000L)
                    _stopwatchElapsedSec.value += 1L
                }
            }
        }
    }

    fun lapStopwatch() {
        val sec = _stopwatchElapsedSec.value
        val lapNum = _stopwatchLaps.value.size + 1
        _stopwatchLaps.value = listOf("Lap $lapNum · ${formatSeconds(sec)}") + _stopwatchLaps.value
    }

    fun resetStopwatch() {
        _stopwatchRunning.value = false
        stopwatchJob?.cancel()
        stopwatchJob = null
        _stopwatchElapsedSec.value = 0L
        _stopwatchLaps.value = emptyList()
    }

    fun clearMessage() {
        _actionMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        stopwatchJob?.cancel()
    }

    companion object {
        private const val TAG = "HealthViewModel"
        private const val STOP_TIMEOUT_MILLIS = 5000L

        fun formatSeconds(totalSec: Long): String {
            val mins = totalSec / 60L
            val secs = totalSec % 60L
            return "%02d:%02d".format(mins, secs)
        }
    }
}
