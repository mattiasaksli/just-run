package com.example.justrun.activities

import android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.justrun.fragments.SettingsFragment


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(R.id.content, SettingsFragment())
            .commit()

    }
}