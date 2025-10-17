package com.example.stripesdemo.domain.repository

import com.example.stripesdemo.domain.entity.ScanDomainEntity
import kotlinx.coroutines.flow.Flow

interface ScanRepository {
    suspend fun getOpenScan(): ScanDomainEntity?
    suspend fun initOpenScan()
    fun getOpenScanFlow(): Flow<ScanDomainEntity>
    suspend fun saveAndSubmit(scan: ScanDomainEntity)
    fun getScans(): Flow<List<ScanDomainEntity>>
    fun getNumberOfScans(): Flow<Int>
    suspend fun deleteScan(scan: ScanDomainEntity)

}