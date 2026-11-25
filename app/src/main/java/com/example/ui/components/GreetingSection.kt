package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.MoodCheckIn

@Composable
fun GreetingSection(
  greetingHeadline: String,
  userName: String,
  selectedMood: MoodCheckIn,
  searchQuery: String,
  onSelectMood: (MoodCheckIn) -> Unit,
  onSearchQueryChange: (String) -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(
        imageVector = Icons.Outlined.WbSunny,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp),
      )
      Text(
        text = "Today's check-in",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
      )
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(
        text = "$greetingHeadline, $userName",
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("greeting_headline"),
      )
      Text(
        text = selectedMood.encouragement,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.testTag("greeting_encouragement"),
      )
    }

    // Mood check-in chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      MoodCheckIn.entries.forEach { mood ->
        val isSelected = mood == selectedMood
        val bgColor =
          if (isSelected) MaterialTheme.colorScheme.secondaryContainer
          else MaterialTheme.colorScheme.surfaceContainerLow
        val borderColor =
          if (isSelected) MaterialTheme.colorScheme.primary
          else MaterialTheme.colorScheme.outlineVariant
        val textColor =
          if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
          else MaterialTheme.colorScheme.onSurface

        Row(
          modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(role = Role.RadioButton) { onSelectMood(mood) }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("mood_chip_${mood.name.lowercase()}"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          if (isSelected) {
            Icon(
              imageVector = Icons.Rounded.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp),
            )
          }
          Text(
            text = mood.label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
          )
        }
      }
    }

    // Search tips pill
    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      modifier = Modifier
        .fillMaxWidth()
        .heightIn(min = 48.dp)
        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.Search,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp),
        )

        Box(modifier = Modifier.weight(1f)) {
          if (searchQuery.isEmpty()) {
            Text(
              text = stringResource(R.string.search_tips_placeholder),
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
              color = MaterialTheme.colorScheme.onSurface,
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("search_tips_input"),
          )
        }

        if (searchQuery.isNotEmpty()) {
          IconButton(
            onClick = { onSearchQueryChange("") },
            modifier = Modifier
              .minimumInteractiveComponentSize()
              .testTag("clear_search_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Close,
              contentDescription = "Clear search",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }
    }
  }
}
