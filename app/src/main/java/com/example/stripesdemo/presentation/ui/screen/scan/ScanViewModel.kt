package com.example.stripesdemo.presentation.ui.screen.scan

import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.entity.DeviceDomainEntity
import com.example.stripesdemo.domain.interactor.scan.GetNumberOfScans
import com.example.stripesdemo.domain.interactor.scan.GetOpenScanFlow
import com.example.stripesdemo.domain.interactor.scan.InitOpenScan
import com.example.stripesdemo.domain.interactor.scan.SubmitScan
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
import com.example.stripesdemo.domain.interactor.scanner.finger.GetConnectedDevices
import com.example.stripesdemo.domain.utils.combine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import kotlin.getOrThrow

@HiltViewModel
class ScanViewModel @Inject constructor(
    getOpenScanFlow: GetOpenScanFlow,
    getScannerInput: GetScannerInput,
    setScannerEnabled: SetScannerEnabled,
    private val initOpenScan: InitOpenScan,
    private val triggerCameraScan: TriggerCameraScan,
    private val submitScan: SubmitScan,
    getConnectedDevices: GetConnectedDevices,
    getNumberOfScans: GetNumberOfScans
): BlocViewModel<ScanEvent, ScanState>() {


    private val openScanFlow = getOpenScanFlow.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }


    private val scannerInputFlow = getScannerInput.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    private val numberOfScansFlow = getNumberOfScans.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    private val getConnectedDevicesFlow = getConnectedDevices.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }


    private val dialogFlow = MutableSharedFlow<ScanDialog?>()

    private val barcodeFlow = MutableSharedFlow<String>()
    private val countFlow = MutableSharedFlow<String>()

    override val _uiState: StateFlow<ScanState> = combine(
        suspend { initOpenScan() }.asFlow().flatMapLatest { openScanFlow },
        merge(barcodeFlow.onStart { emit("") },
            scannerInputFlow
        ),
        countFlow.onStart { emit("") },
        dialogFlow.onStart { emit(null) },
        numberOfScansFlow,
        getConnectedDevicesFlow.onStart { emit(listOf()) }
    ) { openScan, barcode, count, dialog, numberOfScans, devices ->

        ScanState.Content(
            barcode = barcode,
            count = count,
            isSubmitEnabled = barcode.isNotEmpty() && count.isNotEmpty(),
            dialog = dialog,
            numberOfScans = numberOfScans,
            devices = devices
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ScanState.Idle
    )


    init {
        on(ScanEvent.BarcodeChanged::class) {
            barcodeFlow.emit(it.value)
        }

        on(ScanEvent.CountChanged::class) {
            countFlow.emit(it.value)
        }

        on(ScanEvent.TriggerCameraScan::class) {
            triggerCameraScan.execute(Unit).fold(
                onSuccess = {},
                onFailure = { addError(it) }
            )
        }

        on(ScanEvent.SubmitScan::class) {
            onState<ScanState.Content> { state ->
                submitScan.execute(params = SubmitScan.Params(
                    barcode = state.barcode,
                    count = state.count
                    )
                )
                .fold(
                    onSuccess = {
                        barcodeFlow.emit("")
                        countFlow.emit("")
                    },
                    onFailure = { addError(it) }
                )
            }
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
        val count: String,
        val isSubmitEnabled: Boolean,
        val dialog: ScanDialog?,
        val numberOfScans: Int,
        val devices: List<DeviceDomainEntity>
    ): ScanState

}




sealed interface ScanEvent {
    object ClearForm: ScanEvent
    data class SetScannerEnabled(val isEnabled: Boolean) : ScanEvent

    data class BarcodeChanged(val value: String) : ScanEvent
    data class CountChanged(val value: String) : ScanEvent
    object TriggerCameraScan: ScanEvent
    object SubmitScan: ScanEvent


}