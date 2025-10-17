package com.example.stripesdemo.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import net.stripesapp.mlsretailsoftware.data.MobileScanner
import net.stripesapp.mlsretailsoftware.data.scannerdevice.FingerScanner
import net.stripesapp.mlsretailsoftware.data.scannerdevice.ScannerInterface

import net.stripesapp.mlsretailsoftware.domain.entity.enums.Scanner
import net.stripesapp.mlsretailsoftware.domain.repository.SettingsRepository
import net.stripesapp.mlsretailsoftware.domain.utils.throttleFirst


class MobileScannerInterface(
    val context: Context,
    settingsRepository: SettingsRepository,
    coroutineScope: CoroutineScope,
    private val fingerScanner: FingerScanner
) : ScannerInterface {

    private val mobileScanner = MobileScanner(context)

    override val enabled = MutableStateFlow(false)

    private var _scanner: Scanner = Scanner.Finger
    override val scanner: Scanner
        get() = _scanner


    private val scansFlow = merge(
        fingerScanner.inputFlow
            .onEach { setScanner(Scanner.Finger) },
        mobileScanner.scanFlow
            .onEach { setScanner(Scanner.Mobile) }
    )
        .filterNotNull()
        .shareIn(coroutineScope, SharingStarted.WhileSubscribed())


    @ExperimentalCoroutinesApi
    override val values = settingsRepository.getSettingsFlow().flatMapLatest { settings ->
        scansFlow.throttleFirst(settings?.scansDelay ?: 200L)
    }

    override suspend fun initialize(disabledByDefault: Boolean) {
        enabled.value = false
    }

    override suspend fun cleanup() {
        enabled.value = false
    }


    override fun enable() {
        enabled.value = true
        fingerScanner.setEnabled(true)
    }

    override fun disable() {
        enabled.value = false
        fingerScanner.setEnabled(false)
    }


    suspend fun performMobileScan() {
        mobileScanner.scan()
    }

    private fun setScanner(scanner: Scanner) {
        _scanner = scanner
    }
}