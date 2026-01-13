package com.example.stripesdemo.presentation.di

import com.example.stripesdemo.data.datasource.ScanDataSource
import com.example.stripesdemo.data.datasource.ScanDatabaseDataSource
import com.example.stripesdemo.data.datasource.ScannerLocalDataSource
import com.example.stripesdemo.data.datasource.ScannerLocalDataSourceImpl
import com.example.stripesdemo.data.datasource.SettingsDataSource
import com.example.stripesdemo.data.datasource.SettingsLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindSettingsDataSource(dataSource: SettingsLocalDataSource): SettingsDataSource


    @Binds
    @Singleton
    abstract fun bindsScanDataSource(dataSource: ScanDatabaseDataSource): ScanDataSource


    @Binds
    @Singleton
    abstract fun bindScannerDataSource(dataSource: ScannerLocalDataSourceImpl): ScannerLocalDataSource

}