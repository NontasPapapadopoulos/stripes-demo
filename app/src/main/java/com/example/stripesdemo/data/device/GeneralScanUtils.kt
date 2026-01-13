package com.example.stripesdemo.data.device

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.util.Log
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.domain.entity.enums.PickListMode
import java.util.UUID
import kotlin.collections.forEach

object GeneralScanUtils {
    val serviceUUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
    val readAndWriteCharacteristicUUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb") // for commands and responses.
    val notifyCharUUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
    val configurationDescriptorUUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")


    const val GET_BATTERY_LEVEL_COMMAND = "{G1066}"
    const val GET_VOLUME_LEVEL_COMMAND = "{G3010/?}"
    const val GET_VIBRATION_LEVEL_COMMAND = "{G3008/?}"
    const val SET_VOLUME_LEVEL_COMMAND = "{G3010/X}"
    const val SET_VIBRATION_LEVEL_COMMAND = "{G3008/X}"


    const val DECODING_PROMPT_TONE_STATUS = "{G3031/?}"
    const val DISABLE_DECODING_PROMPT_TONE = "{G3031/0}"
    const val ENABLE_DECODING_PROMPT_TONE = "{G3031/1}"
    const val NOTIFICATION_SOUND = "{G3024/1}"
    const val SUCCESS_SOUND = "{G3024/4}"
    const val ALERT_SOUND = "{G3024/3}"
    const val RATTLE_SOUND = "{G3024/6}"

    const val VIBRATE_SUCCESS = "{G3027/0}"
    const val VIBRATE_WARNING = "{G3027/1}"
    const val VIBRATE_ERROR = "{G3027/3}"

    const val GET_PICK_LIST_MODE_COMMAND = "{G3029/G3030/1;G3023/0/02 01 01 0C 00 04 20 00 07 41 45 41 44 45 43 30 3B 23 03;G3030/0;}"
    const val BUZZER_TEST_COMMAND =  "{G1068}"

    const val TAG = "GSCAN_LIBRARY"

    val pickListModeCommands = mapOf(
        PickListMode.AimingDecoding to "{G3023/0/02 00 01 0C 00 04 20 00 06 41 45 41 44 45 43 32 3B 21 03}",
        PickListMode.FullAreaDecoding to "{G3023/0/02 00 01 0C 00 04 20 00 06 41 45 41 44 45 43 30 3B 23 03}",
        PickListMode.CentralAreaDecoding to "{G3023/0/02 00 01 0C 00 04 20 00 06 41 45 41 44 45 43 31 3B 22 03}"
    )


    fun String.isCommand(): Boolean =
        this.startsWith("{G") && this.endsWith("}") ||
                this.startsWith("[G") && this.endsWith("]")

    fun String.getBatteryLevel(): String =
        this.replace("[","")
            .replace("]","")
            .split("/")[2]

    fun String.getSettingLevel(): String =
        this.replace("{", "")
            .replace("}", "")
            .split("/")[1]



    fun getPickListModeCommand(mode: PickListMode): String {
        return pickListModeCommands.getValue(mode)
    }

    fun String.getCommand(): Command {
        return when  {
            this.contains(Command.Volume.value) -> Command.Volume
            this.contains(Command.Vibration.value) -> Command.Vibration
            this.contains(Command.Battery.value) -> Command.Battery
            else -> Command.Unknown
        }
    }


    fun ByteArray.getBarcode(): String = this.map { it.toInt().toChar() }.joinToString("")

    fun String.setValue(value: String) =
        this.replace("X", value)


    fun getConnectionState(newState: Int): ConnectionState {
        return when (newState) {
            0 -> ConnectionState.DISCONNECTED
            1 -> ConnectionState.CONNECTING
            2 -> ConnectionState.CONNECTED
            3 -> ConnectionState.DISCONNECTED
            else -> ConnectionState.DISCONNECTED
        }
    }



    fun displayServices(services: List<BluetoothGattService>) {
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


}


enum class Command(val value: String) {
    Battery("G1066"),
    Volume("G3010"),
    Vibration("G3008"),
    Unknown("Unknown")
}