package com.example.justrun.room

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workoutdata")
data class WorkoutData(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var startDateTime: Long,
    var endDatetime: Long,
    var distance: Float,
    var steps: Int,
    // TODO: map route data?
) {
    override fun toString(): String {
        return "Workout : {id: $id, start: $startDateTime, end: $endDatetime, distance: $distance, steps: $steps}"
    }
}

