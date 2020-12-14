package com.example.justrun.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.justrun.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val activityIntent = Intent(this, MainActivity::class.java)

        Handler(Looper.getMainLooper()).postDelayed({

            startActivity(activityIntent)

        }, 2500)
    }
}