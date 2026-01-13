package com.example.stripesdemo.data

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.ParcelUuid
import android.util.Log
import com.example.stripesdemo.data.device.GeneralScanLibrary
import com.example.stripesdemo.data.device.GeneralScanUtils.TAG
import com.example.stripesdemo.data.exception.BluetoothIsOffException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@SuppressLint("MissingPermission")
class BluetoothScannerService @Inject constructor(
    private val generalScanLibrary: GeneralScanLibrary,
    @ApplicationContext val context: Context,
): Service() {

    val bluetoothManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter = bluetoothManager.adapter
    private var bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private val scanPeriod: Long = 15_000

    suspend fun startScan(uuid: String) {
        initBluetoothScanner()
        try {
            withTimeout(scanPeriod) {
                startBluetoothScanner(uuid)
                awaitCancellation()
            }
        }
        catch (_: TimeoutCancellationException) {
            stopScan()
        }
        catch (e: Exception) {
            if (e.message == "BT Adapter is not turned ON") {
                throw BluetoothIsOffException()
            }
        }
    }

    private fun initBluetoothScanner() {
        if (bluetoothLeScanner == null)
            bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    private fun startBluetoothScanner(uuid: String) {
        Log.d(TAG, "Start scanning....")
        bluetoothLeScanner.startScan(
            getFilters(uuid),
            getScanSettings(),
            scannerCallback
        )
    }


    fun stopScan() {
        Log.d(TAG, "Stop scanning....")
        bluetoothLeScanner.stopScan(scannerCallback)
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
            Log.d(TAG, "Connecting...")
            generalScanLibrary.connect(device.address)
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

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}