package net.stripesapp.mlsretailsoftware.domain.interactor.scanner.finger

import kotlinx.coroutines.CoroutineDispatcher
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.SuspendUseCase
import net.stripesapp.mlsretailsoftware.domain.repository.ScannerRepository
import javax.inject.Inject

open class SetVolume @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : SuspendUseCase<Unit, SetVolume.Params>(flowDispatcher) {

    override suspend fun invoke(params: Params) {
        scannerRepository.setVolumeLevel(params.level)

    }

    data class Params(val level: Int)
}