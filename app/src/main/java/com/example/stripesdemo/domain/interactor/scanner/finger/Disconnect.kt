package com.example.stripesdemo.domain.interactor.scanner.finger

import com.example.stripesdemo.data.BluetoothScannerService
import com.example.stripesdemo.data.Connect
import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import com.example.stripesdemo.domain.repository.ScannerRepository
import javax.inject.Inject

open class Disconnect @Inject constructor(
    private val scannerRepository: ScannerRepository,
    private val connect: Connect,
    private val bluetoothScannerService: BluetoothScannerService,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : SuspendUseCase<Unit, Unit>(flowDispatcher) {

    override suspend fun invoke(params: Unit) {
        bluetoothScannerService.stopScan()

    }

}