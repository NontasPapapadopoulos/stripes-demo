package com.example.stripesdemo.data.datasource

import com.example.stripesdemo.data.dao.ScanDao
import com.example.stripesdemo.data.entity.ScanDataEntity
import com.example.stripesdemo.data.exception.OpenScanNotInitialized
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

interface ScanDataSource {
    suspend fun initOpenScan()
    suspend fun resetOpenScan(
        properties: Map<String, String>,
        defaultProperties: Map<String, String>,
    ): ScanDataEntity

    suspend fun getOpenScan(): ScanDataEntity?
    suspend fun getScan(id: String): ScanDataEntity
    fun getOpenScanFlow(): Flow<ScanDataEntity>

    suspend fun saveAndSubmit(
        openScan: ScanDataEntity,
        nextScanProperties: Map<String, String>,
    )

    suspend fun save(scan: ScanDataEntity)
    suspend fun save(scans: List<ScanDataEntity>)
    fun getScans(): Flow<List<ScanDataEntity>>
    suspend fun getNumberOfScans(): Int
    suspend fun deleteOpenScan(sessionId: String)

}

class ScanDatabaseDataSource @Inject constructor(
    private val scanDao: ScanDao,
) : ScanDataSource {

    override suspend fun initOpenScan() {
        if (!scanDao.hasOpenScan()) {
            scanDao.put(createOpenScan())
        }
    }

    override suspend fun resetOpenScan(
        properties: Map<String, String>,
        defaultProperties: Map<String, String>,
    ): ScanDataEntity {
        val openScan = scanDao.getOpenScan()
            ?.copy(properties = properties, scanSource = null)
            ?: throw OpenScanNotInitialized()
        scanDao.put(openScan)
        return openScan
    }

    override suspend fun getOpenScan(): ScanDataEntity? {
        return scanDao.getOpenScan()
    }

    override suspend fun getScan(id: String): ScanDataEntity {
        return scanDao.getScan(id)
    }

    override suspend fun saveAndSubmit(
        openScan: ScanDataEntity,
        nextScanProperties: Map<String, String>,
    ) {
        scanDao.put(
            listOf(
                openScan.copy(
                    id = UUID.randomUUID().toString(),
                    dateScanned = LocalDateTime.now(Clock.systemUTC()),
                    submitted = true
                ),
                createOpenScan()
            )
        )
    }

    override fun getOpenScanFlow(): Flow<ScanDataEntity> {
        return scanDao.getOpenScanFlow().filterNotNull()
    }

    override suspend fun save(scan: ScanDataEntity) {
        scanDao.put(scan)
    }

    override suspend fun save(scans: List<ScanDataEntity>) {
        scanDao.put(scans)
    }


    override fun getScans(): Flow<List<ScanDataEntity>> {
        return scanDao.get()
    }

    override suspend fun getNumberOfScans(): Int {
        return scanDao.getNumberOfScans()
    }

    override suspend fun deleteOpenScan(sessionId: String) {
        scanDao.deleteOpenScan(sessionId)
    }


    private fun createOpenScan(): ScanDataEntity = ScanDataEntity(
        id = "",
        dateScanned = LocalDateTime.now(Clock.systemUTC()),
        dateModified = null,
        properties = emptyMap(),
        submitted = false,
        scanSource = null
    )


}