package com.example.stripesdemo.data.mapper

import com.example.stripesdemo.data.entity.ScanDataEntity
import com.example.stripesdemo.domain.entity.ScanDomainEntity

fun ScanDataEntity.toDomain() = ScanDomainEntity(
    id = id,
    dateScanned = dateScanned,
    dateModified = dateModified,
    properties = properties,
    submitted = submitted,
    scanSource = scanSource
)

fun ScanDomainEntity.toData() = ScanDataEntity(
    id = id,
    dateScanned = dateScanned,
    dateModified = dateModified,
    properties = properties,
    submitted = submitted,
    scanSource = scanSource
)