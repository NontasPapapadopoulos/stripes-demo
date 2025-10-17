package com.example.stripesdemo.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Settings
import com.example.stripesdemo.data.ScannerInterface
import dagger.hilt.android.qualifiers.ApplicationContext

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import com.example.stripesdemo.data.datasource.ScannerDataSource
import com.example.stripesdemo.data.mapper.toDomain
import net.stripesapp.mlsretailsoftware.data.scannerdevice.FingerScannerConnectionManager
import net.stripesapp.mlsretailsoftware.data.scannerdevice.FingerScannerSettingsManager
import net.stripesapp.mlsretailsoftware.data.scannerdevice.OpticonFeedbackManager
import net.stripesapp.mlsretailsoftware.domain.entity.enums.SensorFeedback
import net.stripesapp.mlsretailsoftware.domain.repository.ScannerRepository
import net.stripesapp.mlsretailsoftware.data.scannerdevice.mobile.MobileScannerInterface
import net.stripesapp.mlsretailsoftware.domain.entity.ConnectionStateDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.DeviceDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.enums.ScanSource
import net.stripesapp.mlsretailsoftware.domain.entity.enums.Scanner
import net.stripesapp.mlsretailsoftware.domain.utils.SensorFeedbackManager
import javax.inject.Inject

class ScannerDataRepository @Inject constructor(
    private val scannerInterface: ScannerInterface,
    private val sensorFeedbackManager: SensorFeedbackManager,
    private val opticonFeedbackManager: OpticonFeedbackManager,
    private val settingsManager: FingerScannerSettingsManager,
    private val connectionManager: FingerScannerConnectionManager,
    private val scannerDataSource: ScannerDataSource,
    @ApplicationContext val context: Context
): ScannerRepository {


    override fun getInput(): Flow<String> = scannerInterface.values


    override fun enabledState(): Flow<Boolean> = scannerInterface.enabled

    override suspend fun setEnabled(isEnabled: Boolean) {
        if (isEnabled)
            scannerInterface.enable()
        else
            scannerInterface.disable()
    }

    override fun getDeviceCode(): String {
        return deviceIdProvider.getId()
    }

    override suspend fun sendFeedback(sensorFeedback: SensorFeedback) {
        if (getDevice() == Scanner.Regular || getDevice() == Scanner.Mobile)
            sensorFeedbackManager.sendFeedback(sensorFeedback)
        else
            opticonFeedbackManager.sendFeedback(sensorFeedback)
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

    override fun setVolumeLevel(level: Int) {
        settingsManager.setVolume(level)
    }


    override fun getVolume(): Flow<Int> {
        return settingsManager.getVolume()
    }

    override fun getQrCodeFromOpticon(): Flow<String> {
        return connectionManager.getConnectionCode()
    }


    override suspend fun setConnectionCode(id: String) {
        connectionManager.setConnectionCode(id)
    }

    override suspend fun getConnectionCode(): String {
        return scannerDataSource.getConnectionCode()
    }

    override fun getConnectionState(): Flow<ConnectionStateDomainEntity> {
        return connectionManager.getConnectionState()
    }

    override fun getConnectedDevices(): Flow<List<DeviceDomainEntity>> {
        return connectionManager.getConnectedDevices()
            .map { device -> device.map { it.toDomain() }}
    }

    override fun getScanSource(): ScanSource {
        val device = getDevice()
        return when (device) {
            Scanner.Regular -> ScanSource.Casio
            Scanner.Finger -> ScanSource.Opticon
            Scanner.Mobile -> ScanSource.Mobile
        }
    }

    override suspend fun performMobileScan() {
        if (scannerInterface is MobileScannerInterface)
            scannerInterface.performMobileScan()
    }

}