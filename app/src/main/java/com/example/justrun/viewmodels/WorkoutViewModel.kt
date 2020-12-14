package com.example.justrun.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.justrun.room.WorkoutData
import com.example.justrun.room.WorkoutDb


class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    var database: WorkoutDb
    var workouts: List<WorkoutData> = listOf()

    init {
        database = WorkoutDb.getInstance(application)
        refresh()
    }

    fun refresh() {
        workouts = database.workoutDataDao().getAllWorkouts()
    }
}