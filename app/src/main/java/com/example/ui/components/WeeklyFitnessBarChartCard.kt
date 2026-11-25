package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.model.DailyFitnessSummary
import com.example.model.DayMovement
import com.example.model.WeeklyChartMetric
import com.example.ui.theme.MonoMetricMediumStyle
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun WeeklyFitnessBarChartCard(
  summary: DailyFitnessSummary,
  selectedMetric: WeeklyChartMetric,
  selectedDayIndex: Int,
  onSelectMetric: (WeeklyChartMetric) -> Unit,
  onSelectDay: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  val numberFormat = NumberFormat.getIntegerInstance(Locale.US)
  val days = summary.weeklyMovement
  val goalValue =
    when (selectedMetric) {
      WeeklyChartMetric.MINUTES -> summary.activeMinutesGoal
      WeeklyChartMetric.STEPS -> summary.stepsGoal
    }
  val values =
    days.map { day ->
      when (selectedMetric) {
        WeeklyChartMetric.MINUTES -> day.minutes
        WeeklyChartMetric.STEPS -> day.steps
      }
    }
  val maxChartValue = max(goalValue, values.maxOrNull() ?: goalValue).let { raw ->
    when (selectedMetric) {
      WeeklyChartMetric.MINUTES -> ((raw + 14) / 15) * 15
      WeeklyChartMetric.STEPS -> ((raw + 1999) / 2000) * 2000
    }
  }.coerceAtLeast(1)

  val weeklyTotal = values.sum()
  val dailyAvg = if (days.isNotEmpty()) (weeklyTotal.toFloat() / days.size).roundToInt() else 0
  val daysGoalMet = values.count { it >= goalValue }
  val selectedDay = days.getOrNull(selectedDayIndex) ?: days.lastOrNull()

  Surface(
    shape = MaterialTheme.shapes.extraLarge,
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.extraLarge)
      .testTag("weekly_fitness_bar_chart_card"),
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      // Header & Metric Switcher
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Rounded.BarChart,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
            )
          }
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              text = "Weekly fitness progress",
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
              text = "$daysGoalMet of ${days.size} days hit goal",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }

        MetricTogglePill(
          selectedMetric = selectedMetric,
          onSelectMetric = onSelectMetric,
        )
      }

      // Totals Summary Strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(MaterialTheme.shapes.large)
          .background(MaterialTheme.colorScheme.surfaceContainerLow)
          .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = "7-day total",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = formatMetricValue(weeklyTotal, selectedMetric, numberFormat),
            style = MonoMetricMediumStyle,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("weekly_chart_total"),
          )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "Daily avg",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = formatMetricValue(dailyAvg, selectedMetric, numberFormat),
            style = MonoMetricMediumStyle,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Daily goal",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = formatMetricValue(goalValue, selectedMetric, numberFormat),
            style = MonoMetricMediumStyle,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }

      // Custom Canvas Bar Chart
      WeeklyCanvasChartBody(
        days = days,
        values = values,
        maxChartValue = maxChartValue,
        goalValue = goalValue,
        selectedMetric = selectedMetric,
        selectedDayIndex = selectedDayIndex,
        onSelectDay = onSelectDay,
        numberFormat = numberFormat,
      )

      // Selected Day Detail Callout
      selectedDay?.let { day ->
        SelectedDayBanner(
          day = day,
          goalValue = goalValue,
          selectedMetric = selectedMetric,
          numberFormat = numberFormat,
        )
      }
    }
  }
}

