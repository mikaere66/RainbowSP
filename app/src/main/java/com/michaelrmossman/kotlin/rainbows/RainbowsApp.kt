package com.michaelrmossman.kotlin.rainbows

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.michaelrmossman.kotlin.rainbows.utils.SHARED_PREFERENCES_KEY
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RainbowsApp: Application() {

    companion object {
        var actionMenu: Menu? = null

        var alertDialog: AlertDialog? = null

        lateinit var instance: RainbowsApp

        val manufacturer: String by lazy {
            Build.MANUFACTURER
        }

        val sharedPrefs: SharedPreferences by lazy {
            instance.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
        }

        var snackbar: Snackbar? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
