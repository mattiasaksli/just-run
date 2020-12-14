package com.example.justrun.activities

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.example.justrun.R
import com.example.justrun.fragments.SettingsFragment
import com.example.justrun.room.WorkoutDb
import kotlinx.android.synthetic.main.activity_preferences.*


class SettingsActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener, SharedPreferences.Editor {

    private lateinit var database : WorkoutDb
    private lateinit var preferences : SharedPreferences

    companion object {
        const val TAG = "SettingsActivity"
        var SWITCH_DATA = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        database = WorkoutDb.getInstance(this)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        SWITCH_DATA = preferences.getBoolean("switch_data", false)

        button_back_preferences.setOnClickListener {
            finish()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_frame, SettingsFragment())
            .commit()
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "Registering preference change listener")
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "Unregistering preference change listener")
        preferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val value = sharedPreferences?.getBoolean(key, false)
        Log.i(TAG, "Preference $key changed to ${value.toString()}")

        if (key == "switch_data") {
            SWITCH_DATA = value!!
        }
        else if (key == "clear_cache") {
            val workouts = database.workoutDataDao().getAllWorkouts()

            if (workouts.isNotEmpty())
                workouts.forEach{
                    database.workoutDataDao().deleteWorkout(it)
                }

            val editor = sharedPreferences?.edit()
            editor?.putBoolean(key, false)
            editor?.apply()
        }
    }

    override fun putString(key: String?, value: String?): SharedPreferences.Editor {
        return this
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
        return this
    }

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
        return this
    }

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
        return this
    }

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
        return this
    }

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
        return this
    }

    override fun remove(key: String?): SharedPreferences.Editor {
        return this
    }

    override fun clear(): SharedPreferences.Editor {
        return this
    }

    override fun commit(): Boolean {
        return true
    }

    override fun apply() {
    }
}
