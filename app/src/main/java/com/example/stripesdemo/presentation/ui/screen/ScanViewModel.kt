package com.example.stripesdemo.presentation.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.interactor.scan.GetOpenScanFlow
import com.example.stripesdemo.domain.interactor.scan.InitOpenScan
import com.example.stripesdemo.domain.interactor.scan.ResetOpenScanProperties
import com.example.stripesdemo.presentation.BlocViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.example.stripesdemo.domain.interactor.scanner.GetScannerInput
import com.example.stripesdemo.domain.interactor.scanner.SetScannerEnabled
import com.example.stripesdemo.domain.interactor.scanner.TriggerCameraScan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import kotlin.getOrThrow

@HiltViewModel
class ScanViewModel @Inject constructor(
    getOpenScanFlow: GetOpenScanFlow,
    getScannerInput: GetScannerInput,
    setScannerEnabled: SetScannerEnabled,
    private val initOpenScan: InitOpenScan,
    private val resetOpenScanProperties: ResetOpenScanProperties,
    private val triggerCameraScan: TriggerCameraScan,
    private val savedStateHandle: SavedStateHandle
): BlocViewModel<ScanEvent, ScanState>() {


    private val openScanFlow = getOpenScanFlow.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }


    private val scannerInputFlow = getScannerInput.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    private val dialogFlow = MutableSharedFlow<ScanDialog?>()


    override val _uiState: StateFlow<ScanState> = combine(
        suspend { initOpenScan() }.asFlow().flatMapLatest { openScanFlow },
        scannerInputFlow,
        dialogFlow.onStart { emit(null) }
    ) { openScan, input, dialog ->

        ScanState.Content(
            barcode = "",
            count = 1,
            isSubmitEnabled = true,
            dialog = dialog
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ScanState.Idle
    )


    init {

        on(ScanEvent.TriggerCameraScan::class) {
            triggerCameraScan.execute(Unit).fold(
                onSuccess = {},
                onFailure = { addError(it) }
            )
        }


    }



    private suspend fun initOpenScan() {
        initOpenScan.execute(Unit)
            .fold(onSuccess = {}, onFailure = { addError(it) })
    }
}


sealed interface ScanDialog {
    object ClearForm: ScanDialog
    object ValidationError: ScanDialog
}


sealed interface ScanState {
    object Idle: ScanState

    data class Content(
        val barcode: String,
        val count: Int,
        val isSubmitEnabled: Boolean,
        val dialog: ScanDialog?
    ): ScanState

}




sealed interface ScanEvent {
    object ClearForm: ScanEvent
    data class SetScannerEnabled(val isEnabled: Boolean) : ScanEvent
    object SubmitText : ScanEvent
    data class TextChanged(val fieldName: String, val value: String) : ScanEvent
    object TriggerCameraScan: ScanEvent




}