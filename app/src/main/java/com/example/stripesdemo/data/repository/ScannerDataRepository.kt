package com.example.stripesdemo.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Settings
import com.example.stripesdemo.data.datasource.ScannerLocalDataSource
import com.example.stripesdemo.data.datasource.SettingsLocalDataSource
import com.example.stripesdemo.data.device.opticon.FingerScannerConnectionManager
import com.example.stripesdemo.data.device.opticon.FingerScannerSettingsManager
import com.example.stripesdemo.data.device.opticon.OpticonFeedbackManager
import com.example.stripesdemo.data.device.ScannerInterface
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.stripesdemo.data.mapper.toDomain
import com.example.stripesdemo.domain.entity.ConnectionStateDomainEntity
import com.example.stripesdemo.domain.entity.DeviceDomainEntity
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.domain.entity.enums.ScanSource
import com.example.stripesdemo.domain.entity.enums.Scanner
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import com.example.stripesdemo.domain.repository.ScannerRepository
import com.example.stripesdemo.domain.utils.SensorFeedbackManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
class ScannerDataRepository @Inject constructor(
    private val scannerInterface: ScannerInterface,
    private val sensorFeedbackManager: SensorFeedbackManager,
    private val settingsLocalDataSource: SettingsLocalDataSource,
    private val scannerLocalDataSource: ScannerLocalDataSource,
    @ApplicationContext val context: Context
): ScannerRepository {

    override fun getInput(): Flow<String> = scannerInterface.values

    override fun enabledState(): Flow<Boolean> = scannerInterface.enabled

    override suspend fun setEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            scannerInterface.enable()
        } else {
            scannerInterface.disable()
        }
    }

    override suspend fun sendFeedback(sensorFeedback: SensorFeedback) {
        if (getDevice() == Scanner.Regular)
            sensorFeedbackManager.sendFeedback(sensorFeedback)
        else
            scannerLocalDataSource.sendFeedback(sensorFeedback)
    }

    private fun getDevice() = scannerInterface.scanner


    override suspend fun getAutoTimeEnabled(): Boolean  {
        val isAutoTimeEnabled = Settings.Global.getInt(context.contentResolver, Settings.Global.AUTO_TIME) == 1
        val isAutoTimeZoneEnabled = Settings.Global.getInt(context.contentResolver, Settings.Global.AUTO_TIME_ZONE) == 1
        return isAutoTimeZoneEnabled && isAutoTimeEnabled
    }


    override fun getBatteryLevel(): Flow<Int> = callbackFlow {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val batteryPct = (level.toFloat() / scale.toFloat() * 100).toInt()
                    this@callbackFlow.trySend(batteryPct).isSuccess
                }
            }
        }

        context.registerReceiver(batteryReceiver, intentFilter)

        awaitClose {
            context.unregisterReceiver(batteryReceiver)
        }
    }

    override fun getFingerScannerBatteryLevel(): Flow<String> {
        return scannerLocalDataSource.getBatteryLevel()
    }

    override fun setVolumeLevel(level: Int) {
        scannerLocalDataSource.setVolume(level)
    }


    override fun getVolume(): Flow<String> {
        return scannerLocalDataSource.getVolumeLevel()
    }

    override fun getVibration(): Flow<String> {
        return scannerLocalDataSource.getVibrationLevel()
    }


    override fun getConnectionCode(): Flow<String> {
        return settingsLocalDataSource.getSettingsFlow()
            .map { it!!.connectionCode }
    }

    override fun getConnectionState(): Flow<ConnectionState> {
        return scannerLocalDataSource.getConnectionState()
    }


    override fun getScanSource(): ScanSource {
        val device = getDevice()
        return when (device) {
            Scanner.Regular -> ScanSource.RegularScanner
            Scanner.Finger -> ScanSource.FingerScanner
           // Scanner.Camera -> ScanSource.Camera
        }
    }

    override suspend fun initFingerScanner() {
        val connectionCode = settingsLocalDataSource.getSettings()!!.connectionCode
        scannerLocalDataSource.startScan(connectionCode)
    }

    override fun setVibration(level: Int) {
        scannerLocalDataSource.setVibration(level)
    }

    override fun getMacAddress(): Flow<String> {
        return scannerLocalDataSource.getMacAddress()
    }

    override fun disconnect() {
        scannerLocalDataSource.disconnect()
    }


    override fun stopBluetoothScan() {
        scannerLocalDataSource.stopScan()
    }

    override fun setDefaultSettings() {
        scannerLocalDataSource.setDefaultSettings()
    }



}