package com.example.stripesdemo.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.stripesdemo.domain.entity.enums.ScanSource
import java.time.LocalDateTime

@Entity(
    tableName = "scan"
)
data class ScanDataEntity(
    @PrimaryKey
    val id: String,
    val barcode: String,
    val count: String,
    val dateScanned: LocalDateTime,
    val submitted: Boolean,
    val scanSource: ScanSource?
)