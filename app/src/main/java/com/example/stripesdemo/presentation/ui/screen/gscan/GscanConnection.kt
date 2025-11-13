package com.example.stripesdemo.presentation.ui.screen.gscan

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.presentation.BarcodeUtils
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.BackButton
import com.example.stripesdemo.presentation.ui.composables.LoadingBox

@Composable
fun GscanConnectionScreen(
    onNavigateBack: () -> Unit,
    viewModel: GscanConnectionViewmodel = hiltViewModel()
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
        is GscanConnectionState.Content -> {
            GscanConnectionContent(
                content = state,
                onNavigateBack = onNavigateBack
            )
        }
        GscanConnectionState.Idle -> { LoadingBox() }
    }



}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GscanConnectionContent(
    content: GscanConnectionState.Content,
    onNavigateBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Scan")
                },
                navigationIcon = {
                    BackButton(navigateBack = onNavigateBack)
                }
            )
        },
    ) {

        Column(
            modifier = Modifier.padding(it)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                bitmap = BarcodeUtils.createQrCode(content.uuid).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text =content.uuid)

        }

    }
}