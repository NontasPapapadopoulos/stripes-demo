package com.example.stripesdemo.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Connect @Inject constructor(
    @ApplicationContext val context: Context,
) {

    // HAVE A LOOK ON THAT:
    // https://developer.android.com/develop/connectivity/bluetooth/ble/transfer-ble-data

    private val serviceUUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
    private val writeCharUUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
    private val notifyCharUUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb")
    var uuidNotifyDesc = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")


    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter


    val connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)

    private val scanChannel = Channel<String?>(Channel.BUFFERED)
    val scanFlow: Flow<String?> = scanChannel.receiveAsFlow()
    val scope = CoroutineScope(Dispatchers.IO)

    private var bluetoothGatt: BluetoothGatt? = null


    fun connect(address: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
            == PackageManager.PERMISSION_GRANTED) {

            val device = bluetoothAdapter!!.getRemoteDevice(address)

            if (device.type == BluetoothDevice.DEVICE_TYPE_DUAL)
                bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
            else
                bluetoothGatt = device.connectGatt(context, false, gattCallback)

        }

    }

    fun disconnect() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
            == PackageManager.PERMISSION_GRANTED) {

            bluetoothGatt?.apply {
                disconnect()
                close()
            }
            connectionState.value = ConnectionState.DISCONNECTED

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun setCommand(command: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val gatt = bluetoothGatt ?: return
        val service = gatt.getService(serviceUUID) ?: return
        val characteristic = service.getCharacteristic(writeCharUUID) ?: return

        val bytes = command.toByteArray()

        val result = gatt.writeCharacteristic(
            characteristic,
            bytes,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        )

        Log.d("BLE", "Write result = $result")

    }

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val device = gatt.device ?: return
            val address = device.address


            scope.launch {
                val state = when(newState) {
                    0 -> ConnectionState.DISCONNECTED
                    1 -> ConnectionState.CONNECTING
                    2 -> ConnectionState.CONNECTED
                    3 -> ConnectionState.DISCONNECTED
                    else -> ConnectionState.DISCONNECTED
                }
                connectionState.value = state

            }


            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, gatt.device.address + "state: " + newState)
                    gatt.discoverServices()

                }
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            if (gatt != null && gatt.device != null) {
                Log.d(TAG, "onMtuChanged mtu=$mtu,status=$status")

            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "Service discovery failed, status=$status")
                return
            }

            Log.d(TAG, "Services discovered!")

            val service = gatt.getService(serviceUUID) ?: run {
                Log.e(TAG, "Service not found!")
                return
            }

            val writeChar = service.getCharacteristic(writeCharUUID)
            if (writeChar == null) {
                Log.e(TAG, "Write characteristic FFF1 not found")
            }

            displayServices(gatt.services)

            Log.d(TAG, "Reading characteristic: $writeCharUUID")
            gatt.readCharacteristic(writeChar)
            gatt.setCharacteristicNotification(writeChar, true)

            val cccd = writeChar.getDescriptor(uuidNotifyDesc)
            if (cccd != null) {
                cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                val success = gatt.writeDescriptor(cccd)
                Log.d(TAG, "Writing CCCD to enable notify: $success")
            }

        }



        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic.value
                Log.d(TAG, "Characteristic read: ${ByteUtils.toHexString(data)}")
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic.value
                Log.d(TAG, "Characteristic read: ${ByteUtils.toHexString(data)}")
            }


        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic, status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val data = characteristic.value
                Log.d(TAG, "Characteristic read: ${ByteUtils.toHexString(data)}")
            }
        }


        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            val barcode = value.map { it.toInt().toChar() }.joinToString("")
            Log.d(TAG, gatt.device.address + " -- onCharacteristicChanged: " + barcode)


            scope.launch {
                scanChannel.send(barcode)
            }
        }

        //Required for android 10
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val data = characteristic.value
//                    Log.d(
//                        TAG,
//                        gatt.device.address + " -- onCharacteristicChanged: "
//                                + if (data != null) ByteUtils.toHexString(data) else ""
//                    )

            // TODO: Find the ByteUtils.
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor, status: Int
        ) {
            val uuid = descriptor.characteristic.uuid
            Log.d(TAG, "write descriptor uuid:$uuid")

            Log.d(TAG, "set characteristic notification is completed")

        }


        override fun onDescriptorRead(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int,
            value: ByteArray
        ) {
            super.onDescriptorRead(gatt, descriptor, status, value)
            val uuid = descriptor.characteristic.uuid
            Log.d(TAG, "read descriptor uuid:$uuid")
            if (status == BluetoothGatt.GATT_SUCCESS) {


            }
        }


        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            Log.d(TAG, "read remoteRssi, rssi: $rssi")

        }
    }


    private fun displayServices(services: List<BluetoothGattService>) {
        services.forEach { service ->
            val serviceUuid = service.uuid.toString()
            val serviceName = lookupServiceName(service.uuid) // Human name (e.g., "Battery Service")

            Log.d(TAG, "──────────────────────────────────")
            Log.d(TAG, "Service UUID: $serviceUuid")
            Log.d(TAG, "Service Name: $serviceName")
            Log.d(TAG, "──────────────────────────────────")

            val characteristics = service.characteristics
            if (characteristics.isEmpty()) {
                Log.d(TAG, "   (No characteristics)")
            }

            characteristics.forEach { characteristic ->
                val charUuid = characteristic.uuid.toString()
                val charName = lookupCharacteristicName(characteristic.uuid)
                val properties = characteristic.properties

                val propList = mutableListOf<String>()
                if (properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) propList += "READ"
                if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE != 0) propList += "WRITE"
                if (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0) propList += "WRITE_NO_RESP"
                if (properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0) propList += "NOTIFY"
                if (properties and BluetoothGattCharacteristic.PROPERTY_INDICATE != 0) propList += "INDICATE"

                Log.d(TAG, "   ├─ Char UUID : $charUuid")
                Log.d(TAG, "   ├─ Char Name : $charName")
                Log.d(TAG, "   └─ Properties: ${propList.joinToString(", ")}")
            }
        }
    }

    private fun lookupServiceName(uuid: UUID): String {
        return when (uuid.toString().substring(4, 8)) {
            "1800" -> "Generic Access"
            "1801" -> "Generic Attribute"
            "180A" -> "Device Information"
            "180F" -> "Battery Service"
            "1815" -> "Automation IO"
            "1816" -> "Cycling Speed and Cadence"
            "1818" -> "Cycling Power"
            "1819" -> "Location and Navigation"
            "181A" -> "Environmental Sensing"
            "181B" -> "Body Composition"
            "181C" -> "User Data"
            "181D" -> "Weight Scale"
            "181E" -> "Bond Management"
            "181F" -> "Continuous Glucose Monitoring"
            "1820" -> "Internet Protocol Support"
            "1821" -> "Indoor Positioning"
            "1822" -> "Pulse Oximeter"
            "1823" -> "HTTP Proxy"
            "1824" -> "Transport Discovery"
            "1825" -> "Object Transfer"
            "1843" -> "Audio Stream Control"
            "1844" -> "Broadcast Audio Scan"
            "1848" -> "Audio Input Control"
            "1849" -> "Volume Control"
            "1850" -> "Volume Offset Control"
            "1851" -> "Coordinated Set Identification"
            "1852" -> "Device Time"
            "18A0" -> "Mesh Provisioning"
            "18A1" -> "Mesh Proxy"
            else -> "Unknown Service"
        }
    }

    private fun lookupCharacteristicName(uuid: UUID): String {
        return when (uuid.toString().substring(4, 8)) {
            "2A00" -> "Device Name"
            "2A01" -> "Appearance"
            "2A19" -> "Battery Level"
            "2A23" -> "System ID"
            "2A24" -> "Model Number"
            "2A25" -> "Serial Number"
            "2A26" -> "Firmware Revision"
            "2A27" -> "Hardware Revision"
            "2A28" -> "Software Revision"
            "2A29" -> "Manufacturer Name"
            "2AF1" -> "Barcode Data (Common in scanners)"
            else -> "Unknown Characteristic"
        }
    }


    companion object {
        private const val TAG = "GSCAN_CONNECT"
    }

}