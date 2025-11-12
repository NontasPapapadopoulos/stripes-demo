package com.example.stripesdemo.data

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
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

    }

    }