package com.caglaakgul.martilocationtrackercase.presentation.tracking.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.caglaakgul.martilocationtrackercase.R
import com.caglaakgul.martilocationtrackercase.presentation.common.noRippleClickable
import com.caglaakgul.martilocationtrackercase.presentation.tracking.TrackingUiState
import com.caglaakgul.martilocationtrackercase.presentation.tracking.mapper.defaultTrackingTexts
import com.caglaakgul.martilocationtrackercase.ui.theme.MartiLocationTrackerCaseTheme

@Composable
fun AddressPanel(
    address: String?,
    isLoading: Boolean,
    texts: TrackingUiState.Texts,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (address == null && !isLoading) return

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = texts.addressTitle,
                    style = MaterialTheme.typography.titleSmall
                )

                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .noRippleClickable(onCloseClick),
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.close)
                )
            }


            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(top = 12.dp).size(32.dp)
                )
            } else {
                Text(
                    text = address.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddressPanelPreview() {
    MartiLocationTrackerCaseTheme {
        AddressPanel(
            address = "Cengiz Topel Cad., Tuzla/Istanbul",
            isLoading = false,
            texts = defaultTrackingTexts(),
            onCloseClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun AddressPanelLoadingPreview() {
    MartiLocationTrackerCaseTheme {
        AddressPanel(
            address = null,
            isLoading = true,
            texts = defaultTrackingTexts(),
            onCloseClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}