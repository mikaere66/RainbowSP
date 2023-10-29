package com.michaelrmossman.kotlin.rainbows.fragments

import android.text.Html
import coil.load
import com.michaelrmossman.kotlin.rainbows.R
import com.michaelrmossman.kotlin.rainbows.activities.IntroActivity
import com.michaelrmossman.kotlin.rainbows.databinding.FragmentIntroThirdBinding

class ThirdFragment: BaseFragment<FragmentIntroThirdBinding>(
    R.layout.fragment_intro_third,2
) {

    override fun onStart() {
        super.onStart()

        binding.apply {
            introActivity = activity as IntroActivity

            thirdFragment = this@ThirdFragment

            intro3Image.load(R.drawable.intro_3_image)

            intro3Text2.apply {
                text = Html.fromHtml(
                    getString(R.string.intro_3_text_2),
                    Html.FROM_HTML_MODE_COMPACT
                )
            }
        }
    }
}
