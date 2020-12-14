package com.example.justrun.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justrun.R
import com.example.justrun.adapters.WorkoutsAdapter
import com.example.justrun.room.WorkoutData
import com.example.justrun.room.WorkoutDb
import com.example.justrun.viewmodels.WorkoutViewModel
import kotlinx.android.synthetic.main.activity_workouts.*

class WorkoutsActivity : AppCompatActivity() {

    private lateinit var model: WorkoutViewModel
    private lateinit var database: WorkoutDb
    private lateinit var workoutsAdapter: WorkoutsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        model = ViewModelProvider(this).get(WorkoutViewModel::class.java)
        setUpRecyclerView()
        setUpDatabase()

        button_back_workouts.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        model.refresh()
        workoutsAdapter.data = model.workouts.toTypedArray()
        workoutsAdapter.notifyDataSetChanged()
    }

    private fun setUpDatabase() {
        database = WorkoutDb.getInstance(this)
    }

    private fun setUpRecyclerView() {
        workoutsAdapter = WorkoutsAdapter(
            object : WorkoutsAdapter.WorkoutAdapterListener {
                override fun onWorkoutClick(workout: WorkoutData) {
                    openWorkoutDetails(workout)
                }
            })
        rv_workouts.adapter = workoutsAdapter
        rv_workouts.layoutManager = LinearLayoutManager(this)
    }

    private fun openWorkoutDetails(workout: WorkoutData) {
        val intent = Intent(this, WorkoutDetailsActivity::class.java)
        intent.putExtra(WorkoutDetailsActivity.EXTRA_WORKOUT_ID, workout.id)
        startActivity(intent)
    }

}