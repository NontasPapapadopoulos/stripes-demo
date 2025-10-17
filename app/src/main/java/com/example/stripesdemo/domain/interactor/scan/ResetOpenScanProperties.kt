package com.example.stripesdemo.domain.interactor.scan

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.entity.ScanDomainEntity
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import com.example.stripesdemo.domain.repository.ScanRepository
import javax.inject.Inject

open class ResetOpenScanProperties @Inject constructor(
    private val scanRepository: ScanRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<ScanDomainEntity,Unit>(dispatcher) {

    override suspend fun invoke(params: Unit): ScanDomainEntity {
        return scanRepository.resetOpenScan()
    }

}