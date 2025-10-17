package com.example.stripesdemo.domain.interactor.scanner.finger

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.entity.ConnectionStateDomainEntity
import com.example.stripesdemo.domain.interactor.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import com.example.stripesdemo.domain.repository.ScannerRepository
import javax.inject.Inject

open class GetConnectionState @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
): FlowUseCase<ConnectionStateDomainEntity?, Unit>(flowDispatcher) {

    override fun invoke(params: Unit): Flow<ConnectionStateDomainEntity?> {
        return scannerRepository.getConnectionState()
    }
}