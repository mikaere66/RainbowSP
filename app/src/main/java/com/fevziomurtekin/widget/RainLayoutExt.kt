package com.fevziomurtekin.widget

import android.content.Context

object RainLayoutExt {

    fun getWidth(context: Context?): Int = context?.resources?.displayMetrics?.widthPixels.let {
        it ?: 0
    }

    fun getHeight(context: Context?): Int = context?.resources?.displayMetrics?.heightPixels.let {
        it ?: 0
    }
}