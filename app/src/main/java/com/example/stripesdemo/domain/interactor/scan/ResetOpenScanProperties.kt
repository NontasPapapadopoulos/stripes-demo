package com.example.stripesdemo.domain.interactor.scan

import kotlinx.coroutines.CoroutineDispatcher
import net.stripesapp.mlsretailsoftware.domain.entity.ScanDomainEntity
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.SuspendUseCase
import com.example.stripesdemo.domain.repository.ScanRepository
import net.stripesapp.mlsretailsoftware.domain.repository.SessionRepository
import javax.inject.Inject

open class ResetOpenScanProperties @Inject constructor(
    private val scanRepository: ScanRepository,
    private val sessionRepository: SessionRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<ScanDomainEntity, ResetOpenScanProperties.Params>(dispatcher) {

    override suspend fun invoke(params: Params): ScanDomainEntity {
        val session = sessionRepository.getAddScansSession(params.zoneCheckInId)!!
        return scanRepository.resetOpenScan(session.id, params.selectedField)
    }

    data class Params(val zoneCheckInId: String, val selectedField: String?)
}