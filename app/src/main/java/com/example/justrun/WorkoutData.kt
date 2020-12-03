package com.example.justrun

import java.util.*

data class WorkoutData(
    // TODO: id for Room
    var startDateTime: Date,
    var endDatetime: Date,
    var distance: Float,
    var steps: Int,
    // TODO: map route data?
)
