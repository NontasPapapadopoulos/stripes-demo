package com.example.stripesdemo.domain.interactor

import com.example.stripesdemo.data.Connect
import com.example.stripesdemo.domain.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.example.stripesdemo.domain.repository.ScannerRepository
import javax.inject.Inject

open class GetConnectionUUID @Inject constructor(
    private val scannerRepository: ScannerRepository,
    private val connect: Connect,
    @IoDispatcher private val flowDispatcher: CoroutineDispatcher,
) : FlowUseCase<String, Unit>(flowDispatcher) {

    override fun invoke(params: Unit): Flow<String> {
        return  flow  {
            val uuid = getNewAdUuid()
            connect.connect("xx")
            emit("{G6000/${uuid}}")
        }

    }





}