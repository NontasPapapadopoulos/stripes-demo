package com.example.stripesdemo.domain.utils

import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import kotlinx.coroutines.flow.Flow

interface SensorFeedbackService {
   // suspend fun sendFeedback(sensorFeedback: SensorFeedback)
    fun sendFeedBack(sensorFeedback: SensorFeedback): Flow<SensorFeedback>
}