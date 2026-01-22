package com.example.stripesdemo.data.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.stripesdemo.domain.entity.enums.Scanner
import com.example.stripesdemo.domain.repository.SettingsRepository
import com.example.stripesdemo.domain.utils.throttleFirst
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


class MultipleScanner(
    val context: Context,
    settingsRepository: SettingsRepository,
    coroutineScope: CoroutineScope,
    private val fingerScanner: GeneralScanLibrary,
) : ScannerInterface {


    override val enabled = MutableStateFlow(false)

    private var _scanner: Scanner = Scanner.Regular
    override val scanner: Scanner
        get() = _scanner


    private val scansFlow = merge(
        fingerScanner.scanValue
            .onEach { setScanner(Scanner.Finger) },
        )
        .filterNotNull()
        .shareIn(coroutineScope, SharingStarted.WhileSubscribed())


    @ExperimentalCoroutinesApi
    override val values = scansFlow.throttleFirst(300L)

    override suspend fun initialize(disabledByDefault: Boolean) {
        //casioScanner.initialize(disabledByDefault)
    }

    override suspend fun cleanup() {
      //  casioScanner.close()
    }

    override fun enable() {
     //   casioScanner.enable()
        enabled.value = true
        fingerScanner.enable()
    }

    override fun disable() {
     //   casioScanner.disable()
        enabled.value = false

        fingerScanner.disable()
    }


    private fun setScanner(scanner: Scanner) {
        _scanner = scanner
    }

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
        registerReceiver(receiver, IntentFilter(action), Context.RECEIVER_NOT_EXPORTED )
    } else {
        registerReceiver(receiver, IntentFilter(action), broadcastPermission, null, Context.RECEIVER_NOT_EXPORTED)
    }

    awaitClose { unregisterReceiver(receiver) }
}
