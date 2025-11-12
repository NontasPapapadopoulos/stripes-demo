package com.example.stripesdemo.data.datasource

import com.example.stripesdemo.data.dao.ScannerSettingsDao
import kotlinx.coroutines.flow.Flow
import com.example.stripesdemo.data.entity.SettingsDataEntity
import javax.inject.Inject



interface SettingsDataSource {
    fun getSettingsFlow(): Flow<SettingsDataEntity?>
    suspend fun getSettings(): SettingsDataEntity?
    suspend fun initSettings()
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

    override suspend fun initSettings() {
        val settings = SettingsDataEntity(
            scansDelay = 200L,
            feedbackDelay = 200L,
            connectionUUID = getNewAdUuid()
        )

        scannerSettingsDao.put(settings)
    }

    private fun getNewAdUuid(): String {
        val randomNumStr = ((0..0xffff).random()).toString(16).padStart(4,'0').uppercase()
        //val randomNumStr = ((1000..9999).random()).toString()
        return "0000${randomNumStr}-0000-1000-8000-00805F9B34FB"
    }

}