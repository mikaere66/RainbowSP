package com.michaelrmossman.kotlin.rainbows.fragments

import android.text.Html
import androidx.core.text.method.LinkMovementMethodCompat
import coil.load
import com.michaelrmossman.kotlin.rainbows.R
import com.michaelrmossman.kotlin.rainbows.activities.IntroActivity
import com.michaelrmossman.kotlin.rainbows.databinding.FragmentIntroSecondBinding

class SecondFragment: BaseFragment<FragmentIntroSecondBinding>(
    R.layout.fragment_intro_second,1
) {

    override fun onStart() {
        super.onStart()

        binding.apply {
            introActivity = activity as IntroActivity

            secondFragment = this@SecondFragment

            intro2Image.load(R.drawable.intro_2_image)

            intro2Text2.apply {
                text = Html.fromHtml(
                    getString(R.string.intro_2_text_2),
                    Html.FROM_HTML_MODE_COMPACT
                )
                movementMethod =
                    LinkMovementMethodCompat.getInstance()
            }

            intro2Text6.apply {
                text = Html.fromHtml(
                    getString(R.string.intro_2_text_6),
                    Html.FROM_HTML_MODE_COMPACT
                )
            }
        }
    }
}
