package com.example.stripesdemo.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.stripesdemo.data.datasource.ScanDataSource
import com.example.stripesdemo.domain.repository.ScanRepository
import com.example.stripesdemo.data.mapper.toData
import com.example.stripesdemo.data.mapper.toDomain
import net.stripesapp.mlsretailsoftware.domain.entity.ScanDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.ScanFlowStepDomainEntity
import net.stripesapp.mlsretailsoftware.domain.entity.enums.Mode
import net.stripesapp.mlsretailsoftware.domain.utils.getNextScanProperties
import net.stripesapp.mlsretailsoftware.domain.utils.resetProperties
import javax.inject.Inject

class ScanDataRepository @Inject constructor(
    private val scanDataSource: ScanDataSource,
) : ScanRepository {

    override suspend fun initOpenScan(sessionId: String, mode: Mode) {
        val defaultProperties = getScanFlowSteps(sessionId).getNextScanProperties()
        scanDataSource.initOpenScan(sessionId, defaultProperties, mode)
    }

    override suspend fun getOpenScan(sessionId: String): ScanDomainEntity? {
        return scanDataSource.getOpenScan(sessionId)?.toDomain()
    }

    override suspend fun resetOpenScan(
        sessionId: String,
        selectedField: String?
    ): ScanDomainEntity {
        val scanFlowSteps = getScanFlowSteps(sessionId)

        val openScan = scanDataSource.getOpenScan(sessionId)
        val currentProperties = openScan?.properties?.plus(openScan.changes) ?: emptyMap()

        val properties = scanFlowSteps.resetProperties(currentProperties, selectedField)
        val defaultProperties = scanFlowSteps.getNextScanProperties(properties)

        return scanDataSource.resetOpenScan(sessionId, properties, defaultProperties).toDomain()
    }

    override suspend fun getScan(id: String): ScanDomainEntity {
        return scanDataSource.getScan(id).toDomain()
    }

    override fun getOpenScanFlow(sessionId: String): Flow<ScanDomainEntity> {
        return scanDataSource.getOpenScanFlow(sessionId).map { it.toDomain() }
    }

    override suspend fun save(scan: ScanDomainEntity) {
        scanDataSource.save(scan.toData())
    }

    override suspend fun save(scans: List<ScanDomainEntity>) {
        scanDataSource.save(scans.map { it.toData() })
    }

    override suspend fun updateMarker(scanId: String, marker: Boolean) {
       scanDataSource.updateMarker(scanId, marker)
    }

    override suspend fun saveAndSubmit(scan: ScanDomainEntity, mode: Mode) {
        val nextScanProperties =
            getScanFlowSteps(scan.sessionId).getNextScanProperties(scan.properties)
        scanDataSource.saveAndSubmit(scan.toData(), nextScanProperties, mode)
    }

    override fun getScans(sessionId: String): Flow<List<ScanDomainEntity>> {
        return scanDataSource.getScans(sessionId).map { scans -> scans.map { it.toDomain() } }
    }

    override suspend fun getNumberOfScans(checkInId: String): Int {
        return scanDataSource.getNumberOfScans(checkInId)
    }

    override suspend fun getNumberOfVerifiedScans(checkInId: String): Int {
        return scanDataSource.getNumberOfVerifiedScans(checkInId)
    }

    override suspend fun getNumberOfVerifyAddedScans(zoneCheckInId: String): Int {
        return scanDataSource.getNumberOfVerifyAddedScans(zoneCheckInId)
    }

    override fun getZoneCheckInScans(checkInId: String): Flow<List<ScanDomainEntity>> {
        return scanDataSource.getZoneCheckInScans(checkInId)
            .map { scans -> scans.map { it.toDomain() } }
    }

    override fun getSessionScans(sessionId: String): Flow<List<ScanDomainEntity>> {
        return scanDataSource.getSessionScans(sessionId)
            .map { scans -> scans.map { it.toDomain() } }
    }

    override suspend fun deleteOpenScan(sessionId: String) {
        scanDataSource.deleteOpenScan(sessionId)
    }


    private suspend fun getScanFlowSteps(sessionId: String): List<ScanFlowStepDomainEntity> {
        val session = sessionDataSource.getSession(sessionId)
        val zoneCheckIn = zoneDataSource.getZoneCheckIn(session.checkInId)
        return configurationsDataSource.getScanFlowSteps(zoneCheckIn.projectId)
            .map { it.toDomain() }
    }
}