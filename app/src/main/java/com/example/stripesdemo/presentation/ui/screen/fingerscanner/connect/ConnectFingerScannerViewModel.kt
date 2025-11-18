package com.example.stripesdemo.presentation.ui.screen.fingerscanner.connect

import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.entity.enums.ConnectionState
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
open class ConnectFingerScannerViewModel @Inject constructor(
    private val getConnectionQrCode: GetConnectionQrCode,
    private val getConnectionState: GetConnectionState,
    ): BlocViewModel<ConnectFingerScannerEvent, ConnectFingerScannerState>(){



    private val connectionCodeFlow = getConnectionQrCode.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    private val connectionStateFlow = getConnectionState.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }



    private val _navigationFlow = MutableSharedFlow<Navigation>()
    open val navigationFlow: SharedFlow<Navigation> = _navigationFlow.asSharedFlow()


    override val _uiState: StateFlow<ConnectFingerScannerState> = combine(
        connectionCodeFlow.onStart { emit("") },
        connectionStateFlow.onStart { emit(ConnectionState.DISCONNECTED) },
    ) { connectionCode, connectionState  ->

        ConnectFingerScannerState.Content(
            awaitsForScan = true,
            code = connectionCode,
            connectionState = connectionState,
        )



    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ConnectFingerScannerState.Content(
            code = "",
            awaitsForScan = true,
            connectionState = ConnectionState.DISCONNECTED
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
        val code: String?,
        val awaitsForScan: Boolean,
        val connectionState: ConnectionState,
    ): ConnectFingerScannerState

    object ConnectionSuccessful : ConnectFingerScannerState
    object ConnectionFailed: ConnectFingerScannerState


}
