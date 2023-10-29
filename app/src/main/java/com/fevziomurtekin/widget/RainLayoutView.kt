package com.fevziomurtekin.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.michaelrmossman.kotlin.rainbows.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class RainLayoutView : ConstraintLayout {

    private var animationJob = SupervisorJob()

    /**
     * Original library by fevziomurtekin: Fevzi Ömür Tekin, of
     * AndroidArsenal|GitHub fame, but I found that I needed to
     * change the drawable used in the animation, so I opted to
     * use the files "hard-coded" ... many thanks to Fevzi =)
     */
    private var uiScope = CoroutineScope(Dispatchers.Main + animationJob)

    constructor(context: Context): super(context) { init(context,null,0,0) }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) { init(context, attrs,0,0) }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) { init(context,attrs,defStyleAttr,0) }

    /** attributes of layout **/
    // private var dropSrc = resources.getDrawable(R.drawable.rain_umbrella_dynamic)
    private var dropSrc: Drawable? = null
    private var dropPerSecond = 10
    private var dropTintColor = 0 // resources.getColor(android.R.color.white)
    private var fallToDropTime = 100
    private var isColorful = false

    @Suppress("SameParameterValue")
    private fun init(context: Context, attrs:AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        context.theme?.obtainStyledAttributes(attrs,R.styleable.rain,defStyleAttr,defStyleRes)?.let {
            // dropSrc = ContextCompat.getDrawable(context, R.drawable.rain_umbrella_dynamic)
            dropPerSecond = it.getInt(R.styleable.rain_dropPerSecond,10)
            dropTintColor = it.getColor(R.styleable.rain_dropTintColor, ContextCompat.getColor(context, android.R.color.white))
            fallToDropTime = it.getInt(R.styleable.rain_durationOfDropTime,100)
            isColorful = it.getBoolean(R.styleable.rain_isColorful,false)
            it.recycle()
            initViews()
        }
    }

    /** initViews for RainLayout */
    @SuppressLint("InflateParams")
    private fun initViews() {
        val view = LayoutInflater.from(context).inflate(R.layout.rain_layout,null)
        this@RainLayoutView.apply {
            val rainLayout = view.findViewById<RelativeLayout>(R.id.relative_layout)
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)
            addView(rainLayout)
            constraintSet.apply {
                connect(rainLayout.id, ConstraintSet.RIGHT, this@RainLayoutView.id, ConstraintSet.RIGHT,0)
                connect(rainLayout.id, ConstraintSet.LEFT, this@RainLayoutView.id, ConstraintSet.LEFT,0)
                connect(rainLayout.id, ConstraintSet.TOP, this@RainLayoutView.id, ConstraintSet.TOP,0)
                connect(rainLayout.id, ConstraintSet.BOTTOM, this@RainLayoutView.id, ConstraintSet.BOTTOM,0)
            }
            constraintSet.applyTo(this)
        }
        this@RainLayoutView.requestLayout()

        /* Not only do we NOT want the animation to start automatically, but now we
           also need to set the drawableId BEFOREHAND, or nothing will show. MRM */
        // showAnimation()
    }

    fun animationClear() = animationJob.cancel()

    fun setDropSrc(@DrawableRes drawableId: Int) {
        dropSrc = ContextCompat.getDrawable(context, drawableId)
    }

    fun showAnimation() {
        if(!uiScope.isActive) {
            animationJob = SupervisorJob()
            uiScope = CoroutineScope(Dispatchers.Main + animationJob)
        }

        dropSrc?.let { drawable ->
            uiScope.launch {
                repeat(100_000) {
                    RainAnimation.showAnimation(
                        context,
                        this@RainLayoutView.findViewById(R.id.relative_layout) as RelativeLayout,
                        fallToDropTime,
                        drawable,
                        dropTintColor,
                        isColorful)
                    delay(1_000.div(dropPerSecond).toLong())
                }
            }
        }
    }
}
