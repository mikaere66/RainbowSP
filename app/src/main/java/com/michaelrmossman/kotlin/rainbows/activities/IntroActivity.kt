package com.michaelrmossman.kotlin.rainbows.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.michaelrmossman.kotlin.rainbows.R
import com.michaelrmossman.kotlin.rainbows.RainbowsApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.rainbows.adapters.IntroPagerAdapter
import com.michaelrmossman.kotlin.rainbows.databinding.ActivityIntroBinding
import com.michaelrmossman.kotlin.rainbows.utils.APP_FIRST_RUN_PREF
import kotlin.properties.Delegates

class IntroActivity: AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    // These three are public for use in fragments
    var itemCount by Delegates.notNull<Int>()
    lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_intro
        )

        itemCount = 4

        onBackPressedDispatcher.addCallback(this, object:
                OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishWithResult()
            }
        })

        viewPager = binding.introViewPager.apply {
            adapter = IntroPagerAdapter(
                this@IntroActivity, itemCount
            )
        }

        viewPager.registerOnPageChangeCallback(object:
                ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                supportActionBar?.subtitle = String.format(
                    getString(R.string.intro_subtitle),
                    position + 1, itemCount
                )
            }
        })
    }

    fun finishWithResult() {
        if (sharedPrefs.edit().putBoolean(
            APP_FIRST_RUN_PREF, false).commit()
        ) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
