package com.example.stripesdemo.data.device

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.casio.ht.devicelibrary.ScannerLibrary
import kotlinx.coroutines.flow.map
import jp.casio.ht.devicelibrary.ScannerLibrary.CONSTANT.OUTPUT.USER
import javax.inject.Inject

private const val CASIO_BROADCAST_ACTION = "device.common.USERMSG"

class CasioScanner @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val scannerLibrary = ScannerLibrary()


    val scansFlow = context.broadcastReceiverFlow(CASIO_BROADCAST_ACTION)
            .map { scannerLibrary.getScanResultSafe() }


    fun initialize(disabledByDefault: Boolean) {
        try {
            scannerLibrary.outputType = USER
            scannerLibrary.openScanner()
            scannerLibrary.notificationVibrator =
                ScannerLibrary.CONSTANT.NOTIFICATION.VIBRATOR_ALL_OFF
            scannerLibrary.notificationSound =
                ScannerLibrary.CONSTANT.NOTIFICATION.SOUND_ALL_OFF

            if (disabledByDefault) {
                disable()
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun enable() {
        scannerLibrary.outputType = USER
        scannerLibrary.triggerKeyEnable = ScannerLibrary.CONSTANT.TRIGGERKEY.ENABLE
    }

    fun disable() {
        scannerLibrary.triggerKeyEnable = ScannerLibrary.CONSTANT.TRIGGERKEY.DISABLE
    }

    fun close() {
        scannerLibrary.closeScanner()
    }
}

private fun ScannerLibrary.getScanResultSafe(): String? {
    try {
        val scan = ScannerLibrary.ScanResult()
        if (this.getScanResult(scan) == ScannerLibrary.CONSTANT.RETURN.SUCCESS && scan.value?.isNotEmpty() == true) {
            return scan.value.map { it.toInt().toChar() }.joinToString("")
        }
    } catch (throwable: Throwable) {
    }
    return null
}


