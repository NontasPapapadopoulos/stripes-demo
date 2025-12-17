package com.example.stripesdemo.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.stripesdemo.domain.repository.SettingsRepository
import com.example.stripesdemo.presentation.navigation.MainNavGraph
import com.example.stripesdemo.ui.theme.StripesDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkCameraPermissions()
        initSettings()

        setContent {
            val navController = rememberNavController()

            StripesDemoTheme {
                MainNavGraph(navController)
            }
        }
    }

    private fun initSettings() {
        coroutineScope.launch {
            settingsRepository.initSettings()

        }
    }



    private fun checkCameraPermissions() {
        val cameraPermission = Manifest.permission.CAMERA
        val hasPermission = ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(cameraPermission),
                1001
            )
        }
    }
}