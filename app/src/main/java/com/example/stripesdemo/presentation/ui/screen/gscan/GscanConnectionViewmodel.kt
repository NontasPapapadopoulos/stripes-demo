package com.example.stripesdemo.presentation.ui.screen.gscan

import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.data.exception.BluetoothIsOffException
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.domain.interactor.GetConnectionUUID
import com.example.stripesdemo.domain.interactor.executeAsFlow
import com.example.stripesdemo.domain.interactor.scanner.InitBluetoothScanner
import com.example.stripesdemo.domain.interactor.scanner.finger.GetConnectionState
import com.example.stripesdemo.domain.interactor.scanner.finger.SetCommand
import com.example.stripesdemo.presentation.BlocViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class GscanConnectionViewmodel @Inject constructor(
    private val getConnectionUUID: GetConnectionUUID,
    private val initBluetoothScanner: InitBluetoothScanner,
    private val getConnectionState: GetConnectionState,
    private val setCommand: SetCommand
): BlocViewModel<ConnectionEvent,GscanConnectionState>() {


    private val uuidFlow = getConnectionUUID.execute(Unit)
        .map { it.getOrThrow() }
        .catch {
            if (it is BluetoothIsOffException) {
                throwable.emit(it)
            }
            else {
                addError(it)
            }
        }

    private val throwable = MutableSharedFlow<Throwable?>()

    private val commandFlow = MutableSharedFlow<String>()

    private val initBluetoothScannerFlow = initBluetoothScanner.executeAsFlow(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    private val connectionStateFlow = getConnectionState.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    override val _uiState: StateFlow<GscanConnectionState> = combine(
        connectionStateFlow.onStart { emit(ConnectionState.DISCONNECTED) },
        uuidFlow,
        throwable.onStart { emit(null) },
        commandFlow.onStart { emit("") }
    ) { state, uuid, throwable, command ->
            GscanConnectionState.Content(
                uuid = uuid,
                state = state,
                throwable = throwable,
                command = command
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = GscanConnectionState.Idle
        )


    init {

        on(ConnectionEvent.CommandChanged::class) {
            commandFlow.emit(it.value)
        }

        on(ConnectionEvent.SubmitCommand::class) {
            onState<GscanConnectionState.Content> { state ->
                setCommand.execute(SetCommand.Params(state.command))
                    .fold(
                        onSuccess = {},
                        onFailure = { addError(it) }
                    )
            }
        }
    }

}




sealed interface ConnectionEvent {
    data class CommandChanged(val value: String): ConnectionEvent
    object SubmitCommand: ConnectionEvent
}



sealed interface GscanConnectionState {
    object Idle: GscanConnectionState
    data class Content(
        val uuid: String,
        val state: ConnectionState,
        val throwable: Throwable?,
        val command: String
    ): GscanConnectionState
}