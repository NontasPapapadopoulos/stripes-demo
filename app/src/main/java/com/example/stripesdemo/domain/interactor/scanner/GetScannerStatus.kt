package com.example.stripesdemo.domain.interactor.scanner

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.FlowUseCase
import com.example.stripesdemo.domain.repository.ScannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

open class GetScannerStatus @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : FlowUseCase<Boolean, Unit>(flowDispatcher) {

    override fun invoke(params: Unit): Flow<Boolean> {
        return scannerRepository.enabledState()
    }
}