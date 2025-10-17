package com.example.stripesdemo.domain.interactor.scan

import com.example.stripesdemo.domain.interactor.FlowUseCase
import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.entity.ScanDomainEntity
import com.example.stripesdemo.domain.repository.ScanRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject


open class GetNumberOfScans @Inject constructor(
    private val scanRepository: ScanRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : FlowUseCase<Int, Unit>(flowDispatcher) {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(params: Unit): Flow<Int> {
        return scanRepository.getNumberOfScans()
    }

}