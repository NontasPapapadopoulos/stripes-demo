package com.example.stripesdemo.data.repository

import android.content.Context
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
import com.example.stripesdemo.domain.entity.enums.ScanSource
import com.example.stripesdemo.domain.entity.enums.Scanner
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import com.example.stripesdemo.domain.repository.ScannerRepository
import com.example.stripesdemo.domain.utils.SensorFeedbackManager
import javax.inject.Inject

class ScannerDataRepository @Inject constructor(
    private val scannerInterface: ScannerInterface,
    private val sensorFeedbackManager: SensorFeedbackManager,
    private val opticonFeedbackManager: OpticonFeedbackManager,
    private val settingsManager: FingerScannerSettingsManager,
    private val connectionManager: FingerScannerConnectionManager,
//    private val scannerDataSource: ScannerDataSource,
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



    override suspend fun sendFeedback(sensorFeedback: SensorFeedback) {
//        if (getDevice() == Scanner.Regular || getDevice() == Scanner.Camera)
            sensorFeedbackManager.sendFeedback(sensorFeedback)
//        else
//            opticonFeedbackManager.sendFeedback(sensorFeedback)
    }

    private fun getDevice() = scannerInterface.scanner


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
            Scanner.Regular -> ScanSource.RegularScanner
            Scanner.Finger -> ScanSource.FingerScanner
            Scanner.Camera -> ScanSource.Camera
        }
    }

    override suspend fun performCameraScan() {
        scannerInterface.performCameraScan()
    }

}