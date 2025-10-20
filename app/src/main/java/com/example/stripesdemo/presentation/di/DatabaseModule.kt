package com.example.stripesdemo.presentation.di

import android.content.Context
import androidx.room.Room
import com.example.stripesdemo.data.db.AppDatabase
import com.example.stripesdemo.data.dao.ScanDao
import com.example.stripesdemo.data.dao.ScannerSettingsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val APP_DATABASE_NAME = "scanner_db"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, APP_DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideScanDao(database: AppDatabase): ScanDao {
        return database.scanDao()
    }


    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase): ScannerSettingsDao {
        return database.settingsDao()
    }

}