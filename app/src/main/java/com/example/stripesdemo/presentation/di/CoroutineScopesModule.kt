package net.stripesapp.mlsretailsoftware.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {
    @Provides
    @Singleton
    fun providesCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob()) + Dispatchers.IO
    }
}