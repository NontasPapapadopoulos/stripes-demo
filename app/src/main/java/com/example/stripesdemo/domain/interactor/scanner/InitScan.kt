package com.example.stripesdemo.domain.interactor.scanner

import com.example.stripesdemo.data.Bluetooth
import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import com.example.stripesdemo.domain.repository.ScannerRepository
import javax.inject.Inject

//open class InitScan @Inject constructor(
//    private val scannerRepository: ScannerRepository,
//    private val bluetooth: Bluetooth,
//    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
//) : SuspendUseCase<Unit, Unit>(flowDispatcher) {
//
//    override suspend fun invoke(params: Unit) {
//        bluetooth.startScan()
//    }
//
//}