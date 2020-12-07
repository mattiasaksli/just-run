package com.example.justrun.presentation.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.example.justrun.data.models.WorkoutData
import com.example.justrun.data.repositories.WorkoutDataRepository


class WorkoutDataViewModel @ViewModelInject constructor(
    private val workoutDataRepository: WorkoutDataRepository
) : ViewModel() {

    val workoutData = workoutDataRepository.getWorkoutData()


}