package com.example.stripesdemo.data.device

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.stripesdemo.data.device.GeneralScanUtils.ALERT_SOUND
import com.example.stripesdemo.data.device.GeneralScanUtils.ENABLE_DECODING_PROMPT_TONE
import com.example.stripesdemo.data.device.GeneralScanUtils.GET_BATTERY_LEVEL_COMMAND
import com.example.stripesdemo.data.device.GeneralScanUtils.GET_VIBRATION_LEVEL_COMMAND
import com.example.stripesdemo.data.device.GeneralScanUtils.GET_VOLUME_LEVEL_COMMAND
import com.example.stripesdemo.data.device.GeneralScanUtils.NOTIFICATION_SOUND
import com.example.stripesdemo.data.device.GeneralScanUtils.RATTLE_SOUND
import com.example.stripesdemo.data.device.GeneralScanUtils.SUCCESS_SOUND
import com.example.stripesdemo.data.device.GeneralScanUtils.TAG
import com.example.stripesdemo.data.device.GeneralScanUtils.VIBRATE_ERROR
import com.example.stripesdemo.data.device.GeneralScanUtils.VIBRATE_WARNING
import com.example.stripesdemo.data.device.GeneralScanUtils.configurationDescriptorUUID
import com.example.stripesdemo.data.device.GeneralScanUtils.getBatteryLevel
import com.example.stripesdemo.data.device.GeneralScanUtils.getCommand
import com.example.stripesdemo.data.device.GeneralScanUtils.getConnectionState
import com.example.stripesdemo.data.device.GeneralScanUtils.getPickListModeCommand
import com.example.stripesdemo.data.device.GeneralScanUtils.getSettingLevel
import com.example.stripesdemo.data.device.GeneralScanUtils.isCommand
import com.example.stripesdemo.data.device.GeneralScanUtils.notifyCharUUID
import com.example.stripesdemo.data.device.GeneralScanUtils.readAndWriteCharacteristicUUID
import com.example.stripesdemo.data.device.GeneralScanUtils.serviceUUID
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.domain.entity.enums.PickListMode
import com.example.stripesdemo.domain.entity.enums.SensorFeedback
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@SuppressLint("MissingPermission")
class GeneralScanLibrary @Inject constructor(
    @ApplicationContext val context: Context,
) {

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private var bluetoothGatt: BluetoothGatt? = null

    val scope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState = _connectionState.asStateFlow()

    private val _scanValue = MutableSharedFlow<String>()
    val scanValue: Flow<String> = _scanValue

    private val _batteryLevel = MutableStateFlow("")
    val batteryLevel: StateFlow<String> = _batteryLevel.asStateFlow()

    private val _volumeLevel = MutableStateFlow("")
    val volumeLevel: StateFlow<String> = _volumeLevel.asStateFlow()

    private val _vibrationLevel = MutableStateFlow("")
    val vibrationLevel: StateFlow<String> = _vibrationLevel.asStateFlow()

    private val _macAddress = MutableStateFlow<String>("")
    val macAddress = _macAddress.asStateFlow()

//    private val scanChannel = Channel<String?>(Channel.BUFFERED)
//    val scanFlow: Flow<String?> = scanChannel.receiveAsFlow()

    private val commandQueue = mutableSetOf<String>()
    @Volatile private var writeInProgress = false

    private var enable = false

    fun connect(address: String) {
        val device = bluetoothAdapter!!.getRemoteDevice(address)

        if (device.type == BluetoothDevice.DEVICE_TYPE_DUAL)
            bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        else
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    fun disconnect() {
        enableDefaultSound()
        bluetoothGatt?.apply {
            disconnect()
            close()
        }
        bluetoothGatt = null
        onDisconnect()
        Log.d(TAG, "Device disconnected")
    }

    fun addCommand(command: String) {
        Log.d(TAG, "Command $command added!")
        commandQueue.add(command)
        processNextCommand()
    }

    private fun processNextCommand() {
        if (writeInProgress) return
        val next = commandQueue.firstOrNull() ?: return
        commandQueue.remove(next)
        setCommand(next)
    }

    fun enable() {
        enable = true
    }

    fun disable() {
        enable = false
    }

    @SuppressLint("MissingPermission")
    private fun setCommand(command: String) {
        val gatt = bluetoothGatt ?: return
        val service = gatt.getService(serviceUUID) ?: return
        val characteristic = service.getCharacteristic(readAndWriteCharacteristicUUID) ?: return

        writeInProgress = true

        val bytes = command.toByteArray()

        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeCharacteristic(
                characteristic,
                bytes,
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            )
        } else {
            characteristic.value = bytes
            gatt.writeCharacteristic(characteristic)
        }

    }

    @SuppressLint("MissingPermission")
    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val device = gatt.device ?: return
            val address = device.address


            if (status == BluetoothGatt.GATT_SUCCESS &&
                newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices()


                initMacAddress(address)
                onConnected()

//                gatt.requestMtu(512)
            }

            updateState(newState)
        }

        private fun onConnected() {
            writeInProgress = false
            commandQueue.clear()
        }

        private fun initMacAddress(address: String) {
            scope.launch {
                _macAddress.emit(address)
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            if (gatt != null && gatt.device != null) {
                Log.d(TAG, "onMtuChanged mtu=$mtu,status=$status")

            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "Service discovery failed, status=$status")
                return
            }

            val service = gatt.getService(serviceUUID) ?: run {
                Log.e(TAG, "Service not found!")
                return
            }

            val notifyCharacteristic = service.getCharacteristic(notifyCharUUID)
            if (notifyCharacteristic == null) {
                Log.e(TAG, "Write characteristic FFF1 not found")
            }

//            displayServices(gatt.services)

            gatt.readCharacteristic(notifyCharacteristic)
            gatt.setCharacteristicNotification(notifyCharacteristic, true)

            val clientCharacteristicConfigurationDescriptor = notifyCharacteristic.getDescriptor(configurationDescriptorUUID)
            if (clientCharacteristicConfigurationDescriptor != null) {
                clientCharacteristicConfigurationDescriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                val success = gatt.writeDescriptor(clientCharacteristicConfigurationDescriptor)
//                Log.d(TAG, "Writing CCCD to enable notify: $success")
            }

        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            val result = value.toString(Charsets.US_ASCII).trim()
            handleScannerInput(result)
        }

        // Required for android 10, Casio Device
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val result = characteristic.value.toString(Charsets.US_ASCII).trim()
            handleScannerInput(result)
        }


        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            writeInProgress = false
            processNextCommand()
        }

    }

    private fun handleScannerInput(
        scannerInput: String,
    ) {
        Log.d(TAG, "Scanner Input: $scannerInput")

        if (scannerInput.isCommand())
            handleCommand(scannerInput)
        else
            handleBarcode(scannerInput)
    }


    private fun updateState(newState: Int) {
        val state = getConnectionState(newState)
        scope.launch {
            _connectionState.emit(state)
        }
    }


    private fun handleCommand(command: String) {
        when (command.getCommand()) {
            Command.Battery -> {
                scope.launch {
                    _batteryLevel.emit(command.getBatteryLevel())
                }
            }
            Command.Volume -> {
                scope.launch {
                    _volumeLevel.emit(command.getSettingLevel())
                }
            }
            Command.Vibration -> {
                scope.launch {
                    _vibrationLevel.emit(command.getSettingLevel())
                }
            }
            Command.Unknown -> {
                Log.d(TAG, "Unknown command")
            }
        }

        Log.d(TAG,"Executed Command: ${command.getCommand()}")
    }

    private fun handleBarcode(barcode: String) {
        Log.d(TAG, "onCharacteristicChanged -> Barcode: $barcode")
     //   if (enable) {

        scope.launch {
            _scanValue.emit(barcode)
//            scanChannel.send(barcode)
        }
      //  }
    }

    private fun onDisconnect() {
        setConnectionStateToDisconnected()
        setPropertiesEmpty()
        setAimingDecoding()
    }

    private fun setAimingDecoding() {
        addCommand(getPickListModeCommand(PickListMode.AimingDecoding))
    }

    private fun setConnectionStateToDisconnected() {
        scope.launch {
            _connectionState.emit(ConnectionState.DISCONNECTED)
        }
    }

    private fun setPropertiesEmpty() {
        scope.launch {
            _batteryLevel.emit("")
            _macAddress.emit("")
            _volumeLevel.emit("")
            _vibrationLevel.emit("")
        }
    }

    private fun enableDefaultSound() {
        addCommand(ENABLE_DECODING_PROMPT_TONE)
    }

    fun observeBattery(): Flow<String> = flow {
        if (isConnected()) {
            emit(batteryLevel.first())
            addCommand(GET_BATTERY_LEVEL_COMMAND)
            emitAll(batteryLevel.drop(1))
        }
    }

    fun observeVolume(): Flow<String> = flow {
        if (isConnected()) {
            emit(volumeLevel.first())
            addCommand(GET_VOLUME_LEVEL_COMMAND)
            emitAll(volumeLevel.drop(1))
        }
    }

   fun observeVibration(): Flow<String> = flow {
       if (isConnected()) {
           emit(vibrationLevel.first())
           addCommand(GET_VIBRATION_LEVEL_COMMAND)
           emitAll(vibrationLevel.drop(1))
       }
    }


    private suspend fun isConnected(): Boolean {
        return _connectionState.first() == ConnectionState.CONNECTED
    }


    fun sendFeedback(sensorFeedback: SensorFeedback) {
        when (sensorFeedback) {
            SensorFeedback.SUCCESS -> {
                addCommand(SUCCESS_SOUND)
            //    addCommand(VIBRATE_SUCCESS)
            }
            SensorFeedback.ERROR -> {
                addCommand(SUCCESS_SOUND)
                addCommand(VIBRATE_ERROR)
            }
            SensorFeedback.ERROR_NO_VIBRATION -> {
                addCommand(RATTLE_SOUND)
            }
            SensorFeedback.WARNING -> {
                addCommand(VIBRATE_WARNING)
                addCommand(NOTIFICATION_SOUND)
            }
            SensorFeedback.WARNING_NO_VIBRATION -> {
                addCommand(NOTIFICATION_SOUND)
            }
            SensorFeedback.NOTIFICATION -> {
                addCommand(NOTIFICATION_SOUND)
                addCommand(VIBRATE_WARNING)
            }
            SensorFeedback.SYNC_ERROR -> {
                addCommand(ALERT_SOUND)
                addCommand(VIBRATE_ERROR)
            }
            else -> {}
        }
    }
}