package com.example.stripesdemo.data.device

import android.content.Context
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

const val OPTICON_BROADCAST_ACTION = "com.opticon.decode.wedge.action"
const val OPTICON_BROADCAST_PERMISSION =
    "com.opticon.opticonnect.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION"
const val OPTICON_INTENT_EXTRA_BARCODE = "com.opticon.decode.intentwedge.barcode_data"


class FingerScanner @Inject constructor(
    opticonFeedbackManager: OpticonFeedbackManager,
    @ApplicationContext val context: Context
) {

    private val isEnabled = MutableStateFlow(false)

    val inputFlow =
        context.broadcastReceiverFlow(OPTICON_BROADCAST_ACTION, OPTICON_BROADCAST_PERMISSION)
            .map { it.getStringExtra(OPTICON_INTENT_EXTRA_BARCODE) }
            .onEach {
                if (!isEnabled.value)
                    opticonFeedbackManager.sendFeedback(SensorFeedback.ERROR)
            }

    fun setEnabled(enabled: Boolean) {
        isEnabled.value = enabled
    }

}