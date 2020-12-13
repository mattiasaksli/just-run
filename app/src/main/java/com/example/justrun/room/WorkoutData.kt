package com.example.justrun.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "workoutdata")
data class WorkoutData(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var startDateTime: Long,
    var endDatetime: Long,
    var distance: Float,
    var steps: Int,
    var locations: List<LatLng>?
) {
    constructor(startDateTime: Long) : this(
        0,
        startDateTime,
        0,
        0.0f,
        0,
        null
    ) //id 0 is safe, gets overwritten when writing to DB

    override fun toString(): String {
        return "Workout : {id: $id, start: $startDateTime, end: $endDatetime, distance: $distance, steps: $steps}"
    }
}

