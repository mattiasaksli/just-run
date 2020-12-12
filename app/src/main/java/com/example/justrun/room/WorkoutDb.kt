package com.example.justrun.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WorkoutData::class], version = 2, exportSchema = false)
abstract class WorkoutDb : RoomDatabase() {

    abstract fun workoutDataDao(): WorkoutDAO

    companion object {
        private lateinit var dbInstance: WorkoutDb

        @Synchronized fun getInstance(context: Context): WorkoutDb {
            if (!this::dbInstance.isInitialized) {
                dbInstance = Room.databaseBuilder(
                    context, WorkoutDb::class.java, "myWorkouts"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }

            return dbInstance
        }
    }

}