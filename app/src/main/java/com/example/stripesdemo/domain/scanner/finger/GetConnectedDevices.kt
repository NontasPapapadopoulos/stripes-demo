package net.stripesapp.mlsretailsoftware.domain.interactor.scanner.finger


import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import net.stripesapp.mlsretailsoftware.domain.entity.DeviceDomainEntity
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.FlowUseCase
import net.stripesapp.mlsretailsoftware.domain.interactor.SuspendUseCase
import net.stripesapp.mlsretailsoftware.domain.repository.ScannerRepository
import javax.inject.Inject
import kotlin.random.Random

open class GetConnectedDevices @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): FlowUseCase<List<DeviceDomainEntity>, Unit>(dispatcher) {


    override fun invoke(params: Unit):Flow<List<DeviceDomainEntity>> {
        return scannerRepository.getConnectedDevices()
    }

}
