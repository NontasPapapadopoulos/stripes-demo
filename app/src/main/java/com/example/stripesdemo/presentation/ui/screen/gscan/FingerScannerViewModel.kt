package com.example.stripesdemo.presentation.ui.screen.gscan


import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.entity.enums.ConnectionState
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
import com.example.stripesdemo.domain.interactor.scanner.finger.GetBatteryLevel
import com.example.stripesdemo.domain.interactor.scanner.finger.GetConnectionState
import com.example.stripesdemo.domain.interactor.scanner.finger.GetMacAddress
import com.example.stripesdemo.domain.interactor.scanner.finger.GetVibration
import com.example.stripesdemo.domain.interactor.scanner.finger.GetVolume
import com.example.stripesdemo.domain.interactor.scanner.finger.SetVibration
import com.example.stripesdemo.domain.interactor.scanner.finger.SetVolume
import com.example.stripesdemo.presentation.BlocViewModel
import javax.inject.Inject

@HiltViewModel
open class FingerScannerViewModel @Inject constructor(
    private val setVolume: SetVolume,
    private val setVibration: SetVibration,
    private val getConnectionState: GetConnectionState,
    private val getBatteryLevel: GetBatteryLevel,
    private val getMacAddress: GetMacAddress,
    getVolume: GetVolume,
    getVibration: GetVibration
): BlocViewModel<FingerScannerEvent, FingerScannerState>() {


    private val preSelectedVolumeFlow = getVolume.execute(Unit)
        .map { it.getOrThrow()  }
        .map { if (it.isEmpty()) 0f else it.toFloat() }
        .catch { addError(it) }

    private val preSelectedVibrationFlow = getVibration.execute(Unit)
        .map { it.getOrThrow() }
        .map { if (it.isEmpty()) 0f else it.toFloat() }
        .catch { addError(it) }

    private val volumeFlow = MutableSharedFlow<Float>()
    private val vibrationFlow = MutableSharedFlow<Float>()


    private val connectionStateFlow = getConnectionState.execute(Unit)
        .map { it.getOrThrow() }
        .map { it == ConnectionState.CONNECTED }
        .catch { addError(it) }

    private val batteryLevelFlow = getBatteryLevel.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    private val macAddressFlow = getMacAddress.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }

    override val _uiState: StateFlow<FingerScannerState> = combine(
        merge(
            preSelectedVolumeFlow,
            volumeFlow.onStart { emit(0f) }
        ),
        merge(
            preSelectedVibrationFlow,
            vibrationFlow.onStart { emit(0f) }
        ),
        connectionStateFlow.onStart { emit(false) },
        batteryLevelFlow.onStart { emit("") },
        macAddressFlow.onStart { emit("") },
    ) { volume, vibration, isConnected, batteryLevel, macAddress ->
        FingerScannerState.Content(
            volume = volume,
            vibration = vibration,
            isConnected = isConnected,
            macAddress = macAddress,
            batteryLevel = batteryLevel
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

        on(FingerScannerEvent.SetVolumeSliderPosition::class) {
            volumeFlow.emit(it.level)
        }



        on(FingerScannerEvent.SetVibration::class) {
            onState<FingerScannerState.Content> { state ->
                setVibration.execute(SetVibration.Params(level = state.vibration.toInt())).fold(
                    onSuccess = {},
                    onFailure = { addError(it) }
                )
            }
        }

        on(FingerScannerEvent.SetVibrationSliderPosition::class) {
            vibrationFlow.emit(it.level)
        }

    }

}



sealed interface FingerScannerEvent {
    object SetVolume: FingerScannerEvent
    object SetVibration: FingerScannerEvent
    data class SetVolumeSliderPosition(val level: Float): FingerScannerEvent
    data class SetVibrationSliderPosition(val level: Float): FingerScannerEvent
}

sealed interface FingerScannerState {
    object Idle: FingerScannerState

    data class Content(
        val volume: Float,
        val vibration: Float,
        val macAddress: String,
        val isConnected: Boolean,
        val batteryLevel: String
    ): FingerScannerState
}