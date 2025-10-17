package com.example.stripesdemo.data.entity

import androidx.room.Entity
import com.example.stripesdemo.domain.entity.enums.ScanSource
import java.time.LocalDateTime

@Entity(
    tableName = "scan"
)
data class ScanDataEntity(
    val id: String,
    val dateScanned: LocalDateTime,
    val dateModified: LocalDateTime?,
    val properties: Map<String, String>,
    val submitted: Boolean,
    val scanSource: ScanSource?
)