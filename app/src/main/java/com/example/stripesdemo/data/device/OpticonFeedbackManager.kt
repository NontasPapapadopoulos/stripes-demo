package com.example.stripesdemo.data.device

import android.content.Context
import android.content.Intent
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

private const val FEEDBACK = "feedback"
private const val RESPONSES = "responses"
private const val TYPE = "type"
private const val RESPONSE_TYPE = "responsetype"
private const val BAD_READ = "badread"
private const val GOOD_READ = "goodread"
private const val SIMPLE_LED = "simpleled"
private const val COLOR = "color"
private const val RED = "#FF0000"
private const val SIMPLE_SOUND = "simplesound"
private const val SOUND_TYPE = "soundtype"
private const val ERROR_SOUND = "7"
private const val GOOD_READ_SOUND = "A"
const val OPTICON_FEEDBACK_ACTION = "com.opticon.opticonnect.feedback"
class OpticonFeedbackManager @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun sendFeedback(sensorFeedback: SensorFeedback) {
        val isGoodRead = sensorFeedback == SensorFeedback.SUCCESS

        val feedback = createOpticonFeedback(isGoodRead)
        val intent = Intent(OPTICON_FEEDBACK_ACTION).apply {
            putExtra(FEEDBACK, feedback.toString())
        }
        context.sendBroadcast(intent)
    }

    private fun createOpticonFeedback(isGoodRead: Boolean) = JSONObject().apply{
        put(RESPONSE_TYPE, if (isGoodRead) GOOD_READ else BAD_READ)
        put(RESPONSES, createOpticonResponses(isGoodRead))
    }

    private fun createOpticonResponses(isGoodRead: Boolean) = JSONArray().apply {
        put(
            JSONObject().apply {
                put(TYPE, SIMPLE_SOUND)
                put(SOUND_TYPE, if (isGoodRead) GOOD_READ_SOUND else ERROR_SOUND)
            }
        )
        if (!isGoodRead) {
            put(
                JSONObject().apply {
                    put(TYPE, SIMPLE_LED)
                    put(COLOR, RED)
                }
            )
        }
    }
}