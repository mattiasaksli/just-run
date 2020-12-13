package com.example.justrun.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.justrun.R
import com.example.justrun.room.WorkoutData
import com.example.justrun.room.WorkoutDb
import com.example.justrun.utils.ForegroundService
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.ktx.utils.sphericalDistance
import kotlinx.android.synthetic.main.map_overlay.*
import kotlin.math.roundToInt


//@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOCATION_REQUEST_CODE = 101
    private val LOCATION_REQUEST_INTERVAL: Long = 5000
    private var locationPermissionGranted = false
    private var locationLatLngList: MutableList<LatLng> = mutableListOf()
    private var counter = 0

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var mMap: GoogleMap
    private lateinit var workoutData: WorkoutData
    private lateinit var db: WorkoutDb


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        startService(Intent(this, ForegroundService::class.java))

        startWorkOut()
        setUpDatabase()
        setUpLocationRequest()
        setUpLocationCallback()
        setUpOverlay()
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, ForegroundService::class.java))
    }

    override fun onPause() {
        super.onPause()
        Log.i("maps", "paused")

    }

    override fun onResume() {
        super.onResume()
        Log.i("maps", "resumed")
        if (::mMap.isInitialized) updateMapWithLocations()
    }

    private fun setUpOverlay() {

        startTimeCounter()

        button_finish.setOnClickListener {
            finishWorkout()
        }
    }

    private fun finishWorkout() {
        val finishTime = System.currentTimeMillis()
        val distance = calculateDistance()

        workoutData.distance = distance
        workoutData.endDatetime = finishTime
        workoutData.locations = locationLatLngList

        Log.i("workoutData", workoutData.toString())
        db.workoutDataDao().insert(workoutData)

        val activityIntent = Intent(this, MainActivity::class.java)
        startActivity(activityIntent)
        finish()


    }

    private fun calculateDistance(): Float {
        var distance = 0.0
        var previousLatLng: LatLng? = null
        for (latLng in locationLatLngList) {
            if (previousLatLng != null) {
                val temporaryDistance = previousLatLng.sphericalDistance(latLng)
                if (temporaryDistance > 1) distance += temporaryDistance
            }
            previousLatLng = latLng
        }
        return distance.toFloat()
    }

    private fun startWorkOut() {
        val startTime = System.currentTimeMillis()
        Log.i("map", startTime.toString())
        workoutData = WorkoutData(startTime)
    }

    private fun setUpLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = LOCATION_REQUEST_INTERVAL
    }

    private fun setUpLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.i(
                        "location",
                        location.latitude.toString() + " " + location.longitude.toString()
                    )
                    val locationLatLng = LatLng(location.latitude, location.longitude)
                    val bearing = location.bearing
                    locationLatLngList.add(locationLatLng)
                    updateMapWithLocations()
                    if (locationLatLngList.size < 2) updateCameraPosition(locationLatLng, bearing)
                }
            }
        }
    }

    private fun updateMapWithLocations() {
        val polyLineOptions = PolylineOptions()
        mMap.clear()
        for (latLng in locationLatLngList) {
            mMap.addPolyline(polyLineOptions.add(latLng))
        }
        updateOverLayDistance()


    }

    private fun startTimeCounter() {
        object : CountDownTimer(10000000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateOverLayTimer(counter)
                counter++
            }
            override fun onFinish() {
                Log.i("timer", "timer finished")
            }
        }.start()
    }

    private fun updateOverLayTimer(counter: Int) {
        if (counter < 60) {
            val timeElapsed = counter
            text_time.text = "Time elapsed: $timeElapsed s"
        }
        if (counter in 60..3599) {
            val timeElapsed = counter/60
            text_time.text = "Time elapsed: $timeElapsed m"
        }
        if (counter in 3600..86400) {
            val numberOfHours = (counter % 86400 ) / 3600
            val numberOfMinutes = ((counter % 86400 ) % 3600 ) / 60

            text_time.text = "Time elapsed: $numberOfHours h $numberOfMinutes m"
        }
    }

    private fun updateOverLayDistance() {
        val distance = calculateDistance().roundToInt()
        if (distance <= 100) {
            text_distance.text = "Distance ran: $distance m"
        }
        else {
            val distanceKilometers = distance.toDouble().div(1000)
            text_distance.text = "Distance ran: $distanceKilometers km"
        }
    }

    private fun updateCameraPosition(location: LatLng, bearing: Float) {
        val cameraPosition = CameraPosition.Builder()
            .target(location) // Sets the center of the map to location user
            .zoom(18f) // Sets the zoom
            .bearing(bearing) // Sets the orientation of the camera to east
            .tilt(15f) // Sets the tilt of the camera to 30 degrees
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLocationPermission()
        setLocationUI()
        startLocationUpdates()

    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun getLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    private fun setLocationUI() {
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings?.isMyLocationButtonEnabled = false
                getLocationPermission()
            }


        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun setUpDatabase() {
        db = WorkoutDb.getInstance(this)
        /*
        val workout = WorkoutData(0, 10L, 11L, 100F, 100000)
        db.workoutDataDao().insert(workout)
         */
        db.workoutDataDao().getAllWorkouts().forEach {
            Log.i("maps", it.toString())
        }
    }

}