package com.example.justrun.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.justrun.R
import com.example.justrun.data.models.WorkoutData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val PERMISSION_REQ_CODE = 10
        val TAG: String = MainActivity::class.java.name
    }

    private var workouts = ArrayList<WorkoutData>()
    private lateinit var workoutsAdapter: WorkoutsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun setUpRecyclerView() {
        workoutsAdapter = WorkoutsAdapter(
            workouts,
            object : WorkoutsAdapter.WorkoutAdapterListener {
                override fun onWorkoutClick(workout: WorkoutData, index: Int) {
                    openWorkoutDetails(workout, index)
                }
            })
        rv_workouts.adapter = workoutsAdapter
        rv_workouts.layoutManager = LinearLayoutManager(this)
    }

    private fun openWorkoutDetails(workout: WorkoutData, index: Int) {
        //TODO("Not yet implemented")
    }
}