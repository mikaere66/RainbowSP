package com.michaelrmossman.kotlin.rainbows.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.michaelrmossman.kotlin.rainbows.fragments.FirstFragment
import com.michaelrmossman.kotlin.rainbows.fragments.FourthFragment
import com.michaelrmossman.kotlin.rainbows.fragments.SecondFragment
import com.michaelrmossman.kotlin.rainbows.fragments.ThirdFragment

class IntroPagerAdapter(
    fragmentActivity: FragmentActivity,
    private var itemCount: Int
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(
        position: Int
    ) : Fragment = when (position) {
            0 -> FirstFragment()
            1 -> SecondFragment()
            2 -> ThirdFragment()
            else -> {
                FourthFragment() // 3
            }
        }

    override fun getItemCount(): Int {
        return itemCount
    }
}
