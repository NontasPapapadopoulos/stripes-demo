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
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BluetoothScanner @Inject constructor(
    private val connect: Connect,
    @ApplicationContext val context: Context,
) {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private var scanning = false


    fun startScan(uuid: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
            == PackageManager.PERMISSION_GRANTED) {
            bluetoothLeScanner.startScan(
                getFilters(uuid),
                getScanSettings(),
                scannerCallback
            )
        }
    }

    private fun getFilters(uuidService: String):  MutableList<ScanFilter> {
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid.fromString(uuidService))
            .build()

        return mutableListOf(filter)
    }

    private fun getScanSettings(): ScanSettings? {
        return ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
    }


    // this is called on when scanning the QR code
    private val scannerCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val scanRecord = result.scanRecord!!.bytes

            connect.connect(device.address)
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