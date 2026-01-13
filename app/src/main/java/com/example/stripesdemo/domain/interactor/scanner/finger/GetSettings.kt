package com.example.stripesdemo.domain.interactor.scanner.finger


import com.example.stripesdemo.domain.IoDispatcher
import com.example.stripesdemo.domain.interactor.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

//open class GetSettings @Inject constructor(
//    private val configurationsRepository: ConfigurationsRepository,
//    @IoDispatcher private val dispatcher: CoroutineDispatcher,
//): FlowUseCase<String, Unit>(dispatcher) {
//
//    override fun invoke(params: Unit): Flow<String> {
//        return flow { emit(projectRepository.getCurrentProject()) }.flatMapMerge { project ->
//            configurationsRepository.getScanConfigurationFlow(project.id)
//                .map { it?.fingerScannerConfiguration ?: "" }
//        }
//    }
//}
