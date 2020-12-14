package com.example.justrun.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.justrun.R
import com.example.justrun.room.WorkoutData
import com.example.justrun.room.WorkoutDb
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.replay_map_overlay.*
import java.text.SimpleDateFormat
import java.util.*

class ReplayMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var db: WorkoutDb
    private lateinit var workout: WorkoutData
    private lateinit var mMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_replay_map)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.replay_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val workoutId = intent.getIntExtra("workout_id", 0)
        setUpDatabase(workoutId.toString())

        button_back_replay.setOnClickListener {
            finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        displayWorkoutOnMap()
    }

    private fun setUpDatabase(workoutId: String) {
        db = WorkoutDb.getInstance(this)
        workout = db.workoutDataDao().getWorkoutData(workoutId)
    }

    private fun displayWorkoutOnMap() {
        val polyLineOptions = PolylineOptions()
        val workoutLocations = workout.locations!!

        for (latLng in workoutLocations) {
            mMap.addPolyline(polyLineOptions.add(latLng))
        }


        updateCameraPosition(workoutLocations[0])
        replay_distance_text.text = formatDistance()
        replay_time_text.text = formatTime()
        replay_steps_text.text = getString(R.string.steps_with_value, workout.steps)
    }

    private fun formatDistance(): String {
        val distance = workout.distance
        return if (distance <= 100) {
            getString(R.string.distance_m, distance)
        } else {
            val distanceKilometers = distance.toDouble().div(1000)
            getString(R.string.distance_km, distanceKilometers)
        }
    }

    private fun formatTime(): String {
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

    private fun updateCameraPosition(location: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(location) // Sets the center of the map to location user
            .zoom(18f) // Sets the zoom
            .bearing(10f) // Sets the orientation of the camera to east
            .tilt(15f) // Sets the tilt of the camera to 30 degrees
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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