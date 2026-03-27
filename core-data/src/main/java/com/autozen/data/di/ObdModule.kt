package com.autozen.data.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.SharedPreferences
import com.autozen.obd.BluetoothObdDataSource
import com.autozen.obd.ObdDataSource
import com.autozen.obd.SimulatedObdDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ObdModule {

    @Provides
    @Singleton
    fun provideObdPrefs(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("obd_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideObdDataSource(
        simulated: SimulatedObdDataSource,
        bluetoothAdapter: BluetoothAdapter?,
        prefs: SharedPreferences
    ): ObdDataSource {
        val useReal = prefs.getBoolean("use_real_obd", false)
        return if (useReal && bluetoothAdapter != null) {
            BluetoothObdDataSource(bluetoothAdapter)
        } else {
            simulated
        }
    }
}
