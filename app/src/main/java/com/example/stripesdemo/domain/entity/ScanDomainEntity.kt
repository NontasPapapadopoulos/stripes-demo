package com.example.stripesdemo.domain.entity

import com.example.stripesdemo.domain.entity.enums.ScanSource
import java.time.LocalDateTime

data class ScanDomainEntity(
    val id: String,
    val dateScanned: LocalDateTime,
    val dateModified: LocalDateTime?,
    val properties: Map<String, String>,
    val submitted: Boolean,
    val scanSource: ScanSource?
)
