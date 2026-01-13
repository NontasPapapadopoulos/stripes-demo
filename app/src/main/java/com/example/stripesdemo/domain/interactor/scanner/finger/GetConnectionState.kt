package com.example.stripesdemo.domain.interactor.scanner.finger

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.domain.interactor.FlowUseCase
import com.example.stripesdemo.domain.repository.ScannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class GetConnectionState @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
): FlowUseCase<ConnectionState, Unit>(flowDispatcher) {

    override fun invoke(params: Unit): Flow<ConnectionState> {
        return scannerRepository.getConnectionState()
    }
}