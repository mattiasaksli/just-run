package com.example.justrun.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
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
        supportActionBar?.show();

        button_newWorkout.setOnClickListener{ startWorkout() }
        button_pastWorkouts.setOnClickListener{ openWorkouts() }


    }

    private fun startWorkout(){
        Log.i(TAG, "Starting workout activity")

        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun openWorkouts() {
        Log.i(TAG, "Opening workouts list activity")

        val intent = Intent(this, WorkoutsActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        // won't go back to finished workout
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        return true
    }
}