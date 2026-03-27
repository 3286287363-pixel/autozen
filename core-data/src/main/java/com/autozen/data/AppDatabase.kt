package com.autozen.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.autozen.data.trip.TripDao
import com.autozen.data.trip.TripEntity

@Database(entities = [TripEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao

    companion object {
        // v1 -> v2: no schema change yet, placeholder for future columns
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Reserved for future schema changes
            }
        }
    }
}
