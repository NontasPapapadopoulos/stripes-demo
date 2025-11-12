package com.example.stripesdemo.presentation.di

import android.content.Context
import com.example.stripesdemo.data.Bluetooth
import com.example.stripesdemo.data.Connect
import com.example.stripesdemo.domain.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

//    @ExperimentalCoroutinesApi
//    @IoDispatcher
//    @Provides
//    fun provideBluetoothConnection(
//        @ApplicationContext context: Context
//    ): Connect = Connect(context)
//
//
//    @ExperimentalCoroutinesApi
//    @IoDispatcher
//    @Provides
//    fun provideBluetoothScan(
//        @ApplicationContext context: Context
//    ): Bluetooth = Bluetooth(context)
}
