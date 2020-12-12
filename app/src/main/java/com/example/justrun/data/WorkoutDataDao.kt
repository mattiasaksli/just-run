package com.example.justrun.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.justrun.data.models.WorkoutData

@Dao
interface WorkoutDataDao {

    @Query("SELECT * FROM workoutdata WHERE id = :id")
    fun getWorkoutData(id: String): LiveData<WorkoutData>

    @Query("SELECT * FROM workoutdata")
    fun getAllWorkoutData(): LiveData<List<WorkoutData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workoutData: WorkoutData)
}