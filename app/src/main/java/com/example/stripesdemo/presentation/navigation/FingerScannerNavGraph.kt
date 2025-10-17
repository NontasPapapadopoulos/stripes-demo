package com.example.stripesdemo.presentation.navigation


import ConfigureFingerScanner
import ConnectFingerScanner
import FingerScannerRoute
import FingerScannerSettings
import Scan
import android.annotation.SuppressLint
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.stripesdemo.presentation.ui.screen.fingerscanner.FingerScannerScreen
import com.example.stripesdemo.presentation.ui.screen.fingerscanner.connect.ConnectFingerScannerScreen
import com.example.stripesdemo.presentation.ui.screen.fingerscanner.settings.ConfigureFingerScannerScreen


@SuppressLint("RestrictedApi")
fun NavGraphBuilder.fingerScannerNavGraph(
    navController: NavController,
) {

    navigation<FingerScannerRoute>(
        startDestination = FingerScannerSettings
    ) {

        composable<FingerScannerSettings> {
            FingerScannerScreen(
                navigateBack = { navController.popBackStack() },
                navigateToPairFingerScanner = { navController.navigate(ConfigureFingerScanner) },
            )
        }

        composable<ConfigureFingerScanner> {
            ConfigureFingerScannerScreen(
                navigateBack = { navController.popBackStack() },
                navigateToConnectFingerScanner = { navController.navigate(ConnectFingerScanner) },
            )
        }

        composable<ConnectFingerScanner> {
            ConnectFingerScannerScreen(
                navigateBack = { navController.popBackStack() },
                navigateToSettings = { navController.navigate(Scan) {
                        //removeBackStack(navController)
                    }
                },

            )
        }
    }
}
