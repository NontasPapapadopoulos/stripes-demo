package com.example.stripesdemo.domain.interactor.scanner.finger


import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

open class GetSettings @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): FlowUseCase<String, Unit>(dispatcher) {

    override fun invoke(params: Unit): Flow<String> {
        return flow { "@MENU_OPTO@ZZ@EBLE@W0@XP@T0@D3I@D3Q@DLA@Q2@BBP@0F@0F@0F@0F@ZZ@OTPO_UNEM@" }
        // Normally this comes from an API call
    }
}
