package com.example.stripesdemo.presentation.di

import android.content.Context
import com.example.stripesdemo.data.device.CasioScanner
import com.example.stripesdemo.data.device.MultipleScanner
import com.example.stripesdemo.data.device.FingerScanner
import com.example.stripesdemo.data.device.GeneralScanLibrary
import com.example.stripesdemo.data.device.MobileScanner
import com.example.stripesdemo.data.device.ScannerInterface
import com.example.stripesdemo.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope

import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScannerDeviceModule {

    @Provides
    @Singleton
    fun provideScannerInterface(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository,
        coroutineScope: CoroutineScope,
        fingerScanner: GeneralScanLibrary,
        mobileScanner: MobileScanner,
        casioScanner: CasioScanner,
    ): ScannerInterface {
         return  MultipleScanner(
            context,
            settingsRepository,
            coroutineScope,
            fingerScanner,
             mobileScanner,
             casioScanner,
        )
    }

}