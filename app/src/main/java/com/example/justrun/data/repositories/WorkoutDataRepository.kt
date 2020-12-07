package com.example.justrun.data.repositories

import androidx.lifecycle.distinctUntilChanged
import com.example.justrun.data.WorkoutDataDao
import com.example.justrun.data.models.WorkoutData
import javax.inject.Inject

class WorkoutDataRepository @Inject constructor(
    private val workoutDataDao: WorkoutDataDao) {

    fun getWorkoutData(): WorkoutData {
        //pole kindel kas see praegu töötab
       return workoutDataDao.getWorkoutData().value!!
    }

}