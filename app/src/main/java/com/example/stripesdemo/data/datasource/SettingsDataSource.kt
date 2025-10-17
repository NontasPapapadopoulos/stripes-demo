package com.example.stripesdemo.data.datasource

import com.example.stripesdemo.data.dao.ScannerSettingsDao
import kotlinx.coroutines.flow.Flow
import com.example.stripesdemo.data.entity.SettingsDataEntity
import javax.inject.Inject



interface SettingsDataSource {
    fun getSettingsFlow(): Flow<SettingsDataEntity?>
    suspend fun getSettings(): SettingsDataEntity?
}

class SettingsDatabaseDataSource @Inject constructor(
    private val scannerSettingsDao: ScannerSettingsDao,
) : SettingsDataSource {



    override fun getSettingsFlow(): Flow<SettingsDataEntity?> {
        return scannerSettingsDao.getSettingsFlow()
    }

    override suspend fun getSettings(): SettingsDataEntity? {
        return scannerSettingsDao.getSettings()
    }


}