package com.example.stripesdemo.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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

//    private val connectionChannel = Channel<ConnectionState>(Channel.BUFFERED)
//    val state: Flow<ConnectionState> = connectionChannel.receiveAsFlow()

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
            //remove timeout callback
            //There is a problem here Every time a new object is generated that causes the same device to be disconnected and the connection produces two objects


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
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Services discovered!")

                val service = gatt.getService(serviceUUID)
                val characteristic = service?.getCharacteristic(writeCharUUID)

                if (characteristic != null) {
                    Log.d(TAG, "Reading characteristic: $writeCharUUID")
                    gatt.readCharacteristic(characteristic)
                    gatt.setCharacteristicNotification(characteristic, true)

                    val cccd = characteristic.getDescriptor(uuidNotifyDesc)
                    if (cccd != null) {
                        cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        val success = gatt.writeDescriptor(cccd)
                        Log.d(TAG, "Writing CCCD to enable notify: $success")
                    }


                } else {
                    Log.e(TAG, "Characteristic not found!")
                }
            }

            else {
                Log.e(TAG, "Service discovery failed, status=$status")
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


    companion object {
        private const val TAG = "GSCAN_CONNECT"
    }

}