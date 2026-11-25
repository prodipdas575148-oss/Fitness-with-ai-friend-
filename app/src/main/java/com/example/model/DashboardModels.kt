package com.example.model

import androidx.compose.runtime.Immutable

enum class MoodCheckIn(val label: String, val encouragement: String) {
  ENERGIZED(
    label = "Energized",
    encouragement = "Great momentum today—channel that energy into a brisk walk or strength set.",
  ),
  STEADY(
    label = "Steady",
    encouragement = "Balanced pacing keeps your routine sustainable. Small steps add up fast.",
  ),
  NEED_REST(
    label = "Need rest",
    encouragement = "Listen to your body today—favor gentle stretching, water, and early rest.",
  ),
  SORE(
    label = "A bit sore",
    encouragement = "Active recovery like light walking and warm hydration supports muscle repair.",
  ),
}

enum class TipCategory(val label: String) {
  ALL("All"),
  FITNESS("Fitness"),
  HYDRATION("Hydration"),
  NUTRITION("Nutrition"),
  SLEEP("Sleep & Recovery"),
  SAVED("Saved"),
}

enum class WeeklyChartMetric(val label: String) {
  MINUTES("Minutes"),
  STEPS("Steps"),
}

@Immutable
data class DayMovement(
  val dayLabel: String,
  val fullDayName: String,
  val minutes: Int,
  val steps: Int,
  val isToday: Boolean = false,
)

@Immutable
data class DailyFitnessSummary(
  val steps: Int = 6420,
  val stepsGoal: Int = 8000,
  val activeMinutes: Int = 38,
  val activeMinutesGoal: Int = 45,
  val caloriesBurned: Int = 285,
  val distanceKm: Double = 4.8,
  val waterGlasses: Int = 6,
  val waterGoal: Int = 8,
  val streakDays: Int = 5,
  val weeklyMovement: List<DayMovement> = defaultWeeklyMovement(),
) {
  val stepsProgress: Float
    get() = if (stepsGoal > 0) (steps.toFloat() / stepsGoal).coerceIn(0f, 1f) else 0f

  val activeMinutesProgress: Float
    get() =
      if (activeMinutesGoal > 0) (activeMinutes.toFloat() / activeMinutesGoal).coerceIn(0f, 1f)
      else 0f

  val waterProgress: Float
    get() = if (waterGoal > 0) (waterGlasses.toFloat() / waterGoal).coerceIn(0f, 1f) else 0f

  val weeklyTotalMinutes: Int
    get() = weeklyMovement.sumOf { it.minutes }

  val weeklyTotalSteps: Int
    get() = weeklyMovement.sumOf { it.steps }

  companion object {
    fun defaultWeeklyMovement(): List<DayMovement> =
      listOf(
        DayMovement(dayLabel = "M", fullDayName = "Mon", minutes = 32, steps = 5400),
        DayMovement(dayLabel = "T", fullDayName = "Tue", minutes = 48, steps = 8450),
        DayMovement(dayLabel = "W", fullDayName = "Wed", minutes = 25, steps = 4200),
        DayMovement(dayLabel = "T", fullDayName = "Thu", minutes = 44, steps = 7800),
        DayMovement(dayLabel = "F", fullDayName = "Fri", minutes = 36, steps = 6150),
        DayMovement(dayLabel = "S", fullDayName = "Sat", minutes = 52, steps = 9320),
        DayMovement(dayLabel = "S", fullDayName = "Sun", minutes = 38, steps = 6420, isToday = true),
      )
  }
}

@Immutable
data class HealthTip(
  val id: String,
  val title: String,
  val summary: String,
  val whyItHelps: String,
  val actionStep: String,
  val readTimeLabel: String,
  val category: TipCategory,
  val isBookmarked: Boolean = false,
  val isPracticed: Boolean = false,
)

@Immutable
data class DashboardUiState(
  val userName: String = "Maya",
  val moodCheckIn: MoodCheckIn = MoodCheckIn.STEADY,
  val fitnessSummary: DailyFitnessSummary = DailyFitnessSummary(),
  val selectedChartMetric: WeeklyChartMetric = WeeklyChartMetric.MINUTES,
  val selectedChartDayIndex: Int = 6,
  val tips: List<HealthTip> = emptyList(),
  val selectedCategory: TipCategory = TipCategory.ALL,
  val searchQuery: String = "",
  val featuredTipId: String = "tip_walk_10",
  val isDarkThemeOverride: Boolean? = null,
  val isGoalSheetOpen: Boolean = false,
) {
  val savedCount: Int
    get() = tips.count { it.isBookmarked }

  val practicedCount: Int
    get() = tips.count { it.isPracticed }

  val featuredTip: HealthTip?
    get() = tips.firstOrNull { it.id == featuredTipId } ?: tips.firstOrNull()

  val filteredTips: List<HealthTip>
    get() {
      val query = searchQuery.trim().lowercase()
      return tips.filter { tip ->
        val matchesCategory =
          when (selectedCategory) {
            TipCategory.ALL -> true
            TipCategory.SAVED -> tip.isBookmarked
            else -> tip.category == selectedCategory
          }
        val matchesQuery =
          query.isEmpty() ||
            tip.title.lowercase().contains(query) ||
            tip.summary.lowercase().contains(query) ||
            tip.actionStep.lowercase().contains(query) ||
            tip.category.label.lowercase().contains(query)
        matchesCategory && matchesQuery
      }
    }
}
