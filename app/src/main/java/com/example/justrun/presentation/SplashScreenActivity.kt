package com.example.justrun.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import com.example.justrun.R

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val mapsActivityIntent = Intent(this, MainActivity::class.java)

        Handler(Looper.getMainLooper()).postDelayed({

            startActivity(mapsActivityIntent)

        }, 2500)
    }
}