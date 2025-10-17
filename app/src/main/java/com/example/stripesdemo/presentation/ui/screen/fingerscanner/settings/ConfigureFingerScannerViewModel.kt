package com.example.stripesdemo.presentation.ui.screen.fingerscanner.settings

import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.interactor.scanner.finger.GetSettings
import com.example.stripesdemo.presentation.BlocViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
open class ConfigureFingerScannerViewModel @Inject constructor(
    private val getSettings: GetSettings
): BlocViewModel<Unit, ConfigureFingerScannerState>() {

    override val _uiState: StateFlow<ConfigureFingerScannerState> = getSettings.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }
        .map { settings ->
            ConfigureFingerScannerState.Content(
                settings = settings
            )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ConfigureFingerScannerState.Content(settings = null)
    )

}


sealed interface ConfigureFingerScannerState {
    data class Content(
        val settings: String?,
    ): ConfigureFingerScannerState
}


