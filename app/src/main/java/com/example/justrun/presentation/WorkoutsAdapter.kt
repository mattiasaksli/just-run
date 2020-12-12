package com.example.justrun.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.justrun.R
import com.example.justrun.data.models.WorkoutData
import kotlinx.android.synthetic.main.workouts_list_item.view.*

class WorkoutsAdapter(
    private var workoutsList: ArrayList<WorkoutData>,
    private var listener: WorkoutAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface WorkoutAdapterListener {
        fun onWorkoutClick(workout: WorkoutData, index: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.workouts_list_item, parent, false)

        return object : RecyclerView.ViewHolder(inflatedView) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val workout = workoutsList[position]
        holder.itemView.apply {
            tv_list_start_date.text = workout.startDateTime.toString()
            tv_list_distance.text = workout.distance.toString()
            tv_list_steps.text = workout.steps.toString()

            setOnClickListener {
                listener.onWorkoutClick(workout, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return workoutsList.size
    }
}