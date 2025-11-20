package com.example.stripesdemo.domain.interactor

import com.example.stripesdemo.data.BluetoothScannerService
import com.example.stripesdemo.domain.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.stripesdemo.domain.repository.SettingsRepository
import javax.inject.Inject

open class GetConnectionUUID @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val bluetoothScanner: BluetoothScannerService,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : FlowUseCase<String, Unit>(flowDispatcher) {

    override fun invoke(params: Unit): Flow<String> {
        return  flow {
            val uuid = settingsRepository.getSettings()!!.connectionUUID
            bluetoothScanner.startScan(uuid)
            emit("{G6000/${uuid}}")
        }

    }

}