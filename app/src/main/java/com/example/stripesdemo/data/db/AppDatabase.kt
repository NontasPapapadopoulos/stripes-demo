package com.example.stripesdemo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.stripesdemo.data.dao.ScanDao
import com.example.stripesdemo.data.dao.ScannerSettingsDao
import com.example.stripesdemo.data.entity.ScanDataEntity
import com.example.stripesdemo.data.entity.SettingsDataEntity

@Database(
    entities = [
        ScanDataEntity::class,
        SettingsDataEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao
    abstract fun settingsDao(): ScannerSettingsDao
}