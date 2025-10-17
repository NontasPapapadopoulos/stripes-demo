package net.stripesapp.mlsretailsoftware.domain.interactor.scanner.finger

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.FlowUseCase
import com.example.stripesdemo.domain.repository.ScannerRepository
import javax.inject.Inject

open class GetConnectionQrCode @Inject constructor(
    private val scannerRepository: ScannerRepository,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : FlowUseCase<String, Unit>(flowDispatcher) {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(params: Unit): Flow<String> {
        return flow {
            val code = scannerRepository.getConnectionCode()
                .replace("0x", "")
            emit(scannerRepository.setConnectionCode(code))
        }.flatMapLatest {
            scannerRepository.getQrCodeFromOpticon()
        }

    }

}