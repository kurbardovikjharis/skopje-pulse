package com.haris.sensordetails.di

import com.haris.sensordetails.datasource.RemoteDataSource
import com.haris.sensordetails.repositories.SensorDetailsApi
import com.haris.sensordetails.repositories.SensorDetailsRepository
import com.haris.sensordetails.repositories.SensorDetailsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object SensorDetailsModule {

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): SensorDetailsApi {
        return retrofit.create(SensorDetailsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideRepository(api: SensorDetailsApi): SensorDetailsRepository {
        return SensorDetailsRepositoryImpl(RemoteDataSource(api))
    }
}