package com.example.justrun.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.justrun.R
import com.example.justrun.data.models.WorkoutData
import kotlinx.android.synthetic.main.activity_workout_details.*

class WorkoutDetailsActivity : AppCompatActivity() {
    private lateinit var workout: WorkoutData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_details)

        workout = getWorkout(intent.getStringExtra("id").toString())
        workout.apply {
            tv_workout_start.text = startDateTime.toString()
            tv_workout_end.text = endDatetime.toString()
            tv_distance.text = distance.toString()
            tv_duration.text = (endDatetime - startDateTime).toString()
            tv_steps.text = steps.toString()
        }
    }

    private fun getWorkout(id: String) : WorkoutData {
        //TODO: get workout by id
        return WorkoutData(id, 1, 2, 3.0f, 4)
    }
}