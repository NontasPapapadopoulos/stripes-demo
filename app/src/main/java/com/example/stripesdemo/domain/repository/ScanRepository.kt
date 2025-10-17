package com.example.stripesdemo.domain.repository

import com.example.stripesdemo.domain.entity.ScanDomainEntity
import kotlinx.coroutines.flow.Flow

interface ScanRepository {

    suspend fun initOpenScan()

    suspend fun resetOpenScan():ScanDomainEntity

    suspend fun getOpenScan(): ScanDomainEntity?

    suspend fun getScan(id: String): ScanDomainEntity

    fun getOpenScanFlow(sessionId: String): Flow<ScanDomainEntity>

    suspend fun save(scan: ScanDomainEntity)

    fun getScans(sessionId: String): Flow<List<ScanDomainEntity>>

    suspend fun deleteOpenScan(sessionId: String)

}