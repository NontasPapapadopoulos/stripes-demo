package com.example.stripesdemo.presentation.ui.composables.scandit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.presentation.BuildConfig
import com.scandit.datacapture.core.capture.DataCaptureContext

@Composable
internal fun rememberDataCaptureContext(): DataCaptureContext {

    // Add your license key to `secrets.properties` and it will be automatically added to the BuildConfig field
    // `BuildConfig.SCANDIT_LICENSE_KEY`
    // Create data capture context using your license key.
    val dataCaptureContext = remember(BuildConfig.SCANDIT_LICENSE_KEY) {
        DataCaptureContext.forLicenseKey(BuildConfig.SCANDIT_LICENSE_KEY)
    }

    return dataCaptureContext
}