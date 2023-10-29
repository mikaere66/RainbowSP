package com.michaelrmossman.kotlin.rainbows.fragments

import android.text.Html
import android.widget.ScrollView
import android.view.View
import androidx.core.text.method.LinkMovementMethodCompat
import coil.load
import com.michaelrmossman.kotlin.rainbows.R
import com.michaelrmossman.kotlin.rainbows.activities.IntroActivity
import com.michaelrmossman.kotlin.rainbows.databinding.FragmentIntroFourthBinding

class FourthFragment: BaseFragment<FragmentIntroFourthBinding>(
    R.layout.fragment_intro_fourth,3
) {

    private lateinit var scrollView: ScrollView

    override fun onStart() {
        super.onStart()

        binding.apply {
            scrollView = intro4ScrollView

            introActivity = activity as IntroActivity

            fourthFragment = this@FourthFragment

            intro4Image.load(R.drawable.intro_4_image)

            intro4Text2.text = Html.fromHtml(
                getString(R.string.intro_4_text_2),
                Html.FROM_HTML_MODE_COMPACT
            )

            intro4Text4.apply {
                text = Html.fromHtml(
                    getString(R.string.intro_4_text_4),
                    Html.FROM_HTML_MODE_COMPACT
                )
                movementMethod =
                    LinkMovementMethodCompat.getInstance()
            }

            intro4ScrollView.setOnScrollChangeListener {
                    _, _, scrollY, _, _ ->
                showHideFabs(scrollY)
            }
        }
    }

    private fun showHideFabs(scrollY: Int) {
        /* scrollUpFab was only hidden because
           having them both visible in Android
           Studio's Layout Editor looks stupid =) */
        binding.scrollUpFab.visibility = View.VISIBLE
        binding.scrollDownFab.visibility = when (scrollY) {
            0 -> View.VISIBLE
            else -> View.GONE
        }
    }

    fun scrollDown() { // Called from xml
        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        showHideFabs(Int.MAX_VALUE)
    }

    fun scrollUp() { // Called from xml
        scrollView.scrollTo(0,0)
    }
}
