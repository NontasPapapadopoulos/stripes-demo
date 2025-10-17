package com.example.stripesdemo.presentation.feedback

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import com.example.stripesdemo.domain.entity.enums.SensorFeedback


@RequiresApi(Build.VERSION_CODES.O)
fun Vibrator.vibrate(feedback: SensorFeedback) = when (feedback) {
    SensorFeedback.ERROR, SensorFeedback.SYNC_ERROR -> doubleVibration()
    SensorFeedback.WARNING, SensorFeedback.NOTIFICATION -> singleVibration()
    SensorFeedback.SUCCESS -> singleVibration(milliseconds = 50L)
    else -> {}
}

fun Context.getVibrator(): Vibrator {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun Vibrator.singleVibration(milliseconds: Long = 250) {
    vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
}

@RequiresApi(Build.VERSION_CODES.O)
fun Vibrator.doubleVibration() {
    vibrate(
        VibrationEffect.createWaveform(
            longArrayOf(40, 200, 40, 0, 40, 200),
            VibrationEffect.DEFAULT_AMPLITUDE
        )
    )
}