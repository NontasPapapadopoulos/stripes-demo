package com.example.stripesdemo.data.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.stripesdemo.domain.entity.enums.Scanner
import com.example.stripesdemo.domain.repository.SettingsRepository
import com.example.stripesdemo.domain.utils.throttleFirst
import dagger.hilt.android.qualifiers.ApplicationContext
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
import javax.inject.Inject


private const val CASIO_BROADCAST_ACTION = "device.common.USERMSG"


class CasioScanner @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val scannerLibrary = ScannerLibrary()


    val scansFlow = context.broadcastReceiverFlow(CASIO_BROADCAST_ACTION)
            .map { scannerLibrary.getScanResultSafe() }


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


