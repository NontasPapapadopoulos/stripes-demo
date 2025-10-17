package net.stripesapp.mlsretailsoftware.domain.interactor.scanner


import kotlinx.coroutines.CoroutineDispatcher
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.SuspendUseCase
import net.stripesapp.mlsretailsoftware.domain.repository.ScannerRepository
import javax.inject.Inject

open class GetAutoTimeEnabledStatus @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): SuspendUseCase<Boolean, Unit>(dispatcher) {
    override suspend fun invoke(params: Unit): Boolean {
        return scannerRepository.getAutoTimeEnabled()
    }


}
