package com.example.stripesdemo.domain.interactor.scanner.finger

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import com.example.stripesdemo.domain.repository.ScannerRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

open class SetVibration @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : SuspendUseCase<Unit, SetVibration.Params>(flowDispatcher) {

    override suspend fun invoke(params: Params) {
        scannerRepository.setVibration(params.level)

    }

    data class Params(val level: Int)
}