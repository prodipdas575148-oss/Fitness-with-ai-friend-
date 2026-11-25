package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.DailyFitnessSummary
import com.example.model.DashboardUiState
import com.example.model.HealthTip
import com.example.model.MoodCheckIn
import com.example.model.TipCategory
import com.example.model.WeeklyChartMetric
import java.util.Calendar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt

class DashboardViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(
    DashboardUiState(
      tips = seedHealthTips(),
    )
  )
  val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

  fun getGreetingHeadline(hourOfDay: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)): String {
    return when (hourOfDay) {
      in 5..11 -> "Good morning"
      in 12..16 -> "Good afternoon"
      else -> "Good evening"
    }
  }

  fun selectMood(mood: MoodCheckIn) {
    val recommendedTipId =
      when (mood) {
        MoodCheckIn.ENERGIZED -> "tip_walk_10"
        MoodCheckIn.STEADY -> "tip_hydration_sip"
        MoodCheckIn.NEED_REST -> "tip_wind_down"
        MoodCheckIn.SORE -> "tip_posture_stretch"
      }
    _uiState.update { state ->
      state.copy(
        moodCheckIn = mood,
        featuredTipId = recommendedTipId,
      )
    }
  }

  fun selectChartMetric(metric: WeeklyChartMetric) {
    _uiState.update { it.copy(selectedChartMetric = metric) }
  }

  fun selectChartDay(index: Int) {
    _uiState.update { state ->
      val clamped = index.coerceIn(0, (state.fitnessSummary.weeklyMovement.size - 1).coerceAtLeast(0))
      state.copy(selectedChartDayIndex = clamped)
    }
  }

  fun addSteps(delta: Int = 500) {
    _uiState.update { state ->
      val current = state.fitnessSummary
      val updatedSteps = (current.steps + delta).coerceAtLeast(0)
      val addedMinutes = (delta / 100).coerceAtLeast(1)
      val updatedMinutes = current.activeMinutes + addedMinutes
      state.copy(
        fitnessSummary = recalculateMetrics(
          summary = current,
          steps = updatedSteps,
          activeMinutes = updatedMinutes,
        )
      )
    }
  }

  fun addActiveMinutes(delta: Int = 10) {
    _uiState.update { state ->
      val current = state.fitnessSummary
      val updatedMinutes = (current.activeMinutes + delta).coerceAtLeast(0)
      val estimatedStepsFromMove = delta * 95
      val updatedSteps = current.steps + estimatedStepsFromMove
      state.copy(
        fitnessSummary = recalculateMetrics(
          summary = current,
          steps = updatedSteps,
          activeMinutes = updatedMinutes,
        )
      )
    }
  }

  fun logWaterGlass() {
    _uiState.update { state ->
      val current = state.fitnessSummary
      val updatedWater = (current.waterGlasses + 1).coerceAtMost(16)
      state.copy(fitnessSummary = current.copy(waterGlasses = updatedWater))
    }
  }

  fun removeWaterGlass() {
    _uiState.update { state ->
      val current = state.fitnessSummary
      val updatedWater = (current.waterGlasses - 1).coerceAtLeast(0)
      state.copy(fitnessSummary = current.copy(waterGlasses = updatedWater))
    }
  }

  fun selectCategory(category: TipCategory) {
    _uiState.update { it.copy(selectedCategory = category) }
  }

  fun updateSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun clearFilters() {
    _uiState.update { it.copy(selectedCategory = TipCategory.ALL, searchQuery = "") }
  }

  fun toggleBookmark(tipId: String) {
    _uiState.update { state ->
      state.copy(
        tips = state.tips.map { tip ->
          if (tip.id == tipId) tip.copy(isBookmarked = !tip.isBookmarked) else tip
        }
      )
    }
  }

  fun togglePracticed(tipId: String) {
    _uiState.update { state ->
      state.copy(
        tips = state.tips.map { tip ->
          if (tip.id == tipId) tip.copy(isPracticed = !tip.isPracticed) else tip
        }
      )
    }
  }

  fun cycleFeaturedTip() {
    _uiState.update { state ->
      val list = state.tips
      if (list.isEmpty()) return@update state
      val idx = list.indexOfFirst { it.id == state.featuredTipId }
      val nextIdx = if (idx < 0) 0 else (idx + 1) % list.size
      state.copy(featuredTipId = list[nextIdx].id)
    }
  }

  fun toggleTheme(currentlyDark: Boolean) {
    _uiState.update { it.copy(isDarkThemeOverride = !currentlyDark) }
  }

  fun openGoalSheet(open: Boolean) {
    _uiState.update { it.copy(isGoalSheetOpen = open) }
  }

  fun updateGoals(
    userName: String,
    stepsGoal: Int,
    activeMinutesGoal: Int,
    waterGoal: Int,
  ) {
    val cleanName = userName.trim().ifEmpty { "Friend" }
    _uiState.update { state ->
      state.copy(
        userName = cleanName,
        fitnessSummary = state.fitnessSummary.copy(
          stepsGoal = stepsGoal.coerceIn(2000, 30000),
          activeMinutesGoal = activeMinutesGoal.coerceIn(15, 180),
          waterGoal = waterGoal.coerceIn(4, 16),
        ),
        isGoalSheetOpen = false,
      )
    }
  }

  private fun recalculateMetrics(
    summary: DailyFitnessSummary,
    steps: Int,
    activeMinutes: Int,
  ): DailyFitnessSummary {
    val distanceKm = ((steps * 0.00075) * 10.0).roundToInt() / 10.0
    val calories = ((steps * 0.035) + (activeMinutes * 1.6)).roundToInt()
    val updatedWeekly = summary.weeklyMovement.map { day ->
      if (day.isToday) day.copy(minutes = activeMinutes, steps = steps) else day
    }
    return summary.copy(
      steps = steps,
      activeMinutes = activeMinutes,
      distanceKm = distanceKm,
      caloriesBurned = calories,
      weeklyMovement = updatedWeekly,
    )
  }

  companion object {
    fun seedHealthTips(): List<HealthTip> =
      listOf(
        HealthTip(
          id = "tip_walk_10",
          title = "10-minute walk after meals",
          summary = "A gentle stroll shortly after eating helps smooth blood sugar spikes and aids digestion.",
          whyItHelps = "Light leg muscle contraction absorbs glucose naturally without needing extra insulin, leaving you clearer-headed in the afternoon.",
          actionStep = "Step outside or walk indoors for 10 calm minutes after lunch.",
          readTimeLabel = "1 min read",
          category = TipCategory.FITNESS,
          isBookmarked = true,
        ),
        HealthTip(
          id = "tip_hydration_sip",
          title = "Steady hydration over chugging",
          summary = "Sip water consistently across the morning and afternoon rather than catching up late at night.",
          whyItHelps = "Smaller, regular sips improve absorption, support focus and skin elasticity, and prevent sleep interruptions.",
          actionStep = "Pair 1 glass of water with every meal and afternoon break.",
          readTimeLabel = "1 min read",
          category = TipCategory.HYDRATION,
        ),
        HealthTip(
          id = "tip_posture_stretch",
          title = "60-second shoulder & neck reset",
          summary = "Roll shoulders back and gently tuck your chin every hour of desk or phone time.",
          whyItHelps = "Counteracts forward-head posture, eases upper-back stiffness, and opens chest breathing.",
          actionStep = "Stand up, clasp hands behind your back, and breathe deeply for 5 slow breaths.",
          readTimeLabel = "2 min read",
          category = TipCategory.FITNESS,
        ),
        HealthTip(
          id = "tip_protein_fiber",
          title = "Pair carbs with protein & fiber",
          summary = "Add nuts, yogurt, lentils, or leafy greens alongside fruit or grains for steady energy.",
          whyItHelps = "Slows digestion gently so energy stays even for 3–4 hours without mid-afternoon crashes.",
          actionStep = "Include one colorful vegetable and one protein source on your next plate.",
          readTimeLabel = "2 min read",
          category = TipCategory.NUTRITION,
          isBookmarked = true,
        ),
        HealthTip(
          id = "tip_wind_down",
          title = "Dim lights 45 minutes before bed",
          summary = "Lowering screen brightness and warm lighting in the evening cues natural melatonin release.",
          whyItHelps = "Bright blue-white light delays your sleep clock; softer evening lighting helps you fall asleep faster.",
          actionStep = "Switch on warm bedside lighting and set your phone aside 30–45 minutes early.",
          readTimeLabel = "1 min read",
          category = TipCategory.SLEEP,
        ),
        HealthTip(
          id = "tip_morning_sun",
          title = "Morning daylight within 30 minutes",
          summary = "Get 5–10 minutes of natural outdoor daylight soon after waking to anchor energy and mood.",
          whyItHelps = "Morning light signals alertness hormones during the day and sets a timer for deeper sleep 14–16 hours later.",
          actionStep = "Drink your morning water near an open window or on a quick patio step-out.",
          readTimeLabel = "1 min read",
          category = TipCategory.SLEEP,
        ),
      )
  }
}
