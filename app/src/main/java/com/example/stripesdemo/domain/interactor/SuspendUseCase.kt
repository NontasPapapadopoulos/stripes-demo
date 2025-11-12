package com.example.stripesdemo.domain.interactor

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

abstract class SuspendUseCase<out Type, in Params>(
    private val dispatcher: CoroutineDispatcher
) where Type : Any? {

    protected abstract suspend fun invoke(params: Params): Type

    open suspend fun execute(params: Params): Result<Type> = withContext(dispatcher) {
        try {
            Result.success(invoke(params))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}

suspend fun <Type: Any> SuspendUseCase<Type, Unit>.execute(): Result<Type> {
    return execute(Unit)
}


fun <T, P> SuspendUseCase<T, P>.executeAsFlow(params: P): Flow<Result<T>> = flow {
    emit(execute(params))
}

fun <Type: Any> SuspendUseCase<Type, Unit>.executeAsFlow(): Flow<Result<Type>> =
    this.executeAsFlow(Unit)