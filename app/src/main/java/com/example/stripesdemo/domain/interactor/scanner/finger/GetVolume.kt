package com.example.stripesdemo.domain.interactor.scanner.finger

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.FlowUseCase
import com.example.stripesdemo.domain.repository.ScannerRepository
import javax.inject.Inject

open class GetVolume @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
): FlowUseCase<Int, Unit>(flowDispatcher) {

    override fun invoke(params: Unit): Flow<Int> {
        return scannerRepository.getVolume()
    }
}