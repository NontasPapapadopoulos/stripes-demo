package com.example.stripesdemo.domain.repository

import com.example.stripesdemo.domain.entity.ConnectionStateDomainEntity
import com.example.stripesdemo.domain.entity.DeviceDomainEntity
import com.example.stripesdemo.domain.entity.enums.ScanSource
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import kotlinx.coroutines.flow.Flow

interface ScannerRepository {

    fun getInput(): Flow<String>

    fun enabledState(): Flow<Boolean>

    suspend fun setEnabled(isEnabled: Boolean)

    suspend fun sendFeedback(sensorFeedback: SensorFeedback)

    fun setVolumeLevel(level: Int)

    fun getVolume(): Flow<Int>

    fun getQrCodeFromOpticon(): Flow<String>

//    suspend fun setConnectionCode(id: String)
//
//    suspend fun getConnectionCode(): String

    fun getConnectionState(): Flow<ConnectionStateDomainEntity>
//
    fun getConnectedDevices(): Flow<List<DeviceDomainEntity>>

    suspend fun performMobileScan()

    fun getScanSource(): ScanSource

}