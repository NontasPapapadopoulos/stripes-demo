package com.example.stripesdemo.presentation.ui.screen.gscan.connect

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.BackButton
import com.example.stripesdemo.presentation.ui.screen.gscan.connect.ConnectFingerScannerConstants.Companion.BACK_BUTTON_TAG
import com.example.stripesdemo.presentation.ui.theme.contentSize5
import com.example.stripesdemo.presentation.ui.theme.contentSpacing2
import com.example.stripesdemo.presentation.ui.theme.contentSpacing4
import com.example.stripesdemo.presentation.ui.theme.contentSpacing6
import com.example.stripesdemo.presentation.ui.theme.contentSpacing8
import com.example.stripesdemo.presentation.utils.createQrCode
import com.example.presentation.R

@Composable
fun ConnectFingerScannerScreen(
    navigateBack: () -> Unit,
    viewModel: ConnectFingerScannerViewModel = hiltViewModel(),
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
        is ConnectFingerScannerState.Content -> {
            ConnectFingerScannerContent(
                content = state,
                navigateBack = navigateBack,
                stopScan = { viewModel.add(ConnectFingerScannerEvent.StopScan) }
            )
        }

        is ConnectFingerScannerState.ConnectionSuccessful -> {
            ConnectionSuccessful(
                navigateBack = {
                    viewModel.add(ConnectFingerScannerEvent.SetDefaultSettings)
                    navigateBack()
                },
                complete = {
                    viewModel.add(ConnectFingerScannerEvent.SetDefaultSettings)
                    viewModel.add(ConnectFingerScannerEvent.Complete)
                           },
            )
        }

        else -> {}

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectFingerScannerContent(
    content: ConnectFingerScannerState.Content,
    stopScan: () -> Unit,
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(stringResource(id = R.string.complete_connection))
                    },
                    navigationIcon = {
                        BackButton(
                            navigateBack = navigateBack,
                            modifier = Modifier.testTag(BACK_BUTTON_TAG)
                        )
                                     },
                )

            }
        }
    ) {

        DisposableEffect(Unit) {
            onDispose {
                stopScan()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(contentSpacing6),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(contentSpacing6))


            Text(
                text = stringResource(id = R.string.connection_qr_code),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(contentSpacing2))


            Text(
                text = stringResource(id = R.string.scan_for_connection),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            if (content.code.isEmpty())
                CircularProgressIndicator()
            else
                Image(
                    bitmap = createQrCode(code = "{G6000/${content.code}}").asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )

            Spacer(modifier = Modifier.weight(1f))

            ConnectionProcessIndicator(content.awaitsForScan)

        }
    }

}

@Composable
private fun ConnectionProcessIndicator(
    awaitsForScan: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {


        CircularProgressIndicator(
            modifier = Modifier.size(contentSize5)
                .padding(bottom = contentSpacing8)
        )

        Spacer(modifier = Modifier.width(contentSpacing6))

        val text = if (awaitsForScan) stringResource(R.string.awaiting_qr_code) else stringResource(R.string.connecting)

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectionSuccessful(
    navigateBack: () -> Unit,
    complete: () -> Unit,
) {

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {

                        Text(stringResource(id = R.string.complete_connection))
                    },
                    navigationIcon = {
                        BackButton(
                            navigateBack = navigateBack,
                            modifier = Modifier.testTag(BACK_BUTTON_TAG)
                        )

                     },
                )

            }
        }
    ) {



        Column(
            modifier = Modifier.fillMaxSize()
                .padding(it)
                .padding(contentSpacing6),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(contentSpacing2))

            Text(
                text = stringResource(id = R.string.connection_successful),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(contentSpacing2))

            Text(
                text = stringResource(id = R.string.finger_scanner_ready_to_use),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(ConnectFingerScannerConstants.COMPLETE_BUTTON_TAG),
                onClick = complete,
            ) {
                Text(text = stringResource(id = R.string.complete))
            }

        }
    }
}


@Composable
@Preview
private fun ConnectFingerScannerScreenPreview() {
    ConnectFingerScannerContent(
        content = ConnectFingerScannerState.Content(
            code = "",
            connectionState = ConnectionState.CONNECTED,
            connectionProcess = ConnectionProcess.Connecting,
            awaitsForScan = true,
        ),
        navigateBack = {},
        stopScan = {},
    )
}


@Composable
@Preview
private fun ConnectionSuccessfulPreview() {
    ConnectionSuccessful (
        navigateBack = {},
        complete = {},
    )

}




class ConnectFingerScannerConstants private constructor() {
    companion object {
        const val COMPLETE_BUTTON_TAG = "complete"
        const val RETRY_BUTTON_TAG = "retry"
        const val BACK_BUTTON_TAG = "back_button"
    }
}


fun checkPermissions(context: Context, permissions: List<String>): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}