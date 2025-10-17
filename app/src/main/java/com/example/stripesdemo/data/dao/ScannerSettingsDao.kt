package com.example.stripesdemo.data.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.stripesdemo.data.entity.SettingsDataEntity


@Dao
interface ScannerSettingsDao {

    @Query("SELECT * FROM settings LIMIT 1")
    fun getSettingsFlow(): Flow<SettingsDataEntity?>

    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun getSettings(): SettingsDataEntity?

}