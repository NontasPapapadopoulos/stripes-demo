package com.example.stripesdemo.domain.interactor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

abstract class FlowUseCase<out Type, in Params>(
    private val flowDispatcher: CoroutineDispatcher
) where Type : Any? {

    protected abstract fun invoke(params: Params): Flow<Type>

    open fun execute(params: Params): Flow<Result<Type>> {
        return invoke(params)
            .flowOn(flowDispatcher)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
}

fun <Type: Any> FlowUseCase<Type, Unit>.execute(): Flow<Result<Type>> {
    return execute(Unit)
}