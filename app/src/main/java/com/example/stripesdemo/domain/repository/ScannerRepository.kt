package com.example.stripesdemo.domain.repository

import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.domain.entity.enums.ScanSource
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import kotlinx.coroutines.flow.Flow

interface ScannerRepository {

    fun getInput(): Flow<String>

    fun enabledState(): Flow<Boolean>

    suspend fun setEnabled(isEnabled: Boolean)

    suspend fun sendFeedback(sensorFeedback: SensorFeedback)

    suspend fun getAutoTimeEnabled(): Boolean

    fun getBatteryLevel(): Flow<Int>

    fun getFingerScannerBatteryLevel(): Flow<String>

    fun setVolumeLevel(level: Int)

    fun getVolume(): Flow<String>
    fun getVibration(): Flow<String>

    fun getConnectionCode(): Flow<String>

    fun getConnectionState(): Flow<ConnectionState>

    fun getScanSource(): ScanSource

    suspend fun initFingerScanner()

    fun setVibration(level: Int)

    fun getMacAddress(): Flow<String>

    fun disconnect()

    fun stopBluetoothScan()

    fun setDefaultSettings()

    suspend fun performCameraScan()
}