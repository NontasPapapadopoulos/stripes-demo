package com.example.stripesdemo.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "settings")
data class SettingsDataEntity(
    @PrimaryKey(autoGenerate = false)
    val dummyPrimaryKey: Int = 0,
    val scansDelay: Long,
    @ColumnInfo(defaultValue = "200")
    val feedbackDelay: Long,
    val connectionCode: String,
)
