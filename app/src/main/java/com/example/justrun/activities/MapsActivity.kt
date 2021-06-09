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
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.ktx.utils.sphericalDistance
import kotlinx.android.synthetic.main.map_overlay.*
import kotlin.math.roundToInt


//@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private var sensorManager: SensorManager? = null
    private val LOCATION_REQUEST_CODE = 101
    private var locationPermissionGranted = false
    private var locationLatLngList: MutableList<LatLng> = mutableListOf()
    private var latLngBuffer: MutableList<LatLng> = mutableListOf()
    private var totalDistance = 0
    private var counter: Int = 0
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
    private lateinit var mainHandler: Handler

    private val updateMapTask = object : Runnable {
        override fun run() {
            updateMapWithLocations()
            mainHandler.postDelayed(this, 4000)
        }
    }

    companion object {
        val TAG: String = MapsActivity::class.java.name
        var LOCATION_REQUEST_INTERVAL: Long = 1000L
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        startService(Intent(this, ForegroundService::class.java))
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mainHandler = Handler(Looper.getMainLooper())

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        LOCATION_REQUEST_INTERVAL = if (preferences.contains("interval_preference"))
            preferences.all.getValue("interval_preference").toString().toLong()
        else {
            val editor = preferences.edit()
            editor.putString("interval_preference", "1000")
            editor.apply()
            1000L
        }

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
        mainHandler.removeCallbacks(updateMapTask)

        Log.i(TAG, "Activity destroyed")


    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "Activity paused")

    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "Activity resumed")
        if (::mMap.isInitialized) updateMapWithLocations()
        setUpStepSensor()

    }


    private fun setUpStepSensor() {
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Log.e(TAG, "StepSensor: can't track steps")
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
        val distance = totalDistance + calculateLastMovedDistance()

        workoutData.distance = distance
        workoutData.endDatetime = finishTime
        workoutData.locations = locationLatLngList
        workoutData.steps = currentSteps

        stopService(Intent(this, ForegroundService::class.java))
        sensorManager?.unregisterListener(this)
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        timer.onFinish()
        mainHandler.removeCallbacks(updateMapTask)

        if (SettingsActivity.SWITCH_DATA) db.workoutDataDao().insert(workoutData)
        Log.i("settings value", SettingsActivity.SWITCH_DATA.toString())
        val returnIntent = intent
        returnIntent.putExtra(TAG, "Workout finished: $workoutData")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun calculateLastMovedDistance(): Float {
        val locationsSize = locationLatLngList.size
        val previousLatLng: LatLng = locationLatLngList[locationsSize - 2]
        val currentLatLng: LatLng = locationLatLngList[locationsSize - 1]

        return previousLatLng.sphericalDistance(currentLatLng).toFloat()
    }

    private fun startWorkOut() {
        val startTime = System.currentTimeMillis()
        workoutData = WorkoutData(startTime)

    }

    private fun setUpLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = LOCATION_REQUEST_INTERVAL
        Log.i(TAG, "Location request interval: $LOCATION_REQUEST_INTERVAL")
    }

    private fun setUpLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                val location = locationResult.lastLocation
                Log.i(TAG, "Location: ${location.latitude} ${location.longitude}")

                val locationLatLng = LatLng(location.latitude, location.longitude)
                val bearing = location.bearing

                latLngBuffer.add(locationLatLng)
                if (latLngBuffer.size >= 3) {
                    val latLngBounds = LatLngBounds.builder()
                    for (latLng in latLngBuffer) {
                        latLngBounds.include(latLng)
                    }
                    locationLatLngList.add(latLngBounds.build().center)

                    latLngBuffer.clear()
                }

                if (locationLatLngList.size < 2) updateCameraPosition(locationLatLng, bearing)
            }
        }
    }

    private fun updateMapWithLocations() {
        val locationsSize = locationLatLngList.size
        if (locationsSize > 1) {
            polyLineOptions.add(
                locationLatLngList[locationsSize - 1],
                locationLatLngList[locationsSize - 2]
            )
            mMap.addPolyline(polyLineOptions)
            updateOverLayDistance()
        }
    }

    private fun startTimeCounter() {
        timer = object : CountDownTimer(10000000000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateOverLayTimer()
                counter++
            }

            override fun onFinish() {
                Log.i(TAG, "Timer finished")
                timer.cancel()
            }

        }.start()
    }

    private fun updateOverLayTimer() {
        val seconds = counter % 60

        when {
            counter < 60 -> {
                text_time.text = getString(R.string.time_elapsed_s, seconds)
            }
            counter in 60..3599 -> {
                val minutes = (counter / 60) % 60
                text_time.text = getString(R.string.time_elapsed_m_s, minutes, seconds)
            }
            counter in 3600..86400 -> {
                val totalMinutes = counter / 60
                val minutes = totalMinutes % 60
                val hours = totalMinutes / 60

                text_time.text = getString(R.string.time_elapsed_h_m_s, hours, minutes, seconds)
            }
            else -> {
                // If time elapsed is more than 24 hours, the counter resets to 0.
                // We assume that no workout lasts more than 24 hours.
                counter = 0
                text_time.text = getString(R.string.time_elapsed_s, seconds)
            }
        }
    }

    private fun updateOverLayDistance() {
        totalDistance += calculateLastMovedDistance().roundToInt()
        if (totalDistance <= 100) {
            text_distance.text = getString(R.string.distance_m, totalDistance)
        } else {
            val distanceKilometers = totalDistance.toDouble().div(1000)
            text_distance.text = getString(R.string.distance_km, distanceKilometers)
        }

        text_steps.text = getString(R.string.steps_with_value, currentSteps)
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
            return
        }

        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        mainHandler.post(updateMapTask)
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