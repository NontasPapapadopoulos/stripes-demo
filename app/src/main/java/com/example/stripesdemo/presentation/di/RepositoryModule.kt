package com.example.stripesdemo.presentation.di

import com.example.stripesdemo.data.repository.ScanDataRepository
import com.example.stripesdemo.data.repository.ScannerDataRepository
import com.example.stripesdemo.data.repository.SettingsDataRepository
import com.example.stripesdemo.domain.repository.ScanRepository
import com.example.stripesdemo.domain.repository.ScannerRepository
import com.example.stripesdemo.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun bindScannerRepository(repository: ScannerDataRepository): ScannerRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(repository: SettingsDataRepository): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindScanRepository(repository: ScanDataRepository): ScanRepository



}