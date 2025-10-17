package com.example.stripesdemo.domain.interactor.scanner.finger

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import com.example.stripesdemo.domain.repository.ScannerRepository
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