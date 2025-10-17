package com.example.stripesdemo.data

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class MobileScanner @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val scanChannel = Channel<String?>(Channel.BUFFERED)

    val scanFlow: Flow<String?> = scanChannel.receiveAsFlow()

    private val options = GmsBarcodeScannerOptions.Builder()
        .enableAutoZoom()
        .setBarcodeFormats(
            Barcode.FORMAT_CODE_128
        )
        .build()

    private val scanner = GmsBarcodeScanning.getClient(context, options)

    suspend fun scan() {
        try {
            val scan = scanner.startScan()
                .await()
                .rawValue

            sendScanValue(scan)
        }
        catch (e: Exception) {
            sendScanValue(null)
        }
    }

    private suspend fun sendScanValue(value: String?) {
        scanChannel.send(value)
    }


}