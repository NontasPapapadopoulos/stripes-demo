package net.stripesapp.mlsretailsoftware.domain.interactor.scanner.finger

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import net.stripesapp.mlsretailsoftware.domain.entity.ConnectionStateDomainEntity
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.FlowUseCase
import net.stripesapp.mlsretailsoftware.domain.repository.ScannerRepository
import javax.inject.Inject

open class GetConnectionState @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
): FlowUseCase<ConnectionStateDomainEntity?, Unit>(flowDispatcher) {

    override fun invoke(params: Unit): Flow<ConnectionStateDomainEntity?> {
        return scannerRepository.getConnectionState()
    }
}