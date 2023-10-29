package com.michaelrmossman.kotlin.rainbows.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.michaelrmossman.kotlin.rainbows.activities.IntroActivity

open class BaseFragment<T: ViewDataBinding>(
    @LayoutRes private val layoutResId: Int,
    private val position: Int
) : Fragment() {

    private var _binding: T? = null
    val binding: T
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        _binding = DataBindingUtil.inflate(
            inflater, layoutResId, container,false
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
    }

    fun goNext() {
        val introActivity = activity as IntroActivity
        when (position) {
            introActivity.itemCount -> introActivity.finish()
            else -> {
                val currentItem = introActivity.viewPager.currentItem
                introActivity.viewPager.setCurrentItem(
                    currentItem.plus(1),true
                )
            }
        }
    }
}
