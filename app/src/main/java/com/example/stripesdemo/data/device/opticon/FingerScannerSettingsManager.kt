package com.example.stripesdemo.data.device.opticon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
private const val OPTICON_CONFIGURATION_FILE_LOCATION = "Android/data/com.opticon.opticonnect/files/"

private const val VOLUME = "volume"
private const val SET_VOLUME = "com.opticon.opticonnect.set_volume"
private const val REQUEST_VOLUME = "com.opticon.opticonnect.request_volume"
private const val LISTEN_VOLUME = "com.opticon.opticonnect.listen_volume"




class FingerScannerSettingsManager @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun setVolume(volume: Int) {
        val setVolumeIntent = Intent(SET_VOLUME).apply {
            putExtra(VOLUME, volume)
        }
        context.sendBroadcast(setVolumeIntent)
    }

    fun getVolume(): Flow<Int> = callbackFlow  {
        val volumeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val volume = intent?.getIntExtra(VOLUME, -1)
                if (volume != null && volume >= 0) {
                    this@callbackFlow.trySend(volume).isSuccess
                }
            }
        }

        registerReceiver(
            receiver = volumeReceiver,
            intentValue = LISTEN_VOLUME
        )

        requestVolume()

        awaitClose {
            context.unregisterReceiver(volumeReceiver)
        }

    }


    private fun requestVolume() {
        val requestVolumeIntent = Intent(REQUEST_VOLUME)
        context.sendBroadcast(requestVolumeIntent)
    }


    private fun registerReceiver(
        receiver: BroadcastReceiver,
        intentValue: String
    ) {
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(intentValue),
            ContextCompat.RECEIVER_EXPORTED
        )
    }


}




