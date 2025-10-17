package com.example.stripesdemo.presentation.ui.screen.scan

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.DoNotTouch
import androidx.compose.material.icons.outlined.PanToolAlt
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.presentation.ui.icons.Barcode
import com.example.stripesdemo.presentation.ui.composables.LoadingBox
import com.example.stripesdemo.presentation.ui.icons.StripesIcons
import com.example.stripesdemo.presentation.exception.errorStringResource


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
                submitScan = { viewModel.add(ScanEvent.SubmitScan) }
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
) {

    Scaffold(
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
                            if (state.devices.isNotEmpty()) Icons.Outlined.PanToolAlt else Icons.Outlined.DoNotTouch,
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            OutlinedTextField(
                value = state.barcode,
                onValueChange = onBarcodeChanged,
                label = {
                    Text(text = "Barcode")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.count,
                onValueChange = { onCountChanged(it) },
                label = {
                    Text(text = "Count")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = submitScan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit Scan")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Total Scans: ${state.numberOfScans}",
                style = MaterialTheme.typography.titleSmall
            )

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
            dialog = null,
            numberOfScans = 3,
            devices = listOf()

        ),
        onNavigateToScanList = {},
        triggerCameraScan = {},
        onCountChanged = {},
        onBarcodeChanged = {},
        submitScan = {},
        onNavigateToFingerScanner = {},
    )
}