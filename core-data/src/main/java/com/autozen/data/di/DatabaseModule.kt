package com.autozen.data.di

import android.content.Context
import androidx.room.Room
import com.autozen.data.AppDatabase
import com.autozen.data.trip.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "autozen.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()

    @Provides
    fun provideTripDao(db: AppDatabase): TripDao = db.tripDao()
}
