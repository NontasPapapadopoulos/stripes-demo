package com.example.stripesdemo.domain.repository

import com.example.stripesdemo.domain.entity.SettingsDomainEntity
import kotlinx.coroutines.flow.Flow


interface SettingsRepository {
    fun getSettingsFlow(): Flow<SettingsDomainEntity?>
    suspend fun getSettings(): SettingsDomainEntity?
    suspend fun initSettings()
}