@Composable
private fun MetricTogglePill(
  selectedMetric: WeeklyChartMetric,
  onSelectMetric: (WeeklyChartMetric) -> Unit,
) {
  Row(
    modifier = Modifier
      .clip(CircleShape)
      .background(MaterialTheme.colorScheme.surfaceContainerLow)
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
      .padding(3.dp),
    horizontalArrangement = Arrangement.spacedBy(2.dp),
  ) {
    WeeklyChartMetric.entries.forEach { metric ->
      val isSelected = metric == selectedMetric
      Box(
        modifier = Modifier
          .heightIn(min = 38.dp)
          .clip(CircleShape)
          .background(
            if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceContainerLow,
          )
          .clickable(role = Role.Tab) { onSelectMetric(metric) }
          .padding(horizontal = 12.dp, vertical = 8.dp)
          .testTag("chart_metric_${metric.name.lowercase()}"),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = metric.label,
          style = MaterialTheme.typography.labelMedium,
          color =
            if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
private fun WeeklyCanvasChartBody(
  days: List<DayMovement>,
  values: List<Int>,
  maxChartValue: Int,
  goalValue: Int,
  selectedMetric: WeeklyChartMetric,
  selectedDayIndex: Int,
  onSelectDay: (Int) -> Unit,
  numberFormat: NumberFormat,
) {
  val animatedRatios = values.mapIndexed { idx, value ->
    val ratio = (value.toFloat() / maxChartValue.toFloat()).coerceIn(0.04f, 1f)
    val anim by animateFloatAsState(
      targetValue = ratio,
      animationSpec = tween(durationMillis = 360),
      label = "weeklyBarAnim_$idx",
    )
    anim
  }

  val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
  val goalLineColor = MaterialTheme.colorScheme.primary
  val primaryBarTop = MaterialTheme.colorScheme.primary
  val primaryBarBottom = MaterialTheme.colorScheme.primaryContainer
  val goalMetTop = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
  val goalMetBottom = MaterialTheme.colorScheme.secondaryContainer
  val baseBarTop = MaterialTheme.colorScheme.secondaryContainer
  val baseBarBottom = MaterialTheme.colorScheme.surfaceVariant
  val selectedHighlight = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(172.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      // Y-Axis Labels Column
      Column(
        modifier = Modifier
          .width(44.dp)
          .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.End,
      ) {
        Text(
          text = formatCompactAxisLabel(maxChartValue, selectedMetric, numberFormat),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = formatCompactAxisLabel(maxChartValue / 2, selectedMetric, numberFormat),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
          text = "0",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      // Main Interactive Custom Canvas
      Canvas(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
          .pointerInput(days.size) {
            detectTapGestures { offset ->
              if (days.isNotEmpty() && size.width > 0) {
                val slotWidth = size.width.toFloat() / days.size.toFloat()
                val tappedIndex = (offset.x / slotWidth).toInt().coerceIn(0, days.size - 1)
                onSelectDay(tappedIndex)
              }
            }
          }
          .testTag("weekly_fitness_canvas"),
      ) {
        val chartWidth = size.width
        val chartHeight = size.height
        val barCount = days.size.coerceAtLeast(1)
        val slotWidth = chartWidth / barCount
        val barWidth = (slotWidth * 0.54f).coerceAtMost(36.dp.toPx())
        val cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx()), 0f)

        // Horizontal gridlines (0%, 50%, 100%)
        listOf(0f, 0.5f, 1f).forEach { fraction ->
          val y = chartHeight * (1f - fraction)
          drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(chartWidth, y),
            strokeWidth = 1.dp.toPx(),
            pathEffect = if (fraction > 0f) dashEffect else null,
          )
        }

        // Daily Goal Dashed Threshold Line
        val goalRatio = (goalValue.toFloat() / maxChartValue.toFloat()).coerceIn(0f, 1f)
        val goalY = chartHeight * (1f - goalRatio)
        drawLine(
          color = goalLineColor,
          start = Offset(0f, goalY),
          end = Offset(chartWidth, goalY),
          strokeWidth = 1.5.dp.toPx(),
          pathEffect = dashEffect,
        )

        // Draw Bars
        animatedRatios.forEachIndexed { index, ratio ->
          val barCenterX = (index * slotWidth) + (slotWidth / 2f)
          val barLeft = barCenterX - (barWidth / 2f)
          val barHeight = (chartHeight * ratio).coerceAtLeast(6.dp.toPx())
          val barTop = chartHeight - barHeight
          val isSelected = index == selectedDayIndex
          val isToday = days[index].isToday
          val hitGoal = values[index] >= goalValue

          if (isSelected) {
            drawRoundRect(
              color = selectedHighlight,
              topLeft = Offset(barLeft - 4.dp.toPx(), 0f),
              size = Size(barWidth + 8.dp.toPx(), chartHeight),
              cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
            )
          }

          val brush = when {
            isSelected || isToday -> Brush.verticalGradient(
              colors = listOf(primaryBarTop, primaryBarBottom),
              startY = barTop,
              endY = chartHeight,
            )
            hitGoal -> Brush.verticalGradient(
              colors = listOf(goalMetTop, goalMetBottom),
              startY = barTop,
              endY = chartHeight,
            )
            else -> Brush.verticalGradient(
              colors = listOf(baseBarTop, baseBarBottom),
              startY = barTop,
              endY = chartHeight,
            )
          }

          drawRoundRect(
            brush = brush,
            topLeft = Offset(barLeft, barTop),
            size = Size(barWidth, barHeight),
            cornerRadius = cornerRadius,
          )
        }
      }
    }

    // X-Axis Day Labels Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(start = 52.dp),
    ) {
      days.forEachIndexed { index, day ->
        val isSelected = index == selectedDayIndex
        Box(
          modifier = Modifier
            .weight(1f)
            .clip(CircleShape)
            .clickable { onSelectDay(index) }
            .padding(vertical = 4.dp)
            .testTag("chart_day_label_$index"),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = day.dayLabel,
            style =
              if (isSelected || day.isToday) MaterialTheme.typography.labelLarge
              else MaterialTheme.typography.labelSmall,
            color =
              when {
                isSelected || day.isToday -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
              },
          )
        }
      }
    }
  }
}

