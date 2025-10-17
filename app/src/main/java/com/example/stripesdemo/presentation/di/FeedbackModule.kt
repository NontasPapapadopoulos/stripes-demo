package com.example.stripesdemo.presentation.di

import com.example.stripesdemo.domain.utils.SensorFeedbackService
import com.example.stripesdemo.presentation.feedback.SensorFeedbackProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorFeedbackModule {

    @Provides
    @Singleton //feedBackProvider: FeedBackProvider
    fun providesSensorFeedbackService(): SensorFeedbackService {
        return SensorFeedbackProvider()
    }
}


