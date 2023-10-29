package com.michaelrmossman.kotlin.rainbows.utils

import android.content.res.Resources
import android.os.Build
import android.util.Log
import com.michaelrmossman.kotlin.rainbows.R
import com.michaelrmossman.kotlin.rainbows.RainbowsApp
import com.michaelrmossman.kotlin.rainbows.RainbowsApp.Companion.alertDialog
import com.michaelrmossman.kotlin.rainbows.RainbowsApp.Companion.snackbar

/**
 * Helper functions used throughout the app, relating directly to OTHER functions/values
 */
object SysUtils {

    fun checkForAlertDialog() {
        if (alertDialog != null) {
            alertDialog?.let {
                if (it.isShowing) {
                    it.dismiss()
                }
            }
        }
    }

    fun checkForSnackbar() {
        if (snackbar != null) {
            snackbar?.let {
                if (it.isShown) {
                    it.dismiss()
                }
            }
        }
    }

//    fun getAppContext(): Context {
//        return RainbowsApp.instance.applicationContext
//    }

//    private fun getOrientation(): Int {
//        return getResources().configuration.orientation
//    }

    fun getPathToBrightness(
        ledNum: Int,
        rgbValue: String
    ) : String {
        return String.format(
            getResources().getString(R.string.sys_path_brightness),
            ledNum, rgbValue
        )
    }

    fun getPathToLedCurrent(
        ledNum: Int,
        rgbValue: String
    ) : String {
        return String.format(
            getResources().getString(R.string.sys_path_led_current),
            ledNum, rgbValue
        )
    }

    fun getResources(): Resources {
        return RainbowsApp.instance.resources
    }

    fun isEmulator(): Boolean {
        if (debug) {
            Log.d("DEVICE BOARD", Build.BOARD)
            Log.d("DEVICE BRAND", Build.BRAND)
            Log.d("DEVICE MANUF", Build.MANUFACTURER)
            Log.d("DEVICE DEVICE", Build.DEVICE)
            Log.d("DEVICE MODEL", Build.MODEL)
        }

        return (Build.MANUFACTURER.contains("Genymotion")
             || Build.MODEL.contains("google_sdk")
             || Build.MODEL.lowercase().contains("droid4x")
             || Build.MODEL.contains("Emulator")
             || Build.MODEL.contains("Android SDK built for x86")
             || Build.HARDWARE == "goldfish"
             || Build.HARDWARE == "vbox86"
             || Build.HARDWARE.lowercase().contains("nox")
             || Build.FINGERPRINT.startsWith("generic")
             || Build.PRODUCT == "sdk"
             || Build.PRODUCT == "google_sdk"
             || Build.PRODUCT == "sdk_x86"
             || Build.PRODUCT == "vbox86p"
             || Build.PRODUCT.lowercase().contains("nox")
             || Build.BOARD.lowercase().contains("nox")
             || Build.BRAND.startsWith("generic")
             || Build.DEVICE.startsWith("generic")) // Last two were AND together
    }

//    fun isPortrait(): Boolean {
//        return getOrientation() == Configuration.ORIENTATION_PORTRAIT
//    }
}
