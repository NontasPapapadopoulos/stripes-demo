package com.example.stripesdemo.domain.interactor.scanner.finger

import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import com.example.stripesdemo.domain.repository.ScannerRepository
import javax.inject.Inject
import kotlin.random.Random

open class GetConnectionQrCode @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : FlowUseCase<String, Unit>(flowDispatcher) {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(params: Unit): Flow<String> {
        return  flow {
            val code = generateConnectionCode()
            emit(scannerRepository.setConnectionCode(code))
        }.flatMapLatest {
            scannerRepository.getQrCodeFromOpticon()

        }



    }

    private fun generateConnectionCode() =
        (1..4)
            .map { Random.nextInt(0, 16) }
            .joinToString("") { it.toString(16) }
            .uppercase()

}