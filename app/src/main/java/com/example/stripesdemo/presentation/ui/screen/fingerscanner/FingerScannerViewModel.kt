package com.example.stripesdemo.presentation.ui.screen.fingerscanner


import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.entity.DeviceDomainEntity
import com.example.stripesdemo.domain.interactor.scanner.finger.GetConnectedDevices
import com.example.stripesdemo.domain.interactor.scanner.finger.GetVolume
import com.example.stripesdemo.domain.interactor.scanner.finger.SetVolume
import com.example.stripesdemo.presentation.BlocViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
open class FingerScannerViewModel @Inject constructor(
    private val setVolume: SetVolume,
    getVolume: GetVolume,
    getConnectedDevices: GetConnectedDevices
): BlocViewModel<FingerScannerEvent, FingerScannerState>() {


    private val preSelectedVolumeFlow = getVolume.execute(Unit)
        .map { it.getOrThrow().toFloat() }
        .catch { addError(it) }

    private val volumeFlow = MutableSharedFlow<Float>()


    private val getConnectedDevicesFlow = getConnectedDevices.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }



    override val _uiState: StateFlow<FingerScannerState> = combine(
        merge(
            preSelectedVolumeFlow.onStart { emit(0f) },
            volumeFlow.onStart { emit(0f) }
        ),
        getConnectedDevicesFlow.onStart { emit(listOf()) }
    ) {  volume, devices,  ->
        FingerScannerState.Content(
            volume = volume,
            devices = devices
        )
    }.stateIn(
        scope = viewModelScope,
        initialValue = FingerScannerState.Idle,
        started = SharingStarted.WhileSubscribed()
    )


    init {
        on(FingerScannerEvent.SetVolume::class) {
            onState<FingerScannerState.Content> { state ->
                setVolume.execute(SetVolume.Params(level = state.volume.toInt())).fold(
                    onSuccess = {},
                    onFailure = { addError(it) }
                )
            }
        }

        on(FingerScannerEvent.SetSliderPosition::class) {
            volumeFlow.emit(it.level)
        }

    }

}



sealed interface FingerScannerEvent {
    object SetVolume: FingerScannerEvent
    data class SetSliderPosition(val level: Float): FingerScannerEvent

}

sealed interface FingerScannerState {
    object Idle: FingerScannerState

    data class Content(
        val volume: Float,
        val devices: List<DeviceDomainEntity>
    ): FingerScannerState
}