package com.example.stripesdemo.presentation.ui.screen.scan

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.DoNotTouch
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.presentation.ui.icons.Barcode
import com.example.stripesdemo.presentation.ui.composables.LoadingBox
import com.example.stripesdemo.presentation.ui.icons.StripesIcons
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.mapKeys


@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    navigateToScanList: () -> Unit,
    navigateToFingerScanner: () -> Unit,
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
        is ScanState.Content -> {
            ScanContent(
                state = state,
                onNavigateToScanList = navigateToScanList,
                onNavigateToFingerScanner = navigateToFingerScanner,
                triggerCameraScan = { viewModel.add(ScanEvent.TriggerCameraScan) },
                onCountChanged = { viewModel.add(ScanEvent.CountChanged(it)) },
                onBarcodeChanged = { viewModel.add(ScanEvent.BarcodeChanged(it)) },
                submitScan = { viewModel.add(ScanEvent.SubmitScan) },
                setScannerEnabled = { viewModel.add(ScanEvent.SetScannerEnabled(it)) },
                onDisconnect = { viewModel.add(ScanEvent.Disconnect) }
            )
        }
        ScanState.Idle -> {
            LoadingBox()
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScanContent(
    state: ScanState.Content,
    onNavigateToScanList: () -> Unit,
    onNavigateToFingerScanner: () -> Unit,
    triggerCameraScan: () -> Unit,
    submitScan: () -> Unit,
    onBarcodeChanged: (String) -> Unit,
    onCountChanged: (String) -> Unit,
    setScannerEnabled: (Boolean) -> Unit,
    onDisconnect: () -> Unit,
) {

    val mappings = mapOf(
        Key.Enter to {
        if (state.isSubmitEnabled)
            submitScan()
        }
    )

    Scaffold(
        modifier = Modifier.mapKeys(mappings = mappings),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Scan")
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToFingerScanner
                    ) {
                        Icon(
                            when (state.connectionState) {
                                ConnectionState.DISCONNECTED -> Icons.Outlined.DoNotTouch
                                ConnectionState.CONNECTED -> Icons.Outlined.PanToolAlt
                                ConnectionState.CONNECTING -> Icons.Outlined.Sync
                                ConnectionState.DISCONNECTING -> Icons.Outlined.Sync
                            } ,
                            null
                            )
                    }


                    IconButton(onClick = onNavigateToScanList) {
                        Icon(Icons.Default.List, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = triggerCameraScan,
            ) {
                Icon(StripesIcons.Barcode,null)
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(it)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val focusRequester = remember { FocusRequester() }
            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current

            OutlinedTextField(
                value = state.barcode,
                onValueChange = onBarcodeChanged,
                label = {
                    Text(text = "Barcode")
                },
                singleLine = true,
                keyboardActions = KeyboardActions(

                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
                    .onFocusEvent {
                        setScannerEnabled(true)
                    }
                    .focusRequester(focusRequester)
            )


            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                keyboardController?.hide()
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.count,
                onValueChange = { count ->
                    if (count.isDigitsOnly())
                        onCountChanged(count)
                                },
                label = {
                    Text(text = "Count")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (state.isSubmitEnabled) {
                            submitScan()
                            focusManager.moveFocus(FocusDirection.Up)
                        }
                    }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                    .onFocusEvent {
                        setScannerEnabled(false)
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = submitScan,
                enabled = state.isSubmitEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit Scan")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total Scans: ${state.numberOfScans}",
                style = MaterialTheme.typography.titleSmall
            )


            Button(
                onClick = onDisconnect
            ) {
                Text("Disconnect")
            }

        }
    }
}


@Composable
@Preview
private fun ScanScreenPreview() {
    ScanContent(
        state = ScanState.Content(
            barcode = "12312312321",
            count = "1",
            isSubmitEnabled = true,
            numberOfScans = 3,
            connectionState = ConnectionState.CONNECTING,
            isScannerEnabled = true
        ),
        onNavigateToScanList = {},
        triggerCameraScan = {},
        onCountChanged = {},
        onBarcodeChanged = {},
        submitScan = {},
        onNavigateToFingerScanner = {},
        setScannerEnabled = {},
        onDisconnect = {}
    )
}