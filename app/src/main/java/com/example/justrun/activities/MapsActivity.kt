package com.example.justrun.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
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
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private var sensorManager: SensorManager? = null
    private val LOCATION_REQUEST_CODE = 101
    private val LOCATION_REQUEST_INTERVAL: Long = 5000
    private var locationPermissionGranted = false
    private var locationLatLngList: MutableList<LatLng> = mutableListOf()
    private var counter = 0
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var currentSteps = 0
    private var running = false
    private val polyLineOptions = PolylineOptions()


    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var mMap: GoogleMap
    private lateinit var workoutData: WorkoutData
    private lateinit var db: WorkoutDb
    private lateinit var timer: CountDownTimer



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        startService(Intent(this, ForegroundService::class.java))
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        startWorkOut()
        setUpDatabase()
        setUpLocationRequest()
        setUpLocationCallback()
        setUpOverlay()
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, ForegroundService::class.java))
        sensorManager?.unregisterListener(this)
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        timer.onFinish()

        Log.i("maps", "destroyed")



    }

    override fun onPause() {
        super.onPause()
        Log.i("maps", "paused")

    }

    override fun onResume() {
        super.onResume()
        Log.i("maps", "resumed")
        if (::mMap.isInitialized)  updateMapWithLocations()
        setUpStepSensor()

    }




    private fun setUpStepSensor() {
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Log.e("stepsensor", "can't track steps")
        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
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
        workoutData.steps = currentSteps.toInt()

        stopService(Intent(this, ForegroundService::class.java))
        sensorManager?.unregisterListener(this)
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        timer.onFinish()

        Log.i("workoutData", workoutData.toString())
        if (SettingsActivity.SWITCH_DATA) db.workoutDataDao().insert(workoutData)
        val returnIntent = intent
        returnIntent.putExtra("maps", "finished")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()


    }

    private fun calculateDistance(): Float {
        var distance = 0.0
        var previousLatLng: LatLng? = null
        for (latLng in locationLatLngList) {
            if (previousLatLng != null) {
                val temporaryDistance = previousLatLng.sphericalDistance(latLng)
                if (temporaryDistance > 2) distance += temporaryDistance
            }
            previousLatLng = latLng
        }
        return distance.toFloat()
    }

    private fun startWorkOut() {
        val startTime = System.currentTimeMillis()
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

        mMap.clear()
        for (latLng in locationLatLngList) {
            mMap.addPolyline(polyLineOptions.add(latLng))
        }
        updateOverLayDistance()


    }

    private fun startTimeCounter() {
        timer = object : CountDownTimer(10000000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateOverLayTimer(counter)
                counter++
            }

            override fun onFinish() {
                Log.i("timer", "timer finished")
                timer.cancel()
            }

        }.start()


    }

    private fun updateOverLayTimer(counter: Int) {
        if (counter < 60) {
            val timeElapsed = counter
            text_time.text = "Time elapsed: $timeElapsed s"
        }
        if (counter in 60..3599) {
            val timeElapsed = counter / 60
            text_time.text = "Time elapsed: $timeElapsed m"
        }
        if (counter in 3600..86400) {
            val numberOfHours = (counter % 86400) / 3600
            val numberOfMinutes = ((counter % 86400) % 3600) / 60

            text_time.text = "Time elapsed: $numberOfHours h $numberOfMinutes m"
        }
    }

    private fun updateOverLayDistance() {
        val distance = calculateDistance().roundToInt()
        if (distance <= 100) {
            text_distance.text = "Distance ran: $distance m"
        } else {
            val distanceKilometers = distance.toDouble().div(1000)
            text_distance.text = "Distance ran: $distanceKilometers km"
        }

        text_steps.text = "Steps: $currentSteps"
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
        getStepsPermission()
        setLocationUI()
        startLocationUpdates()

    }

    private fun getStepsPermission() {


            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACTIVITY_RECOGNITION,
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissionGranted = true
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                        LOCATION_REQUEST_CODE
                    )
                }
            }

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
                Manifest.permission.ACCESS_FINE_LOCATION,
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
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        totalSteps = event!!.values[0]

        if (!running) {
            previousTotalSteps = totalSteps
            running = true
        }
        currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

    }


}