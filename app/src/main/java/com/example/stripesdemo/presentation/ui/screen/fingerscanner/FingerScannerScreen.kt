package com.example.stripesdemo.presentation.ui.screen.fingerscanner


import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoNotTouch
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material.icons.outlined.VolumeDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.R
import com.example.stripesdemo.domain.entity.DeviceDomainEntity
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.LoadingBox
import com.example.stripesdemo.presentation.ui.composables.BackButton
import com.example.stripesdemo.presentation.ui.theme.contentSpacing4
import com.example.stripesdemo.presentation.ui.theme.contentSpacing6


@Composable
fun FingerScannerScreen(
    navigateBack: () -> Unit,
    navigateToPairFingerScanner: () -> Unit,
    viewModel: FingerScannerViewModel = hiltViewModel(),
) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { error ->
            Toast.makeText(
                context,
                context.resources.errorStringResource(error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is FingerScannerState.Content -> {
            FingerScannerContent(
                content = state,
                setVolume = { viewModel.add(FingerScannerEvent.SetVolume) },
                navigateBack = navigateBack,
                navigateToPairFingerScanner = navigateToPairFingerScanner,
                setSliderPosition = { viewModel.add(FingerScannerEvent.SetSliderPosition(it)) },
            )
        }
        FingerScannerState.Idle -> { LoadingBox() }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FingerScannerContent(
    content: FingerScannerState.Content,
    setVolume: () -> Unit,
    setSliderPosition: (Float) -> Unit,
    navigateBack: () -> Unit,
    navigateToPairFingerScanner: () -> Unit,
) {

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.finger_scanner),
                        )
                    },
                    navigationIcon = { BackButton(navigateBack = navigateBack) },
                    actions = {
                        Icon(
                            if (content.devices.isNotEmpty()) Icons.Outlined.PanToolAlt else Icons.Outlined.DoNotTouch,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = null,
                            modifier = Modifier.padding(contentSpacing4)
                        )
                    }
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {


            PairStatus(scannerId = if (content.devices.isNotEmpty()) content.devices[0].id else "")

            Volume(
                volume = content.volume,
                setVolume = setVolume,
                isEnabled = content.devices.isNotEmpty(),
                setSliderPosition = setSliderPosition,
            )

            Spacer(modifier = Modifier.weight(1f))

            PairNewFingerScannerButton(navigateToPairFingerScanner)

            Spacer(modifier = Modifier.height(contentSpacing4))
        }
    }
}

@Composable
private fun PairStatus(
    scannerId: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.PanToolAlt,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = contentSpacing4)
        )

        Column {
            Text(
                text = stringResource(id = R.string.finger_scanner_paired),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = scannerId,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun Volume(
    volume: Float,
    setVolume: () -> Unit,
    setSliderPosition: (Float) -> Unit,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = contentSpacing4, end = contentSpacing6),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.VolumeDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.width(contentSpacing4))

        Column {
            Text(
                text = stringResource(id = R.string.volume),
                style = MaterialTheme.typography.bodyLarge
            )
            VolumeSlider(
                volume = volume,
                setVolume = setVolume,
                setSliderPosition = setSliderPosition,
                isEnabled = isEnabled
            )
        }

    }
}

@Composable
fun VolumeSlider(
    volume: Float,
    setVolume: () -> Unit,
    setSliderPosition: (Float) -> Unit,
    isEnabled: Boolean
) {

    Slider(
        value = volume,
        onValueChange = { setSliderPosition(it) },
         onValueChangeFinished = setVolume,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        enabled = isEnabled,
        steps = 10,
        valueRange = 0f..100f,
        modifier = Modifier.testTag(FingerScannerConstants.SLIDE_BAR)
    )

}

@Composable
private fun PairNewFingerScannerButton(navigateToPairFingerScanner: () -> Unit) {
    Button(
        onClick = navigateToPairFingerScanner,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = contentSpacing4)
            .border(
                width = 1.dp, MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.extraLarge
            )

    ) {
        Text(
            text = stringResource(id = R.string.pair_new_finger_scanner),
            style = MaterialTheme.typography.labelLarge
        )
    }
}




@Composable
@Preview
private fun FingerScannerSettingsScreenPreview() {
    FingerScannerContent(
        navigateBack = {},
        navigateToPairFingerScanner = {},
        setVolume = {},
        setSliderPosition = {},
        content = FingerScannerState.Content(
            volume = 50f,
            devices = listOf(
                DeviceDomainEntity(
                    id = "00:22:33:11:aa:E_32",
                    name = "name "
                )
            )
        ),
    )

}

class FingerScannerConstants private constructor() {
    companion object {
        const val SLIDE_BAR = "slide_bar"
    }
}