package com.example.justrun.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.justrun.data.models.WorkoutData

@Database(entities = [WorkoutData::class], version = 1, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {

    abstract fun workoutDataDao(): WorkoutDataDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "rundatabase")
                .fallbackToDestructiveMigration()
                .build()
    }

}