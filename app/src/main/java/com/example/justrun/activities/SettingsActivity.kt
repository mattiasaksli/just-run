package com.example.justrun.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.justrun.R
import com.example.justrun.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_preferences.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        button_back_preferences.setOnClickListener {
            finish()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_frame, SettingsFragment())
            .commit()

    }
}