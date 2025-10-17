package com.example.stripesdemo.domain.utils

import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import com.example.stripesdemo.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext


@Singleton
class SensorFeedbackManager @Inject constructor(
    val settingsRepository: SettingsRepository,
    private val sensorFeedbackService: SensorFeedbackService
) {
    private val mediaFlowContext: CoroutineContext = CoroutineName("sensorFeedback") + Dispatchers.IO

    private val feedbackFlow = MutableSharedFlow<SensorFeedback>()

    @ExperimentalCoroutinesApi
    val feedbackPlayerFlow = settingsRepository.getSettingsFlow().flatMapLatest { settings ->
        feedbackFlow.throttleFirst(settings?.feedbackDelay?: THROTTLE_PERIOD)
            .flatMapLatest { sensorFeedback->
                sensorFeedbackService.sendFeedBack(sensorFeedback)
            }
    }

    init {
        feedbackPlayerFlow.launchIn(CoroutineScope(mediaFlowContext))
    }

    suspend fun sendFeedback(sensorFeedback: SensorFeedback) {
        feedbackFlow.emit(sensorFeedback)
    }


    companion object{
        private const val THROTTLE_PERIOD = 200L
    }
}
