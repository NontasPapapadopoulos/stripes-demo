package com.example.stripesdemo.presentation.ui.screen.gscan

import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.entity.enums.ConnectionState
import com.example.stripesdemo.domain.interactor.scanner.finger.GetConnectionState
import com.example.stripesdemo.presentation.BlocViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
open class AdvancedSettingsViewModel @Inject constructor(
    private val getConnectionState: GetConnectionState,
) : BlocViewModel<AdvancedSettingsEvent, AdvancedSettingsState>() {

    private val connectionStateFlow = getConnectionState.execute(Unit)
        .map { it.getOrThrow() }
        .map { it == ConnectionState.CONNECTED }
        .catch { addError(it) }


    override val _uiState: StateFlow<AdvancedSettingsState> =
        connectionStateFlow.map { isConnected ->

            AdvancedSettingsState.Content(
                isConnected = isConnected,
            )

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AdvancedSettingsState.Idle,

            )

}


interface AdvancedSettingsEvent {

}


interface AdvancedSettingsState {
    object Idle : AdvancedSettingsState
    data class Content(
        val isConnected: Boolean,
    ) : AdvancedSettingsState
}