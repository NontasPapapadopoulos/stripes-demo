package com.example.stripesdemo.domain.interactor.scanner

import com.example.stripesdemo.data.BluetoothScanner
import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import com.example.stripesdemo.domain.repository.SettingsRepository
import javax.inject.Inject

open class InitBluetoothScanner @Inject constructor(
    private val bluetoothScanner: BluetoothScanner,
    private val settingsRepository: SettingsRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : SuspendUseCase<Unit, Unit>(flowDispatcher) {

    override suspend fun invoke(params: Unit) {
        val uuid = settingsRepository.getSettings()!!
            .connectionUUID
        bluetoothScanner.startScan(uuid)
    }



}