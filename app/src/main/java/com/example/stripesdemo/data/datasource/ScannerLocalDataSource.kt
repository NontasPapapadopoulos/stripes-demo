package com.example.stripesdemo.data.datasource


import com.example.stripesdemo.data.BluetoothScannerService
import com.example.stripesdemo.data.device.GeneralScanLibrary
import com.example.stripesdemo.data.device.GeneralScanUtils.DISABLE_DECODING_PROMPT_TONE
import com.example.stripesdemo.data.device.GeneralScanUtils.SET_VIBRATION_LEVEL_COMMAND
import com.example.stripesdemo.data.device.GeneralScanUtils.SET_VOLUME_LEVEL_COMMAND
import com.example.stripesdemo.data.device.GeneralScanUtils.getPickListModeCommand
import com.example.stripesdemo.data.device.GeneralScanUtils.setValue
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.domain.entity.enums.PickListMode
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface ScannerLocalDataSource {
    suspend fun startScan(connectionCode: String)
    fun disconnect()
    fun stopScan()
    fun getBatteryLevel(): Flow<String>
    fun getConnectionState(): Flow<ConnectionState>
    fun getVibrationLevel(): Flow<String>
    fun getVolumeLevel(): Flow<String>
    fun setVolume(volume: Int)
    fun setVibration(vibration: Int)
    fun getMacAddress(): Flow<String>
    suspend fun sendFeedback(sensorFeedback: SensorFeedback)
    fun setDefaultSettings()
}

class ScannerLocalDataSourceImpl @Inject constructor(
    private val bluetoothScannerService: BluetoothScannerService,
    private val generalScanLibrary: GeneralScanLibrary
): ScannerLocalDataSource {

    override suspend fun startScan(connectionCode: String) {
        bluetoothScannerService.startScan(connectionCode)
    }

    override fun stopScan() {
        bluetoothScannerService.stopScan()
    }

    override fun disconnect() {
        generalScanLibrary.disconnect()
        stopScan()
    }

    override fun getConnectionState(): Flow<ConnectionState> {
        return generalScanLibrary.connectionState
    }

    override fun getMacAddress(): Flow<String> {
        return generalScanLibrary.macAddress
    }


    override fun getBatteryLevel(): Flow<String> {
        return generalScanLibrary.observeBattery()
    }

    override fun getVibrationLevel(): Flow<String> {
        return generalScanLibrary.observeVibration()
    }

    override fun getVolumeLevel(): Flow<String> {
        return generalScanLibrary.observeVolume()
    }

    override fun setVolume(volume: Int) {
        generalScanLibrary.addCommand(SET_VOLUME_LEVEL_COMMAND.setValue("$volume"))
        //generalScanLibrary.addCommand(BUZZER_TEST_COMMAND)
    }

    override fun setVibration(vibration: Int) {
        generalScanLibrary.addCommand(SET_VIBRATION_LEVEL_COMMAND.setValue("$vibration"))
    }

    override suspend fun sendFeedback(sensorFeedback: SensorFeedback) {
        return generalScanLibrary.sendFeedback(sensorFeedback)
    }

    override fun setDefaultSettings() {
        generalScanLibrary.addCommand(DISABLE_DECODING_PROMPT_TONE)
        generalScanLibrary.addCommand(getPickListModeCommand(PickListMode.AimingDecoding))
    }

}