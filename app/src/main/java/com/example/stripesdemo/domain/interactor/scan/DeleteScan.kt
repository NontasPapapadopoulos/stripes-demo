package com.example.stripesdemo.domain.interactor.scan

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.entity.ScanDomainEntity
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import com.example.stripesdemo.domain.repository.ScanRepository
import com.example.stripesdemo.domain.repository.ScannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


open class DeleteScan @Inject constructor(
    private val scanRepository: ScanRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<Unit, DeleteScan.Params>(dispatcher) {

    override suspend fun invoke(params: Params) {
        scanRepository.deleteScan(params.scan)
    }

    data class Params(
        val scan: ScanDomainEntity
    )
}

