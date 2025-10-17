package com.example.stripesdemo.presentation.ui.screen.fingerscanner.connect

import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.entity.ConnectionStateDomainEntity
import com.example.stripesdemo.domain.interactor.scanner.finger.GetConnectionQrCode
import com.example.stripesdemo.domain.interactor.scanner.finger.GetConnectionState
import com.example.stripesdemo.presentation.BlocViewModel
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
import javax.inject.Inject


@HiltViewModel
open class ConnectFingerScannerViewModel @Inject constructor(
    private val getConnectionQrCode: GetConnectionQrCode,
    private val getConnectionState: GetConnectionState,
    ): BlocViewModel<ConnectFingerScannerEvent, ConnectFingerScannerState>(){


    private val connectionProcessFlow = MutableSharedFlow<ConnectionProcess>()

    private val connectionCodeFlow = getConnectionQrCode.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    private val connectionStateFlow = getConnectionState.execute(Unit)
        .map { it.getOrThrow() }
        .onEach {
            it?.let {
                when (it.state) {
                    ConnectionState.Connected.value -> connectionProcessFlow.emit(ConnectionProcess.Successful)
                    ConnectionState.Connecting.value -> connectionProcessFlow.emit(ConnectionProcess.Connecting)
                    else -> connectionProcessFlow.emit(ConnectionProcess.Failed)
                }
            }
        }
        .catch { addError(it) }



    private val _navigationFlow = MutableSharedFlow<Navigation>()
    open val navigationFlow: SharedFlow<Navigation> = _navigationFlow.asSharedFlow()


    override val _uiState: StateFlow<ConnectFingerScannerState> = combine(
        connectionProcessFlow.onStart { emit(ConnectionProcess.Idle) },
        connectionCodeFlow.onStart { emit("") },
        connectionStateFlow.onStart { emit(null) },
    ) { connectionProcess, connectionCode, connectionState ->

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
            connectionState = null
        )
    )


    init {

        on(ConnectFingerScannerEvent.Complete::class) {
            onState<ConnectFingerScannerState.ConnectionSuccessful> { state ->
                    _navigationFlow.emit(Navigation.NoSession)


            }
        }
    }

}


sealed interface Navigation {
    object NoSession: Navigation
}

enum class ConnectionProcess { Idle, Connecting, Successful, Failed }

enum class ConnectionState(val value: String) {
    Connected("connected"),
    Connecting("connecting")
}

sealed interface ConnectFingerScannerEvent {
    object Complete: ConnectFingerScannerEvent
}


sealed interface ConnectFingerScannerState {

    data class Content(
        val connectionProcess: ConnectionProcess,
        val code: String?,
        val awaitsForScan: Boolean,
        val connectionState: ConnectionStateDomainEntity?,
    ): ConnectFingerScannerState

    object ConnectionSuccessful : ConnectFingerScannerState
    object ConnectionFailed: ConnectFingerScannerState


}
