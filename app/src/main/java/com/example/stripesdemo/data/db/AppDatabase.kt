package com.example.stripesdemo.data.db

import com.example.stripesdemo.data.entity.ScanDataEntity
import com.example.stripesdemo.data.entity.SettingsDataEntity

@androidx.room.Database(
    entities = [
        ScanDataEntity::class,
        SettingsDataEntity::class,
    ],
    version = 1
)
@androidx.room.TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : androidx.room.RoomDatabase() {
    abstract fun scanDao(): com.example.stripesdemo.data.dao.ScanDao
    abstract fun settingsDao(): com.example.stripesdemo.data.dao.ScannerSettingsDao
}