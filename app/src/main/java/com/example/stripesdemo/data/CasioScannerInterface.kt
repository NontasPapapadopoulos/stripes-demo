package com.example.stripesdemo.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.stripesdemo.domain.entity.enums.Scanner
import com.example.stripesdemo.domain.repository.SettingsRepository
import com.example.stripesdemo.domain.utils.throttleFirst
import jp.casio.ht.devicelibrary.ScannerLibrary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import jp.casio.ht.devicelibrary.ScannerLibrary.CONSTANT.OUTPUT.USER


private const val CASIO_BROADCAST_ACTION = "device.common.USERMSG"


class CasioScannerInterface(
    val context: Context,
    settingsRepository: SettingsRepository,
    coroutineScope: CoroutineScope,
    private val fingerScanner: FingerScanner
) : ScannerInterface {

    private val scannerLibrary = ScannerLibrary()

    override val enabled = MutableStateFlow(false)

    private var _scanner: Scanner = Scanner.Regular
    override val scanner: Scanner
        get() = _scanner


    private val scansFlow = merge(
        context.broadcastReceiverFlow(CASIO_BROADCAST_ACTION)
            .map { scannerLibrary.getScanResultSafe() }
            .onEach { setScanner(Scanner.Regular) },
        fingerScanner.inputFlow
            .onEach { setScanner(Scanner.Finger) },
    )
        .filterNotNull()
        .shareIn(coroutineScope, SharingStarted.WhileSubscribed())


    @ExperimentalCoroutinesApi
    override val values = settingsRepository.getSettingsFlow().flatMapLatest { settings ->
        scansFlow.throttleFirst(settings?.scansDelay ?: 200L)
    }

    override suspend fun initialize(disabledByDefault: Boolean) {
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

    override suspend fun cleanup() {
        scannerLibrary.closeScanner()
    }

    override fun enable() {
        scannerLibrary.outputType = USER
        scannerLibrary.triggerKeyEnable = ScannerLibrary.CONSTANT.TRIGGERKEY.ENABLE
        enabled.value = true

        fingerScanner.setEnabled(true)
    }

    override fun disable() {
        scannerLibrary.triggerKeyEnable = ScannerLibrary.CONSTANT.TRIGGERKEY.DISABLE
        enabled.value = false

        fingerScanner.setEnabled(false)
    }

    private fun setScanner(scanner: Scanner) {
        _scanner = scanner
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

fun Context.broadcastReceiverFlow(
    action: String,
    broadcastPermission: String? = null
): Flow<Intent> = callbackFlow {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            trySendBlocking(intent)
        }
    }

    if (broadcastPermission == null) {
        registerReceiver(receiver, IntentFilter(action))
    } else {
        registerReceiver(receiver, IntentFilter(action), broadcastPermission, null)
    }

    awaitClose { unregisterReceiver(receiver) }
}
