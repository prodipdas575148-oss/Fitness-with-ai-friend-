package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CheckCircle
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.HealthTip
import com.example.model.TipCategory

@Composable
fun QuickHealthTipsHeader(
  practicedCount: Int,
  totalCount: Int,
  onCycleFeatured: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = stringResource(R.string.quick_tips_section_title),
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = "$practicedCount of $totalCount practiced today",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.testTag("practiced_tips_counter"),
      )
    }

    TextButton(
      onClick = onCycleFeatured,
      modifier = Modifier
        .minimumInteractiveComponentSize()
        .testTag("next_featured_tip_button"),
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
      Icon(
        imageVector = Icons.Outlined.Refresh,
        contentDescription = null,
        modifier = Modifier.size(16.dp),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Next tip",
        style = MaterialTheme.typography.labelLarge,
      )
    }
  }
}

@Composable
fun FeaturedTipCard(
  tip: HealthTip,
  onToggleBookmark: (String) -> Unit,
  onTogglePracticed: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var showWhy by rememberSaveable(tip.id) { mutableStateOf(false) }

  Surface(
    shape = MaterialTheme.shapes.extraLarge,
    color = MaterialTheme.colorScheme.secondaryContainer,
    modifier = modifier
      .fillMaxWidth()
      .testTag("featured_tip_card"),
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Icon(
            imageVector = Icons.Outlined.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = "Tip of the moment · ${tip.category.label}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
          )
        }

        IconButton(
          onClick = { onToggleBookmark(tip.id) },
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("featured_tip_bookmark_button"),
        ) {
          Icon(
            imageVector =
              if (tip.isBookmarked) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription =
              if (tip.isBookmarked) "Remove tip from saved" else "Save tip",
            tint = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Text(
        text = tip.title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = tip.summary,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
      )

      AnimatedVisibility(visible = showWhy) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = "Why it helps",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = tip.whyItHelps,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = "Try now: ${tip.actionStep}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = CircleShape,
          color =
            if (tip.isPracticed) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceContainerLowest,
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .clickable(role = Role.Checkbox) { onTogglePracticed(tip.id) }
            .testTag("featured_tip_practice_button"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector =
                if (tip.isPracticed) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle,
              contentDescription = null,
              tint =
                if (tip.isPracticed) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp),
            )
            Text(
              text = if (tip.isPracticed) "Practiced" else "Mark done",
              style = MaterialTheme.typography.labelMedium,
              color =
                if (tip.isPracticed) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface,
            )
          }
        }

        TextButton(
          onClick = { showWhy = !showWhy },
          modifier = Modifier
            .heightIn(min = 48.dp)
            .testTag("featured_tip_expand_button"),
        ) {
          Text(
            text = if (showWhy) "Hide details" else "Why it helps",
            style = MaterialTheme.typography.labelLarge,
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = if (showWhy) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

@Composable
fun TipCategoryFilterRow(
  selectedCategory: TipCategory,
  savedCount: Int,
  onSelectCategory: (TipCategory) -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    TipCategory.entries.forEach { category ->
      val isSelected = category == selectedCategory
      val label =
        if (category == TipCategory.SAVED) "Saved ($savedCount)" else category.label
      val bg =
        if (isSelected) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.surfaceContainerLow
      val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outlineVariant
      val textColor =
        if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
        else MaterialTheme.colorScheme.onSurface

      Box(
        modifier = Modifier
          .heightIn(min = 48.dp)
          .clip(CircleShape)
          .background(bg)
          .border(1.dp, borderColor, CircleShape)
          .clickable(role = Role.Tab) { onSelectCategory(category) }
          .padding(horizontal = 14.dp, vertical = 12.dp)
          .testTag("tip_filter_${category.name.lowercase()}"),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = label,
          style = MaterialTheme.typography.labelMedium,
          color = textColor,
        )
      }
    }
  }
}

@Composable
fun QuickHealthTipCard(
  tip: HealthTip,
  onToggleBookmark: (String) -> Unit,
  onTogglePracticed: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by rememberSaveable(tip.id) { mutableStateOf(false) }

  Surface(
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large)
      .clip(MaterialTheme.shapes.large)
      .clickable { expanded = !expanded }
      .testTag("health_tip_card_${tip.id}"),
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = categoryIcon(tip.category),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(20.dp),
          )
        }

        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Text(
              text = tip.category.label,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
            )
            Text(
              text = "· ${tip.readTimeLabel}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          Text(
            text = tip.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = tip.summary,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }

        IconButton(
          onClick = { onToggleBookmark(tip.id) },
          modifier = Modifier
            .minimumInteractiveComponentSize()
            .testTag("bookmark_tip_${tip.id}"),
        ) {
          Icon(
            imageVector =
              if (tip.isBookmarked) Icons.Rounded.Bookmark else Icons.Outlined.BookmarkBorder,
            contentDescription = if (tip.isBookmarked) "Remove bookmark" else "Save tip",
            tint =
              if (tip.isBookmarked) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }

      AnimatedVisibility(visible = expanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Text(
            text = tip.whyItHelps,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = "Action step: ${tip.actionStep}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .background(
              if (tip.isPracticed) MaterialTheme.colorScheme.secondaryContainer
              else MaterialTheme.colorScheme.surfaceContainerLow,
            )
            .clickable(role = Role.Checkbox) { onTogglePracticed(tip.id) }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("practice_tip_${tip.id}"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Icon(
            imageVector =
              if (tip.isPracticed) Icons.Rounded.CheckCircle else Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint =
              if (tip.isPracticed) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
          )
          Text(
            text = if (tip.isPracticed) "Practiced today" else "Mark practiced",
            style = MaterialTheme.typography.labelMedium,
            color =
              if (tip.isPracticed) MaterialTheme.colorScheme.onSecondaryContainer
              else MaterialTheme.colorScheme.onSurface,
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
          Text(
            text = if (expanded) "Less" else "Read tip",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
          )
          Icon(
            imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp),
          )
        }
      }
    }
  }
}

@Composable
fun EmptyTipsStateCard(
  onClearFilters: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = MaterialTheme.shapes.large,
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    modifier = modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.large),
  ) {
    Column(
      modifier = Modifier.padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Icon(
        imageVector = Icons.Outlined.SearchOff,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(32.dp),
      )
      Text(
        text = "No matching health tips",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = "Try another keyword or clear your category filter to browse all wellness tips.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      OutlinedButton(
        onClick = onClearFilters,
        shape = CircleShape,
        modifier = Modifier
          .heightIn(min = 48.dp)
          .testTag("clear_tip_filters_button"),
      ) {
        Text("Show all tips", style = MaterialTheme.typography.labelLarge)
      }
    }
  }
}

private fun categoryIcon(category: TipCategory): ImageVector {
  return when (category) {
    TipCategory.FITNESS -> Icons.Outlined.DirectionsRun
    TipCategory.HYDRATION -> Icons.Outlined.WaterDrop
    TipCategory.NUTRITION -> Icons.Outlined.Eco
    TipCategory.SLEEP -> Icons.Outlined.Bedtime
    TipCategory.ALL, TipCategory.SAVED -> Icons.Outlined.AutoAwesome
  }
}
