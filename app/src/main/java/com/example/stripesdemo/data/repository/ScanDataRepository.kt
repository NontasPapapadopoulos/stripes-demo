package com.example.stripesdemo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.stripesdemo.data.datasource.ScanDataSource
import com.example.stripesdemo.domain.repository.ScanRepository
import com.example.stripesdemo.data.mapper.toData
import com.example.stripesdemo.data.mapper.toDomain
import com.example.stripesdemo.domain.entity.ScanDomainEntity
import javax.inject.Inject

class ScanDataRepository @Inject constructor(
    private val scanDataSource: ScanDataSource,
) : ScanRepository {

    override suspend fun initOpenScan() {
        scanDataSource.initOpenScan()
    }

    override suspend fun getOpenScan(): ScanDomainEntity? {
        return scanDataSource.getOpenScan()?.toDomain()
    }

    override suspend fun resetOpenScan(): ScanDomainEntity {
        val openScan = scanDataSource.getOpenScan()
        return scanDataSource.resetOpenScan().toDomain()
    }

    override suspend fun getScan(id: String): ScanDomainEntity {
        return scanDataSource.getScan(id).toDomain()
    }

    override fun getOpenScanFlow(): Flow<ScanDomainEntity> {
        return scanDataSource.getOpenScanFlow().map { it.toDomain() }
    }

    override suspend fun save(scan: ScanDomainEntity) {
        scanDataSource.save(scan.toData())
    }

    override suspend fun saveAndSubmit(scan: ScanDomainEntity) {
        scanDataSource.saveAndSubmit(scan.toData())
    }

    override fun getScans(): Flow<List<ScanDomainEntity>> {
        return scanDataSource.getScans().map { scans -> scans.map { it.toDomain() } }
    }

    override suspend fun getNumberOfScans(): Int {
        return scanDataSource.getNumberOfScans()
    }

    override suspend fun deleteOpenScan() {
        scanDataSource.deleteOpenScan()
    }


}