package net.stripesapp.mlsretailsoftware.domain.interactor.scanner

import kotlinx.coroutines.CoroutineDispatcher
import net.stripesapp.mlsretailsoftware.domain.entity.enums.SensorFeedback
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.SuspendUseCase
import net.stripesapp.mlsretailsoftware.domain.repository.ScannerRepository
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