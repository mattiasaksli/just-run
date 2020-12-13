package com.example.justrun.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.justrun.R
import com.example.justrun.room.WorkoutData
import kotlinx.android.synthetic.main.workouts_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class WorkoutsAdapter(
    private var listener: WorkoutAdapterListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface WorkoutAdapterListener {
        fun onWorkoutClick(workout: WorkoutData)
    }

    var data = arrayOf<WorkoutData>()
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
            tv_list_start_date.text = convertLongToTime(workout.startDateTime)
            tv_list_distance.text =
                if (workout.distance <= 100) {
                    this.context.getString(R.string.distance_value_m, workout.distance.toInt())
                } else {
                    this.context.getString(R.string.distance_value_km, workout.distance.toDouble().div(1000))
                }
            tv_list_steps.text = workout.steps.toString()

            setOnClickListener {
                listener.onWorkoutClick(workout)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        return format.format(date)
    }
}