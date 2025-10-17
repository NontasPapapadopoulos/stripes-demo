//package net.stripesapp.mlsretailsoftware.presentation.di
//
//import android.content.Context
//import com.example.stripesdemo.data.MobileScannerInterface
//import com.example.stripesdemo.data.ScannerInterface
//import com.example.stripesdemo.domain.repository.SettingsRepository
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import kotlinx.coroutines.CoroutineScope
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.FingerScanner
//import net.stripesapp.mlsretailsoftware.domain.repository.SettingsRepository
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.KeyboardActionsController
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.KeyboardActionsProducer
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.KeyboardActionsSource
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.KeyboardInterface
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.NotSupportedDevice
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.ScannerInterface
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.casio.CasioKeyboard
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.casio.CasioScannerInterface
//import net.stripesapp.mlsretailsoftware.data.scannerdevice.mobile.MobileScannerInterface
//import net.stripesapp.mlsretailsoftware.presentation.Device
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object ScannerDeviceModule {
//    @Provides
//    @Singleton
//    fun provideScannerInterface(
//        @ApplicationContext context: Context,
//        settingsRepository: SettingsRepository,
//        coroutineScope: CoroutineScope,
//        fingerScanner: FingerScanner,
//    ): ScannerInterface {
//        return when {
//            Device.isScanner -> CasioScannerInterface(context, settingsRepository, coroutineScope, fingerScanner)
//            Device.isMobile -> MobileScannerInterface(
//                context,
//                settingsRepository,
//                coroutineScope,
//                fingerScanner
//            )
//            else -> NotSupportedDevice
//        }
//    }
//
//
//
//
//}