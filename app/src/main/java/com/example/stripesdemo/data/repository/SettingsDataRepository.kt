package com.example.stripesdemo.data.repository

import com.example.stripesdemo.data.datasource.SettingsDataSource
import com.example.stripesdemo.data.mapper.toDomain
import com.example.stripesdemo.domain.entity.SettingsDomainEntity
import com.example.stripesdemo.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsDataRepository @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
) : SettingsRepository {

    override fun getSettingsFlow(): Flow<SettingsDomainEntity?> {
        return settingsDataSource.getSettingsFlow().map { it?.toDomain() }
    }

    override suspend fun getSettings(): SettingsDomainEntity? {
        return settingsDataSource.getSettings()?.toDomain()
    }

    override suspend fun initSettings() {
        val settings = getSettings()
        if (settings ==  null)
            settingsDataSource.initSettings()
    }


}