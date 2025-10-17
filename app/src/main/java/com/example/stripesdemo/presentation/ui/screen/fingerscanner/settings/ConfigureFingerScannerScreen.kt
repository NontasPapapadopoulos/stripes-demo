package com.example.stripesdemo.presentation.ui.screen.fingerscanner.settings

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.BackButton
import com.example.stripesdemo.presentation.ui.theme.contentSpacing2
import com.example.stripesdemo.presentation.ui.theme.contentSpacing4
import com.example.stripesdemo.presentation.ui.theme.contentSpacing6
import com.example.stripesdemo.presentation.utils.createQrCode
import com.example.presentation.R

//import com.example.stripesdemo.presentation


@Composable
fun ConfigureFingerScannerScreen(
    navigateBack: () -> Unit,
    navigateToConnectFingerScanner: () -> Unit,
    viewModel: ConfigureFingerScannerViewModel = hiltViewModel(),
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
        is ConfigureFingerScannerState.Content -> {
            ConfigureFingerScannerContent(
                content = state,
                navigateBack = navigateBack,
                navigateToConnectFingerScanner = navigateToConnectFingerScanner,
            )
        }
    }



}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigureFingerScannerContent(
    content: ConfigureFingerScannerState.Content,
    navigateBack: () -> Unit,
    navigateToConnectFingerScanner: () -> Unit,

) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.confugure_scanner),
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
                .padding(contentSpacing4),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(contentSpacing6))


            Text(
                text = stringResource(id = R.string.settings_qr_code),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(contentSpacing2))

            Text(
                text = stringResource(id = R.string.scan_for_settings),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.weight(1f))

            if (content.settings.isNullOrEmpty()) {
                CircularProgressIndicator()
            }
            else {
                Image(
                    bitmap = createQrCode(code = content.settings).asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }



            Spacer(modifier = Modifier.weight(1f))


            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                Button(
                    onClick = navigateBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = stringResource(id = R.string.back))
                }

                Button(
                    onClick = navigateToConnectFingerScanner,
                    modifier = Modifier.weight(2f)
                ) {
                    Text(text = stringResource(id = R.string.next))
                }

            }

        }
    }

}



@Composable
@Preview
private fun ConfigureFingerScannerScreenPreview() {
    ConfigureFingerScannerContent(
        navigateBack = {},
        navigateToConnectFingerScanner = {},
        content = ConfigureFingerScannerState.Content(
            settings = "xxxx"
        ),
    )

}