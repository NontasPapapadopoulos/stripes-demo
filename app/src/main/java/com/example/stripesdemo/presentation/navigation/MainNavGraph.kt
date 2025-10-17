package com.example.stripesdemo.presentation.navigation


import Scan
import ScanList
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.stripesdemo.presentation.ui.screen.ScanScreen
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
                    navController.navigate(ScanList) {
//                        removeBackStack(navController)
                    }
                },
            )
        }

        composable<ScanList> {
            ScanListScreen(
                navigateBack = { navController.popBackStack() }
            )
        }

        fingerScannerNavGraph(
            navController = navController,
        )

    }

}