package com.example.stripesdemo.presentation.di

import android.content.Context
import com.example.stripesdemo.data.MultipleScannerInterface
import com.example.stripesdemo.data.FingerScanner
import com.example.stripesdemo.data.MobileScanner
import com.example.stripesdemo.data.ScannerInterface
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

//    @Provides
//    @Singleton
//    fun provideMobileScanner(
//
//    ): MobileScanner

    @Provides
    @Singleton
    fun provideScannerInterface(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository,
        coroutineScope: CoroutineScope,
        fingerScanner: FingerScanner,
        mobileScanner: MobileScanner
    ): ScannerInterface {
             return  MultipleScannerInterface(
                context,
                settingsRepository,
                coroutineScope,
                fingerScanner,
                 mobileScanner
            )

        }


}