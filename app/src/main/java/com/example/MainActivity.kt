package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DashboardUiState
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.HealthMateTheme
import com.example.ui.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val dashboardViewModel: DashboardViewModel = viewModel()
      val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
      val systemDark = isSystemInDarkTheme()
      val isDarkTheme = uiState.isDarkThemeOverride ?: systemDark

      HealthMateTheme(darkTheme = isDarkTheme) {
        DashboardScreen(
          uiState = uiState,
          greetingHeadline = dashboardViewModel.getGreetingHeadline(),
          isDarkTheme = isDarkTheme,
          onToggleTheme = { dashboardViewModel.toggleTheme(isDarkTheme) },
          onSelectMood = dashboardViewModel::selectMood,
          onSearchQueryChange = dashboardViewModel::updateSearchQuery,
          onAddSteps = { dashboardViewModel.addSteps(500) },
          onAddActiveMinutes = { dashboardViewModel.addActiveMinutes(10) },
          onAddWater = dashboardViewModel::logWaterGlass,
          onRemoveWater = dashboardViewModel::removeWaterGlass,
          onSelectChartMetric = dashboardViewModel::selectChartMetric,
          onSelectChartDay = dashboardViewModel::selectChartDay,
          onSelectCategory = dashboardViewModel::selectCategory,
          onClearFilters = dashboardViewModel::clearFilters,
          onToggleBookmark = dashboardViewModel::toggleBookmark,
          onTogglePracticed = dashboardViewModel::togglePracticed,
          onCycleFeaturedTip = dashboardViewModel::cycleFeaturedTip,
          onOpenGoalsSheet = dashboardViewModel::openGoalSheet,
          onSaveGoals = dashboardViewModel::updateGoals,
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
  HealthMateTheme(darkTheme = false) {
    DashboardScreen(
      uiState = DashboardUiState(tips = DashboardViewModel.seedHealthTips()),
      greetingHeadline = "Good morning",
      isDarkTheme = false,
      onToggleTheme = {},
      onSelectMood = {},
      onSearchQueryChange = {},
      onAddSteps = {},
      onAddActiveMinutes = {},
      onAddWater = {},
      onRemoveWater = {},
      onSelectChartMetric = {},
      onSelectChartDay = {},
      onSelectCategory = {},
      onClearFilters = {},
      onToggleBookmark = {},
      onTogglePracticed = {},
      onCycleFeaturedTip = {},
      onOpenGoalsSheet = {},
      onSaveGoals = { _, _, _, _ -> },
    )
  }
}
