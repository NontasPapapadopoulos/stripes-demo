package com.example.stripesdemo.presentation.ui.screen.scanlist

import com.example.stripesdemo.domain.entity.ScanDomainEntity
import com.example.stripesdemo.presentation.BlocViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ScanListViewmodel @Inject constructor(

): BlocViewModel<ScanListState, ScanListEvent>() {


    override val _uiState: StateFlow<ScanListEvent>
        get() = TODO("Not yet implemented")


}


sealed interface ScanListEvent {

}


sealed interface ScanListState {
    object Idle: ScanListState
    data class Content(
        val scans: List<ScanDomainEntity>
    ): ScanListState
}