package com.example.stripesdemo.domain.interactor.scanner

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import com.example.stripesdemo.domain.repository.ScannerRepository
import javax.inject.Inject

open class SendSensorFeedback @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : SuspendUseCase<Unit, SendSensorFeedback.Params>(flowDispatcher) {

    override suspend fun invoke(params: Params) {
        scannerRepository.sendFeedback(params.sensorFeedback)
    }

    data class Params(val sensorFeedback: SensorFeedback)
}