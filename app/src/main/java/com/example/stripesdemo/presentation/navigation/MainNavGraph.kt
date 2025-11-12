package com.example.stripesdemo.presentation.navigation


import FingerScannerSettings
import GscanConnection
import Scan
import ScanList
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.stripesdemo.presentation.ui.screen.gscan.GscanConnectionScreen
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
                    navController.navigate(GscanConnection)
                }
            )
        }

        composable<ScanList> {
            ScanListScreen(
                navigateBack = { navController.popBackStack() }
            )
        }



        composable<GscanConnection> {
            GscanConnectionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

//        fingerScannerNavGraph(
//            navController = navController,
//        )

    }

}