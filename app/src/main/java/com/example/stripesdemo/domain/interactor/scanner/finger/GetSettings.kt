package net.stripesapp.mlsretailsoftware.domain.interactor.scanner.finger


import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.stripesapp.mlsretailsoftware.domain.executor.IoDispatcher
import net.stripesapp.mlsretailsoftware.domain.interactor.FlowUseCase
import net.stripesapp.mlsretailsoftware.domain.repository.ConfigurationsRepository
import net.stripesapp.mlsretailsoftware.domain.repository.ProjectRepository
import javax.inject.Inject

open class GetSettings @Inject constructor(
    private val projectRepository: ProjectRepository,
    private val configurationsRepository: ConfigurationsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
): FlowUseCase<String, Unit>(dispatcher) {

    override fun invoke(params: Unit): Flow<String> {
        return flow { emit(projectRepository.getCurrentProject()) }.flatMapMerge { project ->
            configurationsRepository.getScanConfigurationFlow(project.id)
                .map { it?.fingerScannerConfiguration ?: "" }
        }
    }
}
