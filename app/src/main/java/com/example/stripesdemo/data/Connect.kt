package com.example.stripesdemo.data

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Arrays
import javax.inject.Inject

class Connect @Inject constructor(
    @ApplicationContext val context: Context
) {

    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter



    fun connect(address: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
            == PackageManager.PERMISSION_GRANTED) {
            val device = bluetoothAdapter!!.getRemoteDevice(address)

            val bluetoothGatt =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && device.type == BluetoothDevice.DEVICE_TYPE_DUAL) {
                    device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
                } else {
                    device.connectGatt(context, false, gattCallback)
                }
        }

    }

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val device = gatt.device ?: return
            val address = device.address
            //remove timeout callback
            //There is a problem here Every time a new object is generated that causes the same device to be disconnected and the connection produces two objects
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {


                }
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            if (gatt != null && gatt.device != null) {
                Log.d(TAG, "onMtuChanged mtu=$mtu,status=$status")

            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

            } else {
                Log.d(TAG, "onServicesDiscovered received: $status")
            }
        }


        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //processReceivedData(characteristic)
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            Log.d(TAG, "onCharacteristicRead:$status")


        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic, status: Int
        ) {
            Log.d(TAG, gatt.device.address + "-----write success----- status: " + status)

        }


        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            Log.d(TAG,
                gatt.device.address + " -- onCharacteristicChanged: " + ByteUtils.toHexString(value)
            )

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
            Log.d(
                TAG,
                "write descriptor uuid:$uuid"
            )

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
            Log.d(
                TAG,
                "read descriptor uuid:$uuid"
            )
            if (status == BluetoothGatt.GATT_SUCCESS) {


            }
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
            Log.d(
                TAG,
                "read remoteRssi, rssi: $rssi"
            )

        }
    }


    companion object {
        private const val TAG = "GSCAN_CONNECT"
    }

}