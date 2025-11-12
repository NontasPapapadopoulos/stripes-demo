package com.example.stripesdemo.domain.interactor.scanner

import com.example.stripesdemo.data.Bluetooth
import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import com.example.stripesdemo.domain.repository.ScannerRepository
import com.example.stripesdemo.domain.repository.SettingsRepository
import javax.inject.Inject

open class InitBluetoothScanner @Inject constructor(
    private val bluetooth: Bluetooth,
    private val settingsRepository: SettingsRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : SuspendUseCase<Unit, InitBluetoothScanner.Params>(flowDispatcher) {

    override suspend fun invoke(params: Params) {
        val uuid = settingsRepository.getSettings()
            ?.connectionUUID

        bluetooth.startScan(uuid)
    }


    data class Params(val uuid: String)

}