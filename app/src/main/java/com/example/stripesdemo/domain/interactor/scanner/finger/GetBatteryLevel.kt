package com.example.stripesdemo.domain.interactor.scanner.finger


import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.FlowUseCase
import com.example.stripesdemo.domain.repository.ScannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class GetBatteryLevel @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): FlowUseCase<String, Unit>(dispatcher) {

    override fun invoke(params: Unit): Flow<String> {
        return scannerRepository.getFingerScannerBatteryLevel()
    }
}
