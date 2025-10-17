package com.example.stripesdemo.presentation

import android.os.Build

object Device {
    private const val CASIO = "CASIO"

    val isScanner = Build.BRAND == CASIO
    val isMobile = Build.BRAND != CASIO
}