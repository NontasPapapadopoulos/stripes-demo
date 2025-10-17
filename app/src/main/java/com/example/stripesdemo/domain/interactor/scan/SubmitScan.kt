package com.example.stripesdemo.domain.interactor.scan

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import com.example.stripesdemo.domain.repository.ScanRepository
import com.example.stripesdemo.domain.repository.ScannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


open class SubmitScan @Inject constructor(
    private val scanRepository: ScanRepository,
    private val scannerRepository: ScannerRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<Unit, SubmitScan.Params>(dispatcher) {

    override suspend fun invoke(params: Params) {
        val openScan = scanRepository.getOpenScan()

        openScan?.let {
            val scanSource = scannerRepository.getScanSource()

            val updated = openScan.copy(
                barcode =  params.barcode,
                count = params.count,
                scanSource = scanSource
            )
            scanRepository.saveAndSubmit(updated)
            scannerRepository.sendFeedback(SensorFeedback.SUCCESS)
        }
    }

    data class Params(
        val barcode: String,
        val count: String
    )
}

