package com.example.stripesdemo.presentation

import android.app.Application
import com.example.stripesdemo.data.ScannerInterface
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

lateinit var application: MyApplication

@HiltAndroidApp
class MyApplication : Application() {
    @Inject
    lateinit var scannerInterface: ScannerInterface

    override fun onCreate() {
        super.onCreate()
        application = this



        runBlocking {
            scannerInterface.initialize(
                // Only enable scanner with a ScannerSession.
                true
            )
        }

    }

}