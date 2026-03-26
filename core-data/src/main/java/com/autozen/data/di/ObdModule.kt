package com.autozen.data.di

import com.autozen.obd.ObdDataSource
import com.autozen.obd.SimulatedObdDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ObdModule {
    @Binds
    @Singleton
    abstract fun bindObdDataSource(impl: SimulatedObdDataSource): ObdDataSource
}
