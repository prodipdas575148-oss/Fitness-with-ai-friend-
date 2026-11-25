package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MonoMetricMediumStyle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsAndProfileSheet(
  currentName: String,
  currentStepsGoal: Int,
  currentActiveMinutesGoal: Int,
  currentWaterGoal: Int,
  onDismiss: () -> Unit,
  onSave: (name: String, stepsGoal: Int, activeMinutesGoal: Int, waterGoal: Int) -> Unit,
) {
  var nameInput by remember(currentName) { mutableStateOf(currentName) }
  var stepsSlider by remember(currentStepsGoal) { mutableFloatStateOf(currentStepsGoal.toFloat()) }
  var minutesSlider by remember(currentActiveMinutesGoal) {
    mutableFloatStateOf(currentActiveMinutesGoal.toFloat())
  }
  var waterSlider by remember(currentWaterGoal) { mutableFloatStateOf(currentWaterGoal.toFloat()) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
          text = "Customize daily goals",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = "Update your greeting name and gentle daily movement targets.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      OutlinedTextField(
        value = nameInput,
        onValueChange = { nameInput = it },
        label = { Text("Your name") },
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("goal_name_input"),
      )

      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text("Daily step target", style = MaterialTheme.typography.labelLarge)
          Text(
            text = "${(stepsSlider.roundToInt() / 500) * 500} steps",
            style = MonoMetricMediumStyle,
            color = MaterialTheme.colorScheme.primary,
          )
        }
        Slider(
          value = stepsSlider,
          onValueChange = { stepsSlider = it },
          valueRange = 3000f..15000f,
          steps = 23,
          modifier = Modifier.testTag("steps_goal_slider"),
        )
      }

      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text("Active minutes target", style = MaterialTheme.typography.labelLarge)
          Text(
            text = "${(minutesSlider.roundToInt() / 5) * 5} min",
            style = MonoMetricMediumStyle,
            color = MaterialTheme.colorScheme.primary,
          )
        }
        Slider(
          value = minutesSlider,
          onValueChange = { minutesSlider = it },
          valueRange = 15f..90f,
          steps = 14,
          modifier = Modifier.testTag("active_minutes_goal_slider"),
        )
      }

      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text("Hydration target", style = MaterialTheme.typography.labelLarge)
          Text(
            text = "${waterSlider.roundToInt()} glasses",
            style = MonoMetricMediumStyle,
            color = MaterialTheme.colorScheme.primary,
          )
        }
        Slider(
          value = waterSlider,
          onValueChange = { waterSlider = it },
          valueRange = 4f..12f,
          steps = 7,
          modifier = Modifier.testTag("water_goal_slider"),
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        OutlinedButton(
          onClick = onDismiss,
          shape = CircleShape,
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("cancel_goals_button"),
        ) {
          Text("Cancel", style = MaterialTheme.typography.labelLarge)
        }
        Button(
          onClick = {
            onSave(
              nameInput,
              (stepsSlider.roundToInt() / 500) * 500,
              (minutesSlider.roundToInt() / 5) * 5,
              waterSlider.roundToInt(),
            )
          },
          shape = CircleShape,
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("save_goals_button"),
        ) {
          Text("Save goals", style = MaterialTheme.typography.labelLarge)
        }
      }
    }
  }
}
