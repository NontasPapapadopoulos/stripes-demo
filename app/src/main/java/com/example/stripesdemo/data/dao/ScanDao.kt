package com.example.stripesdemo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stripesdemo.data.entity.ScanDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan where submitted = 1")
    fun get(): Flow<List<ScanDataEntity>>

    @Query("SELECT COUNT(id) FROM scan where submitted = 1")
    suspend fun getNumberOfScans(): Int

    @Query("SELECT * FROM scan where submitted = 0 LIMIT 1")
    fun getOpenScanFlow(): Flow<ScanDataEntity?>

    @Query("SELECT * FROM scan where submitted = 0 LIMIT 1")
    suspend fun getOpenScan(): ScanDataEntity?

    @Query("SELECT * FROM scan where id = :id LIMIT 1")
    suspend fun getScan(id: String): ScanDataEntity

    @Query("SELECT COUNT(*)>0 from scan where submitted = 0 LIMIT 1")
    suspend fun hasOpenScan(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(scan: ScanDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(scan: List<ScanDataEntity>)

    @Query("delete from scan where id = :id")
    suspend fun delete(id: String)

    @Query("delete from scan where submitted = 0")
    suspend fun deleteOpenScan()

}