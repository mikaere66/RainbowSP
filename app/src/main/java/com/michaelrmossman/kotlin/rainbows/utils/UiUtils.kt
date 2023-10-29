package com.michaelrmossman.kotlin.rainbows.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.text.Spanned
import android.widget.Toast
import androidx.annotation.StringRes
import com.michaelrmossman.kotlin.rainbows.R
import com.michaelrmossman.kotlin.rainbows.utils.SysUtils.getResources
import spencerstudios.com.fab_toast.FabToast

/**
 * Helper functions used throughout the app, relating directly to the User Interface
 */
object UiUtils {

    fun fancyToast(
        context: Context, format: Int, @StringRes
        stringId: Int, coroutine: Boolean = false
    ) {
        fancyToast(
            context, format,
            context.getString(stringId), coroutine
        )
    }

    fun fancyToast(
        context: Context, format: Int, string:
        String, coroutine: Boolean = false
    ) {
        // Format :   Success, Information, Error, Warning (1-4)
        // Duration : Short = 0, Long = 1
        // Position : Default, Center, Top (1-3)
        val duration =
            when (format) {
                4 -> Toast.LENGTH_LONG
                else -> Toast.LENGTH_SHORT
            }

        if (coroutine) { // If called by a suspend function
            Handler(Looper.getMainLooper()).post {
                FabToast.makeText(context,
                    string, duration, format,2).show()
            }

        } else FabToast.makeText(context,
            string, duration, format,2).show()
    }

    fun getHelpSpannedFromStringRes(
        @StringRes boldStringId: Int,
        @StringRes helpStingId: Int,
        bulleted: Boolean = false
    ) : Spanned {
        return getHelpSpanned(
            getResources().getString(boldStringId),
            getResources().getString(helpStingId),
            bulleted
        )
    }

    fun getHelpSpanned(
        boldString: String,
        helpSting: String,
        bulleted: Boolean = false
    ) : Spanned {
        if (helpSting.indexOf(boldString) == -1) {
            // Prevent index out of range
            return getSpanned(helpSting)
        }

        val bulletedString = if (bulleted) {
            val bulletPoint = getResources().getString(
                R.string.bullet_point
            )
            "$bulletPoint$helpSting"
        } else helpSting

        val finalString = if (bulletedString.contains("\n")) {
            bulletedString.replace("\n","<BR>")
        } else bulletedString

        val index = finalString.indexOf(boldString)

        val html = finalString.substring(0, index) +
            "<B>$boldString</B>" +
            finalString.substring(
                index + boldString.length, finalString.length
            )

        return getSpanned(html)
    }

    @Suppress("Deprecation")   // Html.fromHtml(string)
    @SuppressLint("InlinedApi") // Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
    fun getSpanned(html: String): Spanned {
        return when (Build.VERSION.SDK_INT) {
            in 1..23 -> Html.fromHtml(html)
            else -> Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        }
    }
}
