package com.example.stripesdemo.presentation.ui.screen.scanlist

import androidx.lifecycle.viewModelScope
import com.example.stripesdemo.domain.entity.ScanDomainEntity
import com.example.stripesdemo.domain.interactor.scan.DeleteScan
import com.example.stripesdemo.domain.interactor.scan.GetScans
import com.example.stripesdemo.presentation.BlocViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ScanListViewmodel @Inject constructor(
    private val getScans: GetScans,
    private val deleteScan: DeleteScan
): BlocViewModel<ScanListEvent, ScanListState>() {


    override val _uiState: StateFlow<ScanListState> = getScans.execute(Unit)
        .map { it.getOrThrow() }
        .catch { addError(it) }
        .map { scans ->
            ScanListState.Content(
                scans = scans
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ScanListState.Idle
        )


    init {

        on(ScanListEvent.DeleteScan::class) {
            deleteScan.execute(DeleteScan.Params(it.scan))
                .fold(
                    onSuccess = {},
                    onFailure = { addError(it) }
                )
        }

    }

}


sealed interface ScanListEvent {
    data class DeleteScan(val scan: ScanDomainEntity): ScanListEvent
}


sealed interface ScanListState {
    object Idle: ScanListState
    data class Content(
        val scans: List<ScanDomainEntity>
    ): ScanListState
}