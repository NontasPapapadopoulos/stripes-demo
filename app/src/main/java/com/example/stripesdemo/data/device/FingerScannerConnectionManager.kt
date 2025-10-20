package com.example.stripesdemo.data.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.example.stripesdemo.data.entity.DeviceDataEntity
import com.example.stripesdemo.domain.entity.ConnectionStateDomainEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
private const val SET_CONNECTION_ID = "com.opticon.opticonnect.set_conn_id"
private const val CONNECTION_POOL_ID = "connection_pool_id"
private const val LISTEN_CONNECTION_ID = "com.opticon.opticonnect.listen_conn_id"
private const val CONNECTION_POOL_QR_DATA = "connection_pool_qr_data"
private const val REQUEST_CONNECTION_ID = "com.opticon.opticonnect.request_conn_id"

private const val REQUEST_CONNECTIONS = "com.opticon.opticonnect.request_connections"
private const val LISTEN_CONNECTIONS = "com.opticon.opticonnect.listen_connections"

private const val DEVICES = "devices"
private const val DEVICE_ID = "device_id"
private const val DEVICE_NAME = "device_name"


private const val LISTEN_CONNECTION_STATE = "com.opticon.opticonnect.listen_conn_state"
private const val REQUEST_CONNECTION_STATE = "com.opticon.opticonnect.request_connection_state"
private const val CONNECTION_STATE = "connection_state"

class FingerScannerConnectionManager @Inject constructor(
    @ApplicationContext val context: Context
) {

    fun setConnectionCode(hexId: String) {
        val setHexIdIntent = Intent(SET_CONNECTION_ID).apply {
            putExtra(CONNECTION_POOL_ID, hexId)
        }
        context.sendBroadcast(setHexIdIntent)
    }


    fun getConnectionCode(): Flow<String> = callbackFlow {
        val qrDataReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val qrCode = intent?.getStringExtra(CONNECTION_POOL_QR_DATA)
                qrCode?.let {
                    this@callbackFlow.trySend(qrCode).isSuccess
                }
            }
        }

        registerReceiver(qrDataReceiver, LISTEN_CONNECTION_ID)

        requestConnectionCode()

        awaitClose {
            context.unregisterReceiver(qrDataReceiver)
        }

    }

    private fun requestConnectionCode() {
        val intent = Intent(REQUEST_CONNECTION_ID)
        context.sendBroadcast(intent)
    }



    fun getConnectedDevices(): Flow<List<DeviceDataEntity>> = callbackFlow {
        val connectedDevicesReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val devicesJson = intent?.getStringExtra(DEVICES)
                if (devicesJson != null) {
                    val devices = getDevices(devicesJson)
                    this@callbackFlow.trySend(devices).isSuccess
                }
            }
        }

        registerReceiver(connectedDevicesReceiver, LISTEN_CONNECTIONS)

        requestConnectedDevices()

        awaitClose {
            context.unregisterReceiver(connectedDevicesReceiver)
        }
    }


    private fun requestConnectedDevices() {
        val requestIntent = Intent(REQUEST_CONNECTIONS)
        context.sendBroadcast(requestIntent)
    }


    fun getConnectionState(): Flow<ConnectionStateDomainEntity> = callbackFlow {
        val connectionStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val deviceId = intent?.getStringExtra(DEVICE_NAME)
                val state = intent?.getStringExtra(CONNECTION_STATE)
                val deviceName = intent?.getStringExtra(DEVICE_NAME)

                if (deviceId != null && state != null) {
                    val connectionState =
                        ConnectionStateDomainEntity(deviceId, state, deviceName)
                    this@callbackFlow.trySend(connectionState).isSuccess
                }
            }
        }

        registerReceiver(connectionStateReceiver, LISTEN_CONNECTION_STATE)

        requestConnectionState()

        awaitClose {
            context.unregisterReceiver(connectionStateReceiver)
        }
    }


    private fun requestConnectionState() {
        val intent = Intent(REQUEST_CONNECTION_STATE)
        context.sendBroadcast(intent)
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


    private fun getDevices(jsonString: String): List<DeviceDataEntity> {
        val jsonArray = JSONArray(jsonString)

        return  (0 until jsonArray.length()).map {
            val jsonObject: JSONObject = jsonArray.getJSONObject(it)
            DeviceDataEntity(
                id = jsonObject.getString(DEVICE_ID),
                name = jsonObject.getString(DEVICE_NAME)
            )

        }
    }
}