package com.example.stripesdemo.data

import com.example.stripesdemo.domain.entity.enums.Scanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ScannerInterface {
    val enabled: StateFlow<Boolean>
    val scanner: Scanner
    val values: Flow<String>

    suspend fun initialize(disabledByDefault: Boolean)
    suspend fun cleanup()

    fun enable()
    fun disable()

    suspend fun performCameraScan()
}
