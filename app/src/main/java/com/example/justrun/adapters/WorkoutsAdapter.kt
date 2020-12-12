package com.example.justrun.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.justrun.R
import com.example.justrun.room.WorkoutData
import kotlinx.android.synthetic.main.workouts_list_item.view.*

class WorkoutsAdapter(
    private var listener: WorkoutAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface WorkoutAdapterListener {
        fun onWorkoutClick(workout: WorkoutData)
    }

    var data =  arrayOf<WorkoutData>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflatedView =
            LayoutInflater.from(parent.context).inflate(R.layout.workouts_list_item, parent, false)

        return object : RecyclerView.ViewHolder(inflatedView) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val workout = data[position]
        holder.itemView.apply {
            tv_list_start_date.text = workout.startDateTime.toString()
            tv_list_distance.text = workout.distance.toString()
            tv_list_steps.text = workout.steps.toString()

            setOnClickListener {
                listener.onWorkoutClick(workout)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}