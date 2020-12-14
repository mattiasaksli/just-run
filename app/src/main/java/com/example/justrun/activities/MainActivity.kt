package com.example.justrun.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.justrun.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQ_CODE = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestedPermissions: Array<String> =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }

        if (hasPermissions(requestedPermissions)) {
            button_newWorkout.setOnClickListener { startWorkout() }
            button_pastWorkouts.setOnClickListener { openWorkouts() }
            button_settings.setOnClickListener { openSettings() }
        }
        else {
            ActivityCompat.requestPermissions(this, requestedPermissions, PERMISSION_REQ_CODE)
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(applicationContext, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQ_CODE) {
            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allPermissionsGranted) {
                button_newWorkout.setOnClickListener { startWorkout() }
                button_pastWorkouts.setOnClickListener { openWorkouts() }
                button_settings.setOnClickListener { openSettings() }
            } else {
                button_newWorkout.isEnabled = false
                button_pastWorkouts.isEnabled = false
                button_settings.isEnabled = false
                Toast.makeText(
                    this,
                    "Please grant Location and Physical Activity permissions to use the app.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun startWorkout() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivityForResult(intent, 1)
    }

    private fun openWorkouts() {
        val intent = Intent(this, WorkoutsActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        // won't go back to finished workout
    }
}