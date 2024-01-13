package com.haris.sensors.repositories

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@InstallIn(ViewModelComponent::class)
@Module
internal object SensorsModule {

    @Provides
    fun provideApi(retrofit: Retrofit): SensorsApi {
        return retrofit.create(SensorsApi::class.java)
    }

    @Provides
    fun provideRepository(api: SensorsApi): SensorsRepository {
        return SensorsRepositoryImpl(api)
    }
}