package com.example.stripesdemo.domain.entity

import com.example.stripesdemo.domain.entity.enums.ScanSource
import java.time.LocalDateTime

data class ScanDomainEntity(
    val id: String,
    val barcode: String,
    val count: String,
    val submitted: Boolean,
    val scanSource: ScanSource?,
    val dateScanned: LocalDateTime,
    )
