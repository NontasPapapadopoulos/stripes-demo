package com.example.stripesdemo.data

import android.view.KeyEvent
import com.example.stripesdemo.domain.entity.enums.Scanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

object NotSupportedDevice : ScannerInterface {
    override val enabled = MutableStateFlow(false)
    override val scanner = Scanner.Regular
    override val values get() = emptyFlow<String>()

    override suspend fun initialize(disabledByDefault: Boolean) {}
    override suspend fun cleanup() {}

    override fun enable() {}
    override fun disable() {}


}