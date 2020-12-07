package com.example.justrun.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.justrun.data.models.WorkoutData

@Dao
interface WorkoutDataDao {
    //needs new models or smt
    @Query("SELECT * FROM workoutdata")
    fun getWorkoutData(): LiveData<WorkoutData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workoutData: WorkoutData)
}