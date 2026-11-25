package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DashboardUiState
import com.example.model.MoodCheckIn
import com.example.model.TipCategory
import com.example.model.WeeklyChartMetric
import com.example.ui.components.DashboardHeader
import com.example.ui.components.EmptyTipsStateCard
import com.example.ui.components.FeaturedTipCard
import com.example.ui.components.FitnessActivitySummaryCard
import com.example.ui.components.GoalsAndProfileSheet
import com.example.ui.components.GreetingSection
import com.example.ui.components.QuickHealthTipCard
import com.example.ui.components.QuickHealthTipsHeader
import com.example.ui.components.TipCategoryFilterRow
import com.example.ui.components.WeeklyFitnessBarChartCard

@Composable
fun DashboardScreen(
  uiState: DashboardUiState,
  greetingHeadline: String,
  isDarkTheme: Boolean,
  onToggleTheme: () -> Unit,
  onSelectMood: (MoodCheckIn) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  onAddSteps: () -> Unit,
  onAddActiveMinutes: () -> Unit,
  onAddWater: () -> Unit,
  onRemoveWater: () -> Unit,
  onSelectChartMetric: (WeeklyChartMetric) -> Unit,
  onSelectChartDay: (Int) -> Unit,
  onSelectCategory: (TipCategory) -> Unit,
  onClearFilters: () -> Unit,
  onToggleBookmark: (String) -> Unit,
  onTogglePracticed: (String) -> Unit,
  onCycleFeaturedTip: () -> Unit,
  onOpenGoalsSheet: (Boolean) -> Unit,
  onSaveGoals: (name: String, stepsGoal: Int, activeMinutesGoal: Int, waterGoal: Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val filteredTips = remember(uiState.tips, uiState.selectedCategory, uiState.searchQuery) {
    uiState.filteredTips
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.surface,
    contentWindowInsets = WindowInsets.safeDrawing,
    topBar = {
      DashboardHeader(
        streakDays = uiState.fitnessSummary.streakDays,
        isDarkTheme = isDarkTheme,
        onToggleTheme = onToggleTheme,
        onOpenGoals = { onOpenGoalsSheet(true) },
      )
    },
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentAlignment = Alignment.TopCenter,
    ) {
      BoxWithConstraints(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 720.dp),
      ) {
        val useTwoColumnTips = maxWidth >= 580.dp

        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
          verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
          // 1. Greeting + Mood Check-in + Search
          item(key = "greeting_section") {
            GreetingSection(
              greetingHeadline = greetingHeadline,
              userName = uiState.userName,
              selectedMood = uiState.moodCheckIn,
              searchQuery = uiState.searchQuery,
              onSelectMood = onSelectMood,
              onSearchQueryChange = onSearchQueryChange,
            )
          }

          // 2. Daily Fitness Activity Summary Card
          item(key = "fitness_summary_card") {
            FitnessActivitySummaryCard(
              summary = uiState.fitnessSummary,
              onAddSteps = onAddSteps,
              onAddActiveMinutes = onAddActiveMinutes,
              onAddWater = onAddWater,
              onRemoveWater = onRemoveWater,
              onEditGoals = { onOpenGoalsSheet(true) },
            )
          }

          // 3. Weekly Fitness Progress Custom Canvas Bar Chart
          item(key = "weekly_fitness_bar_chart") {
            WeeklyFitnessBarChartCard(
              summary = uiState.fitnessSummary,
              selectedMetric = uiState.selectedChartMetric,
              selectedDayIndex = uiState.selectedChartDayIndex,
              onSelectMetric = onSelectChartMetric,
              onSelectDay = onSelectChartDay,
            )
          }

          // 3. Quick Health Tips Header & Featured Tip
          item(key = "quick_tips_header") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              QuickHealthTipsHeader(
                practicedCount = uiState.practicedCount,
                totalCount = uiState.tips.size,
                onCycleFeatured = onCycleFeaturedTip,
              )
              uiState.featuredTip?.let { featured ->
                FeaturedTipCard(
                  tip = featured,
                  onToggleBookmark = onToggleBookmark,
                  onTogglePracticed = onTogglePracticed,
                )
              }
              TipCategoryFilterRow(
                selectedCategory = uiState.selectedCategory,
                savedCount = uiState.savedCount,
                onSelectCategory = onSelectCategory,
              )
            }
          }

          // Quick Health Tips List / Adaptive 2-Column Grid
          if (filteredTips.isEmpty()) {
            item(key = "empty_tips") {
              EmptyTipsStateCard(onClearFilters = onClearFilters)
            }
          } else if (useTwoColumnTips) {
            val rows = filteredTips.chunked(2)
            items(
              items = rows,
              key = { row -> row.joinToString("_") { it.id } },
            ) { rowTips ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
              ) {
                rowTips.forEach { tip ->
                  QuickHealthTipCard(
                    tip = tip,
                    onToggleBookmark = onToggleBookmark,
                    onTogglePracticed = onTogglePracticed,
                    modifier = Modifier.weight(1f),
                  )
                }
                if (rowTips.size == 1) {
                  SpacerPlaceholder(modifier = Modifier.weight(1f))
                }
              }
            }
          } else {
            items(
              items = filteredTips,
              key = { it.id },
            ) { tip ->
              QuickHealthTipCard(
                tip = tip,
                onToggleBookmark = onToggleBookmark,
                onTogglePracticed = onTogglePracticed,
              )
            }
          }

          // Educational Wellness Notice Footer
          item(key = "safety_notice") {
            Surface(
              shape = MaterialTheme.shapes.large,
              color = MaterialTheme.colorScheme.surfaceContainerLow,
              modifier = Modifier
                .fillMaxWidth()
                .border(
                  1.dp,
                  MaterialTheme.colorScheme.outlineVariant,
                  MaterialTheme.shapes.large,
                ),
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
              ) {
                Icon(
                  imageVector = Icons.Outlined.VerifiedUser,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp),
                )
                Text(
                  text = stringResource(R.string.safety_disclaimer),
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          }
        }
      }
    }
  }

  if (uiState.isGoalSheetOpen) {
    GoalsAndProfileSheet(
      currentName = uiState.userName,
      currentStepsGoal = uiState.fitnessSummary.stepsGoal,
      currentActiveMinutesGoal = uiState.fitnessSummary.activeMinutesGoal,
      currentWaterGoal = uiState.fitnessSummary.waterGoal,
      onDismiss = { onOpenGoalsSheet(false) },
      onSave = onSaveGoals,
    )
  }
}

@Composable
private fun SpacerPlaceholder(modifier: Modifier = Modifier) {
  Box(modifier = modifier)
}
