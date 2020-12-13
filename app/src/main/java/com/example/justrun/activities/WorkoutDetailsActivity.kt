package com.example.justrun.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.justrun.R
import com.example.justrun.room.WorkoutDb
import kotlinx.android.synthetic.main.activity_workout_details.*
import java.text.SimpleDateFormat
import java.util.*


class WorkoutDetailsActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_WORKOUT_ID = "workoutId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_details)

        val id = intent.getIntExtra(EXTRA_WORKOUT_ID, -1)

        if (id > 0) getAndShowWorkoutDetails(id)

        button_back_workout.setOnClickListener {
            finish()
        }
    }

    private fun getAndShowWorkoutDetails(id: Int) {
        val workout = WorkoutDb.getInstance(this).workoutDataDao().getWorkoutData(id.toString())

        workout.apply {
            tv_workout_start.text = convertLongToTime(startDateTime)
            tv_workout_end.text = convertLongToTime(endDatetime)
            tv_duration.text = convertLongToDuration(endDatetime - startDateTime)
            tv_distance.text =
                if (distance <= 100) {
                    getString(R.string.distance_value_m, distance.toInt())
                } else {
                    getString(R.string.distance_value_km, distance.toDouble().div(1000))
                }
            tv_steps.text = workout.steps.toString()
        }

        btn_map_activity.setOnClickListener {
            val intent = Intent(baseContext, ReplayMapActivity::class.java)
            intent.putExtra("workout_id", id)
            startActivity(intent)
        }
    }

    private fun convertLongToDuration(duration: Long): String {
        val seconds = duration / 1000 % 60
        val minutes = duration / (1000 * 60) % 60
        val hours = duration / (1000 * 60 * 60) % 24

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }
}