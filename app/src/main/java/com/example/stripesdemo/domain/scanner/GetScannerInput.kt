package net.stripesapp.mlsretailsoftware.domain.interactor.scanner

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.FlowUseCase
import net.stripesapp.mlsretailsoftware.domain.repository.ScannerRepository
import javax.inject.Inject

open class GetScannerInput @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : FlowUseCase<String, Unit>(flowDispatcher) {

    override fun invoke(params: Unit): Flow<String> {
        return scannerRepository.getInput().map { it.trim() }
            .onStart { scannerRepository.setEnabled(true) }
            .onCompletion { scannerRepository.setEnabled(false) }
    }
}