package com.example.stripesdemo.domain.interactor.scanner.finger

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import com.example.stripesdemo.domain.repository.ScannerRepository
import com.example.stripesdemo.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

open class StartBluetoothScanner @Inject constructor(
    private val scannerRepository: ScannerRepository,
    private val settingsRepository: SettingsRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : SuspendUseCase<Unit, Unit>(flowDispatcher) {

    override suspend fun invoke(params: Unit) {
        setConnectionCodeIfNotExist()
        scannerRepository.initFingerScanner()
    }


    private suspend fun setConnectionCodeIfNotExist() {
        val settings = settingsRepository.getSettings()!!
        val hasConnectionCode = settings
            .connectionCode.isNotEmpty()

        if (hasConnectionCode) return

        setNewConnectionCode()

    }

    private suspend fun setNewConnectionCode() {
        settingsRepository.changeConnectionCode(generateConnectionCode())
    }

    private fun generateConnectionCode(): String {
        val randomNumStr = ((0..0xffff).random()).toString(16).padStart(4,'0').uppercase()
        return "0000${randomNumStr}-0000-1000-8000-00805F9B34FB"
    }
}