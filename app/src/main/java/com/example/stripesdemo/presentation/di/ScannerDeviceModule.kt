package net.stripesapp.mlsretailsoftware.presentation.di

import android.content.Context
import com.example.stripesdemo.data.CasioScannerInterface
import com.example.stripesdemo.data.FingerScanner
import com.example.stripesdemo.data.MobileScannerInterface
import com.example.stripesdemo.data.NotSupportedDevice
import com.example.stripesdemo.data.ScannerInterface
import com.example.stripesdemo.domain.repository.SettingsRepository
import com.example.stripesdemo.presentation.Device
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
        fingerScanner: FingerScanner,
    ): ScannerInterface {
        return when {
            Device.isScanner -> CasioScannerInterface(
                context,
                settingsRepository,
                coroutineScope,
                fingerScanner
            )
            Device.isMobile -> MobileScannerInterface(
                context,
                settingsRepository,
                coroutineScope,
                fingerScanner
            )
            else -> NotSupportedDevice
        }
    }




}