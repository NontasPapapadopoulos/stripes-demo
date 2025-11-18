package com.example.stripesdemo.presentation.ui.screen.fingerscanner.connect

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
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.BackButton
import com.example.stripesdemo.presentation.ui.theme.contentSize5
import com.example.stripesdemo.presentation.ui.theme.contentSpacing2
import com.example.stripesdemo.presentation.ui.theme.contentSpacing4
import com.example.stripesdemo.presentation.ui.theme.contentSpacing6
import com.example.stripesdemo.presentation.ui.theme.contentSpacing8
import com.example.stripesdemo.presentation.utils.createQrCode
import com.example.presentation.R
import com.example.stripesdemo.domain.entity.enums.ConnectionState


@Composable
fun ConnectFingerScannerScreen(
    navigateBack: () -> Unit,
    navigateToSettings: () -> Unit,
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


    LaunchedEffect(Unit) {
        viewModel.navigationFlow.collect { navigation ->
           // if (navigation)
                navigateBack()
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ConnectFingerScannerState.Content -> {
            ConnectFingerScannerContent(
                content = state,
                navigateBack = navigateBack,
            )
        }

        is ConnectFingerScannerState.ConnectionSuccessful -> {
            ConnectionSuccessful(
                navigateBack = navigateBack,
                complete = { viewModel.add(ConnectFingerScannerEvent.Complete) },
            )
        }

        ConnectFingerScannerState.ConnectionFailed -> {
            ConnectionFailed(
                navigateBack = navigateBack,
                retry = navigateToSettings,
            )
        }

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectFingerScannerContent(
    content: ConnectFingerScannerState.Content,
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.complete_connection),
                        )
                    },
                    navigationIcon = { BackButton(navigateBack = navigateBack) },
                )

            }
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(24.dp),
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

            if (content.code.isNullOrEmpty()) {
                CircularProgressIndicator()
            }
            else {
                Image(
                    bitmap = createQrCode(code = content.code).asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }



            Text(text = content.connectionState.name)


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
                        Text(
                            text = stringResource(id = R.string.complete_connection)
                        )
                    },
                    navigationIcon = { BackButton(navigateBack = navigateBack) },
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
                Text(text = "Complete")
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectionFailed(
    navigateBack: () -> Unit,
    retry: () -> Unit,
) {

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.complete_connection)
                        )

                    },
                    navigationIcon = { BackButton(navigateBack = navigateBack) },
                )

            }
        }
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(it)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            Icon(
                Icons.Default.AddCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer(rotationZ = 45f)
            )

            Spacer(modifier = Modifier.height(contentSpacing2))


            Text(
                text = stringResource(id = R.string.connection_failed),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(contentSpacing2))

            Text(
                text = "Connection could not complete",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )


            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = retry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = contentSpacing4)
                    .border(
                        width = 1.dp, MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = MaterialTheme.shapes.extraLarge
                    )
                    .testTag(ConnectFingerScannerConstants.RETRY_BUTTON_TAG)

            ) {
                Text(
                    text = "Retry",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}




@Composable
@Preview
private fun ConnectFingerScannerScreenPreview() {
    ConnectFingerScannerContent(
        content = ConnectFingerScannerState.Content(
            code = "xxxx",
            awaitsForScan = true,
            connectionState = ConnectionState.DISCONNECTED
        ),
        navigateBack = {},
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


@Composable
@Preview
private fun ConnectionFailedPreview() {
    ConnectionFailed (
        navigateBack = {},
        retry = {},
    )

}


class ConnectFingerScannerConstants private constructor() {
    companion object {
        const val COMPLETE_BUTTON_TAG = "complete"
        const val RETRY_BUTTON_TAG = "retry"
    }
}