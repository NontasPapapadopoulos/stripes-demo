package com.example.stripesdemo.presentation.ui.screen

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.stripesdemo.presentation.ui.Barcode
import com.example.stripesdemo.presentation.ui.LoadingBox
import com.example.stripesdemo.presentation.ui.icons.StripesIcons
import net.stripesapp.mlsretailsoftware.presentation.exception.errorStringResource


@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel(),
    navigateToScanList: () -> Unit
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
                triggerCameraScan = { viewModel.add(ScanEvent.TriggerCameraScan) }
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
    triggerCameraScan: () -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Scan")
                },
                actions = {
                    IconButton(onClick = onNavigateToScanList) {
                        Icon(Icons.Default.List, null)
                    }
                }
            )
        },
        floatingActionButton = {

            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {

                FloatingActionButton(
                    onClick = triggerCameraScan,
                    modifier = Modifier.align(Alignment.Center)
                ) {

                    Icon(StripesIcons.Barcode,null)
                }


                FloatingActionButton(
                    onClick = {}, // advance flow
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                ) {

                    if (state.isSubmitEnabled)
                        Icon(Icons.Filled.Check, null)
                    else
                        Icon(Icons.Filled.ArrowForward, null)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(it)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {


            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {
                    Text(text = "Barcode")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {
                    Text(text = "Count")
                },
                modifier = Modifier.fillMaxWidth()
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
            count = 1,
            isSubmitEnabled = true,

        ),
        onNavigateToScanList = {},
        triggerCameraScan = {}
    )
}