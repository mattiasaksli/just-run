package com.example.justrun.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkoutDAO {

    @Query("SELECT * FROM workoutdata WHERE id = :id")
    fun getWorkoutData(id: String): WorkoutData

    @Query("SELECT * FROM workoutdata")
    fun getAllWorkouts(): List<WorkoutData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(workoutData: WorkoutData)

    @Delete
    fun deleteWorkout(workoutData: WorkoutData)
}