package com.example.justrun.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justrun.R
import com.example.justrun.adapters.WorkoutsAdapter
import com.example.justrun.viewmodels.WorkoutViewModel
import com.example.justrun.room.WorkoutDb
import com.example.justrun.room.WorkoutData
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
    }

    private fun startWorkout() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
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