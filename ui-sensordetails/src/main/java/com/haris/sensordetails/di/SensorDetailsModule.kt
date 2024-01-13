package com.haris.sensordetails.di

import com.haris.sensordetails.repositories.SensorDetailsApi
import com.haris.sensordetails.repositories.SensorDetailsRepository
import com.haris.sensordetails.repositories.SensorDetailsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@InstallIn(ViewModelComponent::class)
@Module
internal object SensorDetailsModule {

    @Provides
    fun provideApi(retrofit: Retrofit): SensorDetailsApi {
        return retrofit.create(SensorDetailsApi::class.java)
    }

    @Provides
    fun provideRepository(api: SensorDetailsApi): SensorDetailsRepository {
        return SensorDetailsRepositoryImpl(api)
    }
}