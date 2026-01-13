package com.example.stripesdemo.presentation.ui.screen.gscan.connect

import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.domain.interactor.executeAsFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import com.example.stripesdemo.domain.interactor.scanner.finger.GetConnectionCode
import com.example.stripesdemo.domain.interactor.scanner.finger.GetConnectionState
import com.example.stripesdemo.domain.interactor.scanner.finger.SetDefaultSettings
import com.example.stripesdemo.domain.interactor.scanner.finger.StartBluetoothScanner
import com.example.stripesdemo.domain.interactor.scanner.finger.StopBluetoothScanner
import com.example.stripesdemo.presentation.BlocViewModel
import javax.inject.Inject


@HiltViewModel
open class ConnectFingerScannerViewModel @Inject constructor(
    private val getConnectionCode: GetConnectionCode,
    private val getConnectionState: GetConnectionState,
    private val startBluetoothScanner: StartBluetoothScanner,
    private val stopBluetoothScanner: StopBluetoothScanner,
    private val setDefaultSettings: SetDefaultSettings
): BlocViewModel<ConnectFingerScannerEvent, ConnectFingerScannerState>() {


    private val connectionProcessFlow = MutableSharedFlow<ConnectionProcess>()

    private val connectionCodeFlow = getConnectionCode.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    private val connectionStateFlow = getConnectionState.execute(Unit)
        .map { it.getOrThrow() }
        .onEach {
            it?.let { state ->
                when (state) {
                    ConnectionState.CONNECTED -> connectionProcessFlow.emit(ConnectionProcess.Successful)
                    ConnectionState.CONNECTING -> connectionProcessFlow.emit(ConnectionProcess.Connecting)
                    else -> connectionProcessFlow.emit(ConnectionProcess.Idle)
                }
            }
        }
        .catch { addError(it) }

    private val initBluetoothScannerFlow = startBluetoothScanner.executeAsFlow(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }


    override val _uiState: StateFlow<ConnectFingerScannerState> = combine(
        connectionProcessFlow.onStart { emit(ConnectionProcess.Idle) },
        connectionCodeFlow.onStart { emit("") },
        connectionStateFlow.onStart { emit(ConnectionState.DISCONNECTED) },
        initBluetoothScannerFlow.onStart { emit(Unit) },
    ) { connectionProcess, connectionCode, connectionState, initBluetoothScanner ->

        when (connectionProcess) {
            ConnectionProcess.Idle -> {
                ConnectFingerScannerState.Content(
                    connectionProcess = connectionProcess,
                    awaitsForScan = true,
                    code = connectionCode,
                    connectionState = connectionState,
                )
            }
            ConnectionProcess.Connecting -> {
                ConnectFingerScannerState.Content(
                    connectionProcess = connectionProcess,
                    awaitsForScan = false,
                    code = connectionCode,
                    connectionState = connectionState,
                )
            }
            ConnectionProcess.Successful -> ConnectFingerScannerState.ConnectionSuccessful
            ConnectionProcess.Failed -> ConnectFingerScannerState.ConnectionFailed
        }


    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ConnectFingerScannerState.Content(
            code = "",
            connectionProcess = ConnectionProcess.Idle,
            awaitsForScan = true,
            connectionState = null,
        )
    )


    init {



        on(ConnectFingerScannerEvent.StopScan::class) {
            stopBluetoothScanner.execute(Unit)
                .fold(
                    onSuccess = {},
                    onFailure = { addError(it) }
                )
        }

        on(ConnectFingerScannerEvent.SetDefaultSettings::class) {
            setDefaultSettings.execute(Unit)
                .fold(
                    onSuccess = {},
                    onFailure = { addError(it) }
                )
        }
    }

}




enum class ConnectionProcess { Idle, Connecting, Successful, Failed }



sealed interface ConnectFingerScannerEvent {
    object Complete: ConnectFingerScannerEvent
    object StopScan: ConnectFingerScannerEvent
    object SetDefaultSettings: ConnectFingerScannerEvent
}


sealed interface ConnectFingerScannerState {

    data class Content(
        val connectionProcess: ConnectionProcess,
        val code: String,
        val awaitsForScan: Boolean,
        val connectionState: ConnectionState?,
    ): ConnectFingerScannerState

    object ConnectionSuccessful: ConnectFingerScannerState

    object ConnectionFailed: ConnectFingerScannerState


}
