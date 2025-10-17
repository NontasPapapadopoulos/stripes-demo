package com.example.stripesdemo.domain.interactor.scan

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import com.example.stripesdemo.domain.repository.ScanRepository
import javax.inject.Inject

open class InitOpenScan @Inject constructor(
    private val scanRepository: ScanRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<Unit, Unit>(dispatcher) {

    override suspend fun invoke(params: Unit) {
        scanRepository.initOpenScan()
    }

}