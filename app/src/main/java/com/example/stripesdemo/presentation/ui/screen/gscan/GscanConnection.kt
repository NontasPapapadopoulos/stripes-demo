package com.example.stripesdemo.presentation.ui.screen.gscan

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
                onNavigateBack = onNavigateBack,
                onCommandChanged = { viewModel.add(ConnectionEvent.CommandChanged(it))},
                onCommandSubmit = { viewModel.add(ConnectionEvent.SubmitCommand) }
            )
        }
        GscanConnectionState.Idle -> { LoadingBox() }
    }



}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GscanConnectionContent(
    content: GscanConnectionState.Content,
    onNavigateBack: () -> Unit,
    onCommandChanged: (String) -> Unit,
    onCommandSubmit: () -> Unit
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

        val context = LocalContext.current

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

        if (hasPermissions) {
            if (isBluetoothEnabled(context)) {
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

                    Text(text =content.state.name)

                    TextField(
                        value = content.command,
                        onValueChange = onCommandChanged
                    )

                    Button(
                        onClick = onCommandSubmit,
                    ) {
                        Text("Submit Command")
                    }

                }
            }
            else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Bluetooth is disabled."
                    )
                }
            }

        }

        else {


            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Missing Permissions, please enable them."
                )
            }
        }

    }
}

fun checkPermissions(context: Context, permissions: List<String>): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}

fun isBluetoothEnabled(context: Context): Boolean {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val adapter = bluetoothManager.adapter
    return adapter != null && adapter.isEnabled
}