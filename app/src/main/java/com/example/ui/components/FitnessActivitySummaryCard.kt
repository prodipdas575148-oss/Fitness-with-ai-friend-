package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.DailyFitnessSummary
import com.example.ui.theme.MonoMetricLargeStyle
import com.example.ui.theme.MonoMetricMediumStyle
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun FitnessActivitySummaryCard(
  summary: DailyFitnessSummary,
  onAddSteps: () -> Unit,
  onAddActiveMinutes: () -> Unit,
  onAddWater: () -> Unit,
  onRemoveWater: () -> Unit,
  onEditGoals: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val numberFormat = NumberFormat.getIntegerInstance(Locale.US)
  val animatedStepsProgress by
    animateFloatAsState(
      targetValue = summary.stepsProgress,
      animationSpec = tween(durationMillis = 360),
      label = "stepsProgressAnim",
    )
  val animatedWaterProgress by
    animateFloatAsState(
      targetValue = summary.waterProgress,
      animationSpec = tween(durationMillis = 280),
      label = "waterProgressAnim",
    )

  Surface(
    shape = MaterialTheme.shapes.extraLarge,
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.extraLarge)
      .testTag("daily_fitness_summary_card"),
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      // Card Title + Goal Edit
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = stringResource(R.string.fitness_section_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = "${(summary.stepsProgress * 100).roundToInt()}% of daily step goal",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
          )
        }

        TextButton(
          onClick = onEditGoals,
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("edit_fitness_goals_button"),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        ) {
          Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Goals",
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }

      // Steps Ring + Key Fitness Metrics
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        StepsRingIndicator(
          progress = animatedStepsProgress,
          stepsText = numberFormat.format(summary.steps),
          goalText = "/ ${numberFormat.format(summary.stepsGoal)}",
        )

        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          FitnessMetricRow(
            icon = Icons.Outlined.Timer,
            label = "Active time",
            value = "${summary.activeMinutes} / ${summary.activeMinutesGoal} min",
            testTag = "active_minutes_metric",
          )
          FitnessMetricRow(
            icon = Icons.Outlined.LocalFireDepartment,
            label = "Burned",
            value = "${summary.caloriesBurned} kcal",
            testTag = "calories_metric",
          )
          FitnessMetricRow(
            icon = Icons.Outlined.Route,
            label = "Distance",
            value = String.format(Locale.US, "%.1f km", summary.distanceKm),
            testTag = "distance_metric",
          )
        }
      }

      // Quick Fitness Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Button(
          onClick = onAddSteps,
          shape = CircleShape,
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("add_steps_button"),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        ) {
          Icon(
            imageVector = Icons.Outlined.DirectionsWalk,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "+500 steps",
            style = MaterialTheme.typography.labelMedium,
          )
        }

        FilledTonalButton(
          onClick = onAddActiveMinutes,
          shape = CircleShape,
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
          ),
          modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
            .testTag("add_active_minutes_button"),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        ) {
          Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "+10 min move",
            style = MaterialTheme.typography.labelMedium,
          )
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))

      // Hydration Sub-Bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(MaterialTheme.shapes.large)
          .background(MaterialTheme.colorScheme.surfaceContainerLow)
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Outlined.WaterDrop,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp),
            )
            Text(
              text = "Hydration",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }

          Text(
            text = "${summary.waterGlasses} / ${summary.waterGoal} glasses",
            style = MonoMetricMediumStyle,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("water_count_label"),
          )
        }

        val trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
        val fillColor = MaterialTheme.colorScheme.primary
        Canvas(
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(CircleShape),
        ) {
          drawRoundRect(
            color = trackColor,
            size = size,
            cornerRadius = CornerRadius(size.height / 2f, size.height / 2f),
          )
          val fillWidth = (size.width * animatedWaterProgress).coerceIn(0f, size.width)
          if (fillWidth > 0f) {
            drawRoundRect(
              color = fillColor,
              size = Size(width = fillWidth, height = size.height),
              cornerRadius = CornerRadius(size.height / 2f, size.height / 2f),
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "Supports recovery & steady energy",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            IconButton(
              onClick = onRemoveWater,
              enabled = summary.waterGlasses > 0,
              modifier = Modifier
                .minimumInteractiveComponentSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                .testTag("water_minus_button"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = "Remove one glass of water",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp),
              )
            }
            OutlinedButton(
              onClick = onAddWater,
              shape = CircleShape,
              modifier = Modifier
                .heightIn(min = 48.dp)
                .testTag("water_plus_button"),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Glass",
                style = MaterialTheme.typography.labelMedium,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StepsRingIndicator(
  progress: Float,
  stepsText: String,
  goalText: String,
) {
  val trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
  val arcColor = MaterialTheme.colorScheme.primary

  Box(
    modifier = Modifier
      .size(124.dp)
      .testTag("steps_progress_ring"),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.size(124.dp)) {
      val strokePx = 11.dp.toPx()
      val diameter = minOf(size.width, size.height) - strokePx
      val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
      val arcSize = Size(diameter, diameter)

      drawArc(
        color = trackColor,
        startAngle = 0f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokePx, cap = StrokeCap.Round),
      )

      drawArc(
        color = arcColor,
        startAngle = -90f,
        sweepAngle = 360f * progress.coerceIn(0f, 1f),
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = strokePx, cap = StrokeCap.Round),
      )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = stepsText,
        style = MonoMetricLargeStyle,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("steps_count_value"),
      )
      Text(
        text = goalText,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun FitnessMetricRow(
  icon: ImageVector,
  label: String,
  value: String,
  testTag: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(MaterialTheme.colorScheme.surfaceContainerLow)
      .padding(horizontal = 10.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(18.dp),
    )
    Column {
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = value,
        style = MonoMetricMediumStyle.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag(testTag),
      )
    }
  }
}


