package com.caglaakgul.martilocationtrackercase.presentation.tracking.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.caglaakgul.martilocationtrackercase.presentation.tracking.TrackingUiState
import com.caglaakgul.martilocationtrackercase.presentation.tracking.mapper.defaultTrackingTexts
import com.caglaakgul.martilocationtrackercase.ui.theme.MartiLocationTrackerCaseTheme

@Composable
fun TrackingControls(
    isTracking: Boolean,
    routePointCount: Int,
    hasLocationPermission: Boolean,
    texts: TrackingUiState.Texts,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isTracking) texts.trackingActive else texts.trackingStopped,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$routePointCount ${texts.routePointCountSuffix}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onStartClick,
                    enabled = hasLocationPermission && !isTracking,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = texts.startButton)
                }

                OutlinedButton(
                    onClick = onStopClick,
                    enabled = isTracking,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = texts.stopButton)
                }

                OutlinedButton(
                    onClick = onResetClick,
                    enabled = routePointCount > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = texts.resetButton)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrackingControlsPreview() {
    MartiLocationTrackerCaseTheme {
        TrackingControls(
            isTracking = true,
            routePointCount = 4,
            hasLocationPermission = true,
            texts = defaultTrackingTexts(),
            onStartClick = {},
            onStopClick = {},
            onResetClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}