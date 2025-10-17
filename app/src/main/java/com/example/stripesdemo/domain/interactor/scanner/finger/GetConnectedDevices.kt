package com.example.stripesdemo.domain.interactor.scanner.finger


import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.entity.DeviceDomainEntity
import com.example.stripesdemo.domain.interactor.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import com.example.stripesdemo.domain.repository.ScannerRepository
import javax.inject.Inject

open class GetConnectedDevices @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): FlowUseCase<List<DeviceDomainEntity>, Unit>(dispatcher) {


    override fun invoke(params: Unit):Flow<List<DeviceDomainEntity>> {
        return scannerRepository.getConnectedDevices()
    }

}
