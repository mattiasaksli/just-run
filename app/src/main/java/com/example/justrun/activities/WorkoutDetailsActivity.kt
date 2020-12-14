package com.example.justrun.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.justrun.R
import com.example.justrun.room.WorkoutData
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
            tv_duration.text = formatTime(this)
            tv_distance.text = formatDistance(this)
            tv_steps.text = getString(R.string.steps_with_value, workout.steps)
        }

        btn_map_activity.setOnClickListener {
            val intent = Intent(baseContext, ReplayMapActivity::class.java)
            intent.putExtra("workout_id", id)
            startActivity(intent)
        }
    }

    private fun formatDistance(workout: WorkoutData): String {
        val distance = workout.distance
        return if (distance <= 100) {
            getString(R.string.distance_m, distance)
        } else {
            val distanceKilometers = distance.toDouble().div(1000)
            getString(R.string.distance_km, distanceKilometers)
        }
    }

    private fun formatTime(workout: WorkoutData): String {
        val totalSeconds: Int = ((workout.endDatetime - workout.startDateTime) / 1000).toInt()

        val seconds = totalSeconds % 60

        when {
            totalSeconds < 60 -> {
                return getString(R.string.time_elapsed_s, seconds)
            }
            totalSeconds in 60..3599 -> {
                val minutes = (totalSeconds / 60) % 60
                return getString(R.string.time_elapsed_m_s, minutes, seconds)
            }
            totalSeconds in 3600..86400 -> {
                val totalMinutes = totalSeconds / 60
                val minutes = totalMinutes % 60
                val hours = totalMinutes / 60

                return getString(R.string.time_elapsed_h_m_s, hours, minutes, seconds)
            }
            else -> {
                return getString(R.string.time_elapsed_s, totalSeconds % 84600)
            }
        }
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }
}