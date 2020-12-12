package com.example.justrun.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.justrun.R
import com.example.justrun.room.WorkoutDb
import kotlinx.android.synthetic.main.activity_workout_details.*

class WorkoutDetailsActivity : AppCompatActivity() {
    companion object { const val EXTRA_WORKOUT_ID = "workoutId" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_details)

        val id = intent.getIntExtra(EXTRA_WORKOUT_ID, -1)

        if (id > 0) getAndShowWorkoutDetails(id)
    }

    private fun getAndShowWorkoutDetails(id : Int){
        val workout = WorkoutDb.getInstance(this).workoutDataDao().getWorkoutData(id.toString())
        val start = workout.startDateTime
        val end = workout.endDatetime

        workout.apply {
            tv_workout_start.text = start.toString()
            tv_workout_end.text = end.toString()
            tv_duration.text = getDuration(start, end).toString()
            tv_distance.text = workout.distance.toString()
            tv_steps.text = workout.steps.toString()
        }
    }

    private fun getDuration(start : Long, end : Long) : Long{
        return end - start
    }
}