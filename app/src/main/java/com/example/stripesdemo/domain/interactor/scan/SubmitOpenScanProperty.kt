package com.example.stripesdemo.domain.interactor.scan

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.entity.ScanDomainEntity
import com.example.stripesdemo.domain.entity.enums.ScanField
import com.example.stripesdemo.domain.entity.enums.ScanSource
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import com.example.stripesdemo.domain.repository.ScanRepository
import com.example.stripesdemo.domain.repository.ScannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


open class SubmitOpenScanProperty @Inject constructor(
    private val scanRepository: ScanRepository,
    private val scannerRepository: ScannerRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<ScanDomainEntity?, SubmitOpenScanProperty.Params>(dispatcher) {

    override suspend fun invoke(params: Params): ScanDomainEntity? {
        val openScan = scanRepository.getOpenScan()

        val properties = mapOf(params.field.dbField to params.value)

        return openScan?.let {

            val scanSource = getScanSource(params.field, openScan.scanSource)

            val updated = openScan.copy(
                properties =  properties,
                scanSource = scanSource
            )

            scanRepository.save(updated)
            scannerRepository.sendFeedback(SensorFeedback.SUCCESS)

            updated
        }
    }


    private fun getScanSource(
        field: ScanField,
        existingScanSource: ScanSource?
        ): ScanSource? {
        return if (field == ScanField.Barcode)
            scannerRepository.getScanSource()
        else existingScanSource
    }

    data class Params(
        val value: String,
        val field: ScanField
    )
}

