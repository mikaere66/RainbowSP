package com.michaelrmossman.kotlin.rainbows.utils

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.michaelrmossman.kotlin.rainbows.RainbowsApp.Companion.snackbar
import com.nex3z.togglebuttongroup.button.LabelToggle
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup

//fun MaterialButtonToggleGroup.getSelectedIndex(checkedId: Int): Int {
//    val button = findViewById<Button>(checkedId)
//    return indexOfChild(button)
//}

fun SingleSelectToggleGroup.getSelectedIndex(checkedId: Int): Int {
    val button = findViewById<LabelToggle>(checkedId)
    return indexOfChild(button)
}

// Usage : e.g. binding.root.showSnackbar(R.string.whatever, length)
fun View.showSnackbar(
    @StringRes stringId: Int,
    length: Int
) {
    snackbar = Snackbar.make(this, stringId, length)
    snackbar?.show()
}
