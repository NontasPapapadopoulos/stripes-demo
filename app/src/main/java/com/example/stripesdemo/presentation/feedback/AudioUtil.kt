package com.example.stripesdemo.presentation.feedback

import com.example.presentation.R
import com.example.stripesdemo.domain.entity.enums.SensorFeedback


fun SensorFeedback.audioRes(): Int = when (this) {
    SensorFeedback.SUCCESS -> R.raw.scan_success_3
    SensorFeedback.ERROR, SensorFeedback.ERROR_NO_VIBRATION -> R.raw.rattle
    SensorFeedback.WARNING, SensorFeedback.WARNING_NO_VIBRATION, SensorFeedback.NOTIFICATION -> R.raw.notification
    SensorFeedback.SYNC_ERROR -> R.raw.alert
    SensorFeedback.KEY_PRESS -> R.raw.keypress
}