@Composable
private fun SelectedDayBanner(
  day: DayMovement,
  goalValue: Int,
  selectedMetric: WeeklyChartMetric,
  numberFormat: NumberFormat,
) {
  val dayValue =
    when (selectedMetric) {
      WeeklyChartMetric.MINUTES -> day.minutes
      WeeklyChartMetric.STEPS -> day.steps
    }
  val metGoal = dayValue >= goalValue
  val dayTitle = if (day.isToday) "${day.fullDayName} (Today)" else day.fullDayName

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.medium)
      .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f))
      .padding(horizontal = 12.dp, vertical = 10.dp)
      .testTag("selected_day_banner"),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = dayTitle,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = "${numberFormat.format(day.steps)} steps · ${day.minutes} min active",
        style = MonoMetricMediumStyle,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
      )
    }

    if (metGoal) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.CheckCircle,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp),
        )
        Text(
          text = "Goal met",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    } else {
      val remaining = goalValue - dayValue
      Text(
        text = "${formatMetricValue(remaining, selectedMetric, numberFormat)} left",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

private fun formatMetricValue(
  value: Int,
  metric: WeeklyChartMetric,
  numberFormat: NumberFormat,
): String {
  return when (metric) {
    WeeklyChartMetric.MINUTES -> "$value min"
    WeeklyChartMetric.STEPS -> numberFormat.format(value)
  }
}

private fun formatCompactAxisLabel(
  value: Int,
  metric: WeeklyChartMetric,
  numberFormat: NumberFormat,
): String {
  return when (metric) {
    WeeklyChartMetric.MINUTES -> "${value}m"
    WeeklyChartMetric.STEPS ->
      if (value >= 1000) {
        val k = value / 1000f
        if (value % 1000 == 0) "${value / 1000}k" else String.format(Locale.US, "%.1fk", k)
      } else {
        numberFormat.format(value)
      }
  }
}
