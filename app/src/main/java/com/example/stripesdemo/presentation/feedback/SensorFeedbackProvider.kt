package com.example.stripesdemo.presentation.feedback

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import com.example.stripesdemo.domain.utils.SensorFeedbackService
import com.example.stripesdemo.presentation.application
import com.google.android.datatransport.BuildConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SensorFeedbackProvider @Inject constructor(): SensorFeedbackService {


    private val vibrator = application.getVibrator()

    init {
        if (!BuildConfig.DEBUG) {
            (application.getSystemService(Context.AUDIO_SERVICE) as AudioManager?)?.let {
                it.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    it.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0
                )
            }
        }
    }

    override fun sendFeedBack(sensorFeedback: SensorFeedback): Flow<SensorFeedback> {
        return callbackFlow {
            val mediaPlayer = MediaPlayer.create(application, sensorFeedback.audioRes())
            mediaPlayer.start()
            vibrator.vibrate(sensorFeedback)

            mediaPlayer.setOnCompletionListener { trySend(sensorFeedback) }
            awaitClose { mediaPlayer.release() }
        }
    }


}
