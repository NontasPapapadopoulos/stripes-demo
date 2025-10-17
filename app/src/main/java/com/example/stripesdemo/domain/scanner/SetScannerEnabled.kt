package net.stripesapp.mlsretailsoftware.domain.interactor.scanner

import kotlinx.coroutines.CoroutineDispatcher
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.SuspendUseCase
import net.stripesapp.mlsretailsoftware.domain.repository.ScannerRepository
import javax.inject.Inject

open class SetScannerEnabled @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : SuspendUseCase<Unit, SetScannerEnabled.Params>(flowDispatcher) {

    override suspend fun invoke(params: Params) {
        scannerRepository.setEnabled(params.isEnabled)
    }

    data class Params(val isEnabled: Boolean)
}