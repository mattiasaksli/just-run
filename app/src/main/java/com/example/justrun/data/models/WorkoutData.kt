package com.example.justrun.data.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "workoutdata")
data class WorkoutData(
    // TODO: id for Room
    @NonNull
    @PrimaryKey
    var id: String,
    var startDateTime: Long,
    var endDatetime: Long,
    var distance: Float,
    var steps: Int,
    // TODO: map route data?
)
