package com.example.stripesdemo.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import net.stripesapp.mlsretailsoftware.domain.entity.enums.Scanner

interface ScannerInterface {
    val enabled: StateFlow<Boolean>
    val scanner: Scanner
    val values: Flow<String>

    suspend fun initialize(disabledByDefault: Boolean)
    suspend fun cleanup()

    fun enable()
    fun disable()
}
