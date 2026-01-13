package com.example.stripesdemo.presentation.ui.screen.gscan

import com.example.presentation.R

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.outlined.DoNotTouch
import androidx.compose.material.icons.outlined.PanToolAlt
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.BackButton
import com.example.stripesdemo.presentation.ui.composables.LoadingBox
import com.example.stripesdemo.presentation.ui.screen.gscan.connect.checkPermissions
import com.example.stripesdemo.presentation.ui.theme.contentSpacing2
import com.example.stripesdemo.presentation.ui.theme.contentSpacing4


@Composable
fun FingerScannerScreen(
    navigateBack: () -> Unit,
    navigateToPairFingerScanner: () -> Unit,
    navigateToAdvancedSettings: () -> Unit,
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

    val requiredPermissions = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    var hasPermissions by remember {
        mutableStateOf(checkPermissions(context, requiredPermissions))
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Update state when user responds
            hasPermissions = permissions.values.all { it }
        }

    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(requiredPermissions.toTypedArray())
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is FingerScannerState.Content -> {
            FingerScannerContent(
                content = state,
                navigateBack = navigateBack,
                navigateToPairFingerScanner = navigateToPairFingerScanner,
                navigateToAdvancedSettings = navigateToAdvancedSettings,
                setVolume = { viewModel.add(FingerScannerEvent.SetVolume) },
                setVolumeSliderPosition = { viewModel.add(FingerScannerEvent.SetVolumeSliderPosition(it)) },
                setVibration = { viewModel.add(FingerScannerEvent.SetVibration) },
                setVibrationSliderPosition = { viewModel.add(FingerScannerEvent.SetVibrationSliderPosition(it)) },
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
    setVolumeSliderPosition: (Float) -> Unit,
    setVibration: () -> Unit,
    setVibrationSliderPosition: (Float) -> Unit,
    navigateBack: () -> Unit,
    navigateToPairFingerScanner: () -> Unit,
    navigateToAdvancedSettings: () -> Unit,
) {

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(stringResource(id = R.string.finger_scanner),)
                    },
                    navigationIcon = { BackButton(navigateBack = navigateBack) },
                    actions = {

                      Icon(
                            if (content.isConnected) Icons.Outlined.PanToolAlt else Icons.Outlined.DoNotTouch,
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


            PairStatus(
                macAddress = content.macAddress,
                batteryLevel = content.batteryLevel
            )

            Column(
                modifier = Modifier.padding(contentSpacing4)
            ) {

                SliderSettingItem(
                    value = content.volume,
                    setValue = setVolume,
                    isEnabled = content.isConnected,
                    setSliderPosition = setVolumeSliderPosition,
                    imageVector = Icons.Default.VolumeDown,
                    setting = stringResource(R.string.volume),
                    valueRange = 0f..183f
                )

                Spacer(modifier = Modifier.height(contentSpacing4))

                SliderSettingItem(
                    value = content.vibration,
                    setValue = setVibration,
                    isEnabled = content.isConnected,
                    setSliderPosition = setVibrationSliderPosition,
                    imageVector = Icons.Default.Vibration,
                    setting = stringResource(R.string.vibration),
                    valueRange = 0f..240f
                )

                Spacer(modifier = Modifier.weight(1f))

                AdvancedSettingsButton(
                    navigateToAdvancedSettings = navigateToAdvancedSettings,
                    isEnabled = content.isConnected
                )

                Spacer(modifier = Modifier.height(contentSpacing4))

                PairNewFingerScannerButton(navigateToPairFingerScanner)

            }
        }
    }
}

@Composable
private fun AdvancedSettingsButton(
    navigateToAdvancedSettings: () -> Unit,
    isEnabled: Boolean
) {
    Button(
        onClick = navigateToAdvancedSettings,
//        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.advanced_settings)
        )
    }
}

@Composable
private fun PairStatus(
    macAddress: String,
    batteryLevel: String
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
                text = macAddress,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row {
                Text(
                    text = batteryLevel,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (batteryLevel.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(contentSpacing2))

                    Icon(
                        Icons.Default.BatteryFull,
                        null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

            }
        }
    }
}



@Composable
private fun SliderSettingItem(
    value: Float,
    imageVector: ImageVector,
    setting: String,
    setValue: () -> Unit,
    setSliderPosition: (Float) -> Unit,
    isEnabled: Boolean,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.width(contentSpacing4))

        Column {
            Text(
                text = setting,
                style = MaterialTheme.typography.bodyLarge
            )
            Slider(
                volume = value,
                setVolume = setValue,
                setSliderPosition = setSliderPosition,
                isEnabled = isEnabled,
                valueRange = valueRange
            )
        }

    }
}

@Composable
fun Slider(
    volume: Float,
    setVolume: () -> Unit,
    setSliderPosition: (Float) -> Unit,
    isEnabled: Boolean,
    valueRange: ClosedFloatingPointRange<Float>
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
        valueRange = valueRange,
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
        setVolumeSliderPosition = {},
        content = FingerScannerState.Content(
            volume = 50f,
            vibration = 50f,
            macAddress = "00:22:33:11:aa:E_32",
            isConnected = true,
            batteryLevel = "33%"
        ),
        setVibration = {},
        setVibrationSliderPosition = {},
        navigateToAdvancedSettings = {}
    )

}

class FingerScannerConstants private constructor() {
    companion object {
        const val SLIDE_BAR = "slide_bar"
    }
}