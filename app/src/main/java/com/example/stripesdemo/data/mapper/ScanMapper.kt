package com.example.stripesdemo.data.mapper

import net.stripesapp.mlsretailsoftware.data.entity.ScanDataEntity
import net.stripesapp.mlsretailsoftware.domain.entity.ScanDomainEntity

fun ScanDataEntity.toDomain() = ScanDomainEntity(
    id = id,
    sessionId = sessionId,
    dateScanned = dateScanned,
    dateModified = dateModified,
    properties = properties,
    changes = changes,
    isVerified = isVerified,
    needsVerification = needsVerification,
    marker = marker,
    alternateMatchCodesStepId = alternateMatchCodesStepId,
    alternateMatchCodes = alternateMatchCodes?.toDomain(),
    submitted = submitted,
    isAddedScan = isAddedScan,
    order = order,
    verificationAttempts = verificationAttempts,
    scanSource = scanSource
)

fun ScanDomainEntity.toData() = ScanDataEntity(
    id = id,
    sessionId = sessionId,
    dateScanned = dateScanned,
    dateModified = dateModified,
    properties = properties,
    changes = changes,
    isVerified = isVerified,
    needsVerification = needsVerification,
    marker = marker,
    alternateMatchCodesStepId = alternateMatchCodesStepId,
    alternateMatchCodes = alternateMatchCodes?.toData(),
    submitted = submitted,
    isAddedScan = isAddedScan,
    order = order,
    verificationAttempts = verificationAttempts,
    scanSource = scanSource
)