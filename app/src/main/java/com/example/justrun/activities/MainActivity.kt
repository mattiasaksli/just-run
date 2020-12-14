package com.example.justrun.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.justrun.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQ_CODE = 10
        val TAG: String = MainActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button_newWorkout.setOnClickListener { startWorkout() }
        button_pastWorkouts.setOnClickListener { openWorkouts() }
        button_settings.setOnClickListener { openSettings() }

    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun startWorkout() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivityForResult(intent, 1)
    }

    private fun openWorkouts() {
        val intent = Intent(this, WorkoutsActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        // won't go back to finished workout
    }
}