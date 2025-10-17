package com.example.stripesdemo.presentation.di

import com.example.stripesdemo.data.datasource.ScanDataSource
import com.example.stripesdemo.data.datasource.ScanDatabaseDataSource
import com.example.stripesdemo.data.datasource.SettingsDataSource
import com.example.stripesdemo.data.datasource.SettingsDatabaseDataSource
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
    abstract fun bindSettingsDataSource(dataSource: SettingsDatabaseDataSource): SettingsDataSource


    @Binds
    @Singleton
    abstract fun bindsScanDataSource(dataSource: ScanDatabaseDataSource): ScanDataSource


}