package com.example.justrun.room

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class Converters {

    var moshi = Moshi.Builder().build()
    var listOfTailLocation: Type = Types.newParameterizedType(
        MutableList::class.java,
        LatLng::class.java
    )

    var latLngJsonAdapter: JsonAdapter<List<LatLng>> = moshi.adapter(listOfTailLocation)

    @TypeConverter
    fun tailLocationsToString(tailLocations: List<LatLng>?): String = latLngJsonAdapter.toJson(tailLocations)

    @TypeConverter
    fun stringToTailLocations(stringListString: String): List<LatLng>? = latLngJsonAdapter.fromJson(stringListString)
}
