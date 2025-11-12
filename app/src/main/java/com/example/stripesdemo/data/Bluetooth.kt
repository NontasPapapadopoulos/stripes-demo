package com.example.stripesdemo.data

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Bluetooth @Inject constructor(
    @ApplicationContext val context: Context
) {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private var scanning = false

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
            == PackageManager.PERMISSION_GRANTED) {
            bluetoothLeScanner.startScan(
                getFilters("asd"),
                getScanSettings(),
                scannerCallback
            )
        }
    }

    private fun getFilters(uuidService: String):  MutableList<ScanFilter> {
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid.fromString(uuidService.toString())) //8.0以上手机后台扫描，必须开启
            .build()

        return mutableListOf(filter)
    }

    private fun getScanSettings(): ScanSettings? {
        return ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .build()
    }


    private val scannerCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val scanRecord = result.scanRecord!!.bytes


        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            for (sr in results) {
                Log.d("ScanResult - Results", sr.toString())
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e("Scan Failed", "Error Code: $errorCode")
        }
    }

}