package com.example.stripesdemo.presentation.ui.screen.gscan

import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.interactor.GetConnectionUUID
import com.example.stripesdemo.domain.interactor.executeAsFlow
import com.example.stripesdemo.domain.interactor.scanner.InitBluetoothScanner
import com.example.stripesdemo.presentation.BlocViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.uuid.Uuid


@HiltViewModel
class GscanConnectionViewmodel @Inject constructor(
    private val getConnectionUUID: GetConnectionUUID,
    private val initBluetoothScanner: InitBluetoothScanner
): BlocViewModel<ConnectionEvent,GscanConnectionState>() {


    private val uuidFlow = getConnectionUUID.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

//    private val initBluetoothScannerFlow = initBluetoothScanner.executeAsFlow(Unit)
//        .map { it.getOrThrow() }
//        .catch { addError(it) }


    override val _uiState: StateFlow<GscanConnectionState> =uuidFlow.map {
            GscanConnectionState.Content(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = GscanConnectionState.Idle
        )


}




sealed interface ConnectionEvent



sealed interface GscanConnectionState {
    object Idle: GscanConnectionState
    data class Content(
        val uuid: String
    ): GscanConnectionState
}