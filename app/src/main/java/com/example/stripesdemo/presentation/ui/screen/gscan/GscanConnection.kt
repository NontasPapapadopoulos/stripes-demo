package com.example.stripesdemo.presentation.ui.screen.gscan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.DoNotTouch
import androidx.compose.material.icons.outlined.PanToolAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stripesdemo.presentation.exception.errorStringResource
import com.example.stripesdemo.presentation.ui.composables.BackButton
import com.example.stripesdemo.presentation.ui.composables.LoadingBox
import com.example.stripesdemo.presentation.utils.createQrCode

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
                bitmap = createQrCode(code = content.uuid).asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = content.uuid)

        }

    }
}