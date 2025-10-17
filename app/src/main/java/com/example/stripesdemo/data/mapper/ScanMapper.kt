package com.example.stripesdemo.data.mapper

import com.example.stripesdemo.data.entity.ScanDataEntity
import com.example.stripesdemo.domain.entity.ScanDomainEntity

fun ScanDataEntity.toDomain() = ScanDomainEntity(
    id = id,
    dateScanned = dateScanned,
    submitted = submitted,
    scanSource = scanSource,
    barcode = barcode,
    count = count
)

fun ScanDomainEntity.toData() = ScanDataEntity(
    id = id,
    dateScanned = dateScanned,
    submitted = submitted,
    scanSource = scanSource,
    barcode = barcode,
    count = count
)