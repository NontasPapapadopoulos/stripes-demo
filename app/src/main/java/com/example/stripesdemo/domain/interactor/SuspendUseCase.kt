package com.example.stripesdemo.domain.interactor

import kotlinx.coroutines.CoroutineDispatcher
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