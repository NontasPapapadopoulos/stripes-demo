package com.example.stripesdemo.domain.interactor.scanner

import com.example.stripesdemo.domain.interactor.FlowUseCase
import com.example.stripesdemo.domain.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import com.example.stripesdemo.domain.repository.ScannerRepository
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