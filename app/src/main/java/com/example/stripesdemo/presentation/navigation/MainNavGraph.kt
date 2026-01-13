package com.example.stripesdemo.presentation.navigation


import AdvancedFingerScannerSettings
import ConnectFingerScanner
import FingerScannerSettings
import Scan
import ScanList
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.stripesdemo.presentation.ui.screen.gscan.AdvancedSettingsScreen
import com.example.stripesdemo.presentation.ui.screen.gscan.FingerScannerScreen
import com.example.stripesdemo.presentation.ui.screen.gscan.connect.ConnectFingerScannerScreen
import com.example.stripesdemo.presentation.ui.screen.scan.ScanScreen
import com.example.stripesdemo.presentation.ui.screen.scanlist.ScanListScreen

@Composable
fun MainNavGraph(
    navController: NavHostController,
) {

    NavHost(
        navController = navController,
        startDestination = Scan
    ) {
        composable<Scan> {
            ScanScreen (
                navigateToScanList = {
                    navController.navigate(ScanList)
                },
                navigateToFingerScanner = {
                    navController.navigate(FingerScannerSettings)
                }
            )
        }

        composable<ScanList> {
            ScanListScreen(
                navigateBack = { navController.popBackStack() }
            )
        }


        composable<FingerScannerSettings> {
            FingerScannerScreen(
                navigateBack = { navController.popBackStack() },
                navigateToPairFingerScanner = { navController.navigate(ConnectFingerScanner) },
                navigateToAdvancedSettings = {}
            )
        }

        composable<ConnectFingerScanner> {
            ConnectFingerScannerScreen(
                navigateBack = { navController.popBackStack() }
            )
        }

        composable<AdvancedFingerScannerSettings> {
            AdvancedSettingsScreen(
                navigateBack = { navController.popBackStack() }
            )
        }


    }

}