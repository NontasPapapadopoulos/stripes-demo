package com.example.stripesdemo.domain.interactor.scan

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import com.example.stripesdemo.domain.repository.ScanRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


open class SubmitOpenScan @Inject constructor(
    private val scanRepository: ScanRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : SuspendUseCase<Unit, Unit>(dispatcher) {

    override suspend fun invoke(params: Unit) {
        val openScan = scanRepository.getOpenScan()

        openScan?.let {
            val scan = it.copy(
                properties = openScan.properties
            )
//            scanRepository.saveAndSubmit(scan)
        }
    }

}