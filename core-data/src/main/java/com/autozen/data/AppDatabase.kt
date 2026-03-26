package com.autozen.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.autozen.data.trip.TripDao
import com.autozen.data.trip.TripEntity

@Database(entities = [TripEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
}
