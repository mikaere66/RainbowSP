package com.michaelrmossman.kotlin.rainbows.fragments

import android.text.Html
import androidx.core.view.isVisible
import coil.load
import com.michaelrmossman.kotlin.rainbows.R
import com.michaelrmossman.kotlin.rainbows.RainbowsApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.rainbows.activities.IntroActivity
import com.michaelrmossman.kotlin.rainbows.databinding.FragmentIntroFirstBinding
import com.michaelrmossman.kotlin.rainbows.utils.APP_FIRST_RUN_PREF
import com.michaelrmossman.kotlin.rainbows.utils.UiUtils.getHelpSpannedFromStringRes

class FirstFragment: BaseFragment<FragmentIntroFirstBinding>(
    R.layout.fragment_intro_first,0
) {

    override fun onStart() {
        super.onStart()

        binding.apply {
            introActivity = activity as IntroActivity

            firstFragment = this@FirstFragment

            intro1Image.load(R.drawable.intro_1_image)

            /* Cannot use the CDATA html method here, because
               the string contains CDATA of its own (appName) */
            intro1Text1.text = getHelpSpannedFromStringRes(
                R.string.app_name,
                R.string.intro_1_text_1
            )

            intro1Text2.text = Html.fromHtml(
                getString(R.string.intro_1_text_2),
                Html.FROM_HTML_MODE_COMPACT
            )

            intro1Text3.text = Html.fromHtml(
                getString(R.string.intro_1_text_3),
                Html.FROM_HTML_MODE_COMPACT
            )

            intro1Text4.isVisible =
                sharedPrefs.getBoolean(
                    APP_FIRST_RUN_PREF,true
                )
        }
    }
}
