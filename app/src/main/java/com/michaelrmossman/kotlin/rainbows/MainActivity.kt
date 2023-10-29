package com.michaelrmossman.kotlin.rainbows

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnDetachedFromWindow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.android.device.DeviceName
import com.michaelrmossman.kotlin.rainbows.activities.IntroActivity
import com.michaelrmossman.kotlin.rainbows.databinding.ActivityMainBinding
import com.michaelrmossman.kotlin.rainbows.entities.Rainbow
import com.michaelrmossman.kotlin.rainbows.RainbowsApp.Companion.actionMenu
import com.michaelrmossman.kotlin.rainbows.RainbowsApp.Companion.alertDialog
import com.michaelrmossman.kotlin.rainbows.RainbowsApp.Companion.manufacturer
import com.michaelrmossman.kotlin.rainbows.RainbowsApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.rainbows.utils.*
import com.michaelrmossman.kotlin.rainbows.utils.getSelectedIndex
import com.michaelrmossman.kotlin.rainbows.utils.RootUtils.echoCommand
import com.michaelrmossman.kotlin.rainbows.utils.showSnackbar
import com.michaelrmossman.kotlin.rainbows.utils.SysUtils.checkForAlertDialog
import com.michaelrmossman.kotlin.rainbows.utils.SysUtils.checkForSnackbar
import com.michaelrmossman.kotlin.rainbows.utils.SysUtils.getPathToBrightness
import com.michaelrmossman.kotlin.rainbows.utils.SysUtils.getPathToLedCurrent
import com.michaelrmossman.kotlin.rainbows.utils.SysUtils.isEmulator
import com.michaelrmossman.kotlin.rainbows.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.rainbows.viewmodel.RainbowViewModel
import com.nex3z.togglebuttongroup.button.LabelToggle
import com.stephenvinouze.segmentedprogressbar.SegmentedProgressBar
import com.stephenvinouze.segmentedprogressbar.models.SegmentColor
import com.stericson.RootTools.RootTools
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private var brightnessValue by Delegates.notNull<Float>()
    private var progressValue by Delegates.notNull<Float>()
    private val viewModel by viewModels<RainbowViewModel>()
    private var messageFormat by Delegates.notNull<Int>()
    private var rainbowsFlow: Flow<List<Rainbow>>? = null
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var binding: ActivityMainBinding
    private var millis by Delegates.notNull<Long>()
    private var errorMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        if (intent.extras?.getBoolean(SETTINGS_RESET_EXTRA,
                false) == true) {
            binding.root.showSnackbar(
                R.string.reset_done,
                Snackbar.LENGTH_LONG
            )
        }

        when (isEmulator()) {
            true -> initUi(true)
            else -> when (sharedPrefs.getBoolean(
                APP_FIRST_RUN_PREF,true
            )) {
                false -> verifyRootAccess()
                else -> introLauncher.launch(
                    Intent(
                        this,
                        IntroActivity::class.java
                    )
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        actionMenu = menu
        return true
    }

    override fun onDestroy() {
        setLightsOff(cancel = true, update = false)
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_reset -> { settingsResetConfirm(); true }
            R.id.menu_intro -> { replayIntroduction(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        checkForAlertDialog()
        checkForSnackbar()
        super.onPause()
    }

    override fun onStop() {
        binding.rainLayoutView.animationClear()
        super.onStop()
    }

    private fun cancelCoroutine() {
        if (this::coroutineScope.isInitialized) {
            coroutineScope.cancel()
        }
    }

    private fun enableDisableGroupButtons(
        buttons: List<LabelToggle>, enable: Boolean
    ) {
        buttons.forEach { button ->
            button.isEnabled = enable
        }
    }

    private fun getEffectSelectedIndex(): Int {
        return with (binding.buttonsLayout.toggleEffect) {
            when (val checkedId = checkedId
            ) {
                View.NO_ID -> 0
                else -> getSelectedIndex(checkedId)
            }
        }
    }

    private fun initUi(success: Boolean) {
        actionMenu?.findItem(R.id.menu_reset)?.isEnabled =
            (errorMessage == null)

        brightnessValue = sharedPrefs.getFloat(
            BRIGHTNESS_SLIDER_PREF, getString(
                R.string.brightness_mean
            ).toFloat()
        )

        val cycleValue = sharedPrefs.getFloat(
            CYCLES_NUM_SLIDER_PREF, getString(
                R.string.cycle_mean
            ).toFloat()
        )

        val delayValue = sharedPrefs.getFloat(
            DELAY_SECS_SLIDER_PREF, getString(
                R.string.delay_mean
            ).toFloat()
        )

        val effectButtons: List<LabelToggle>
        val black = ContextCompat.getColor(
            this, android.R.color.black
        )

        binding.apply {

            buttonsLayout.apply {

                effectButtons = listOf(
                    effectButtonOne.apply   { setTextColor(black) },
                    effectButtonTwo.apply   { setTextColor(black) },
                    effectButtonThree.apply { setTextColor(black) },
                    effectButtonFour.apply  { setTextColor(black) }
                )

                toggleEffect.apply {
                    val effectButton: View = toggleEffect.getChildAt(
                        sharedPrefs.getInt(
                            EFFECT_SELECTED_PREF,0
                        )
                    )
                    /* There's no way to prevent "notifying" the
                       listener, so just checking a button ends
                       up calling setSwitchRainText() for us */
                    check(effectButton.id)
                    setOnCheckedChangeListener { group, checkedId ->
                        val index = group.getSelectedIndex(checkedId)
                        if (sharedPrefs.edit().putInt(
                                EFFECT_SELECTED_PREF, index
                            ).commit()
                        ) {
                            setSwitchRainText()
                        }
                    }
                }

                /* with is required */
                with (switchLights) {
                    setOnClickListener {
                        when (success) {
                            false -> showErrorMessage()
                            else -> {
                                when (this.isHighlighted) {
                                    false -> setHighlightingTask()
                                    else -> setLightsOff(
                                        cancel = true, update = true
                                    )
                                }
                            }
                        }
                    }
                }
            }

            slidersLayout.apply {

                cycleSlider.apply {
                    addOnChangeListener { slider, value, fromUser ->
                        if (fromUser) {
                            if (sharedPrefs.edit().putFloat(
                                    CYCLES_NUM_SLIDER_PREF, value
                            ).commit()) {
                                updateProgressBar()
                                setSliderHeaderText(slider)
                            }
                        }
                    }
                    value = cycleValue
                    setSliderHeaderText(this)
                }

                delaySlider.apply {
                    addOnChangeListener { slider, value, fromUser ->
                        if (fromUser) {
                            if (sharedPrefs.edit().putFloat(
                                    DELAY_SECS_SLIDER_PREF, value
                            ).commit()) {
                                setSliderHeaderText(slider)
                            }
                        }
                    }
                    value = delayValue
                    setSliderHeaderText(this)
                }

                ledCurrentSlider.apply {
                    addOnChangeListener { slider, value, fromUser ->
                        if (fromUser) {
                            if (sharedPrefs.edit().putFloat(
                                    BRIGHTNESS_SLIDER_PREF, value
                            ).commit()) {
                                setSliderHeaderText(slider)
                            }
                        }
                    }
                    value = brightnessValue
                    setSliderHeaderText(this)
                }

                switchRain.apply {
                    isChecked = sharedPrefs.getBoolean(
                        ADD_SOME_RAIN_PREF, false
                    )
                    setOnCheckedChangeListener { _, isChecked ->
                        if (sharedPrefs.edit().putBoolean(
                            ADD_SOME_RAIN_PREF, isChecked
                        ).commit()) {
                            when (isChecked) {
                                true -> if (
                                    this@MainActivity::coroutineScope.isInitialized
                                ) {
                                    if (buttonsLayout.switchLights.isHighlighted) {
                                        setDropSrcDrawableId()
                                        binding.rainLayoutView.showAnimation()
                                    }
                                }
                                else -> binding.rainLayoutView.animationClear()
                            }
                        }
                    }
                }

                cycleSlider.isEnabled = success
                delaySlider.isEnabled = success
                ledCurrentSlider.isEnabled = success
                switchRain.isEnabled = success
            }
        }

        progressValue = 0F
        updateProgressBar()

        enableDisableGroupButtons(effectButtons, success)
        /* Can't enable/disable switchLights.
           Refer clickListener just above */
    }

    private val introLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            verifyRootAccess()
        }
    }

    private fun setDropSrcDrawableId() {
        val typedArray = resources.obtainTypedArray(
            R.array.rainbow_icons
        )
        binding.rainLayoutView.setDropSrc(
            typedArray.getResourceId(
                getEffectSelectedIndex(),0
            )
        )
        typedArray.recycle()
    }

    private fun setHighlightingTask() {
        enableDisableViews(false)

        binding.apply {
            if (slidersLayout.switchRain.isChecked) {
                setDropSrcDrawableId()
                rainLayoutView.showAnimation()
            }

            buttonsLayout.switchLights.apply {
                showProgressBar()
                setBottomText(getString(
                    R.string.highlight_starts_text
                ))

                lifecycleScope.launch {
                    delay(highlightingTaskDelays)
                    isHighlighted = true
                    delay(highlightingTaskDelays)
                    hideProgressBar()
                }
            }

            updateProgressBar()
        }

        setLightsOn()
    }

    private fun enableDisableViews(
        enable: Boolean
    ) {
        binding.slidersLayout.cycleSlider.isEnabled = enable
        binding.buttonsLayout.apply {
            effectButtonOne.isEnabled = enable
            effectButtonTwo.isEnabled = enable
            effectButtonThree.isEnabled = enable
            effectButtonFour.isEnabled = enable
        }
    }

    private fun replayIntroduction() {
        startActivity(
            Intent(this, IntroActivity::class.java)
        )
    }

    private fun setLightsOff(cancel: Boolean, update: Boolean) {
        if (cancel) { cancelCoroutine() }

        binding.apply {
            rainLayoutView.animationClear()
            buttonsLayout.switchLights.isHighlighted = false
        }

        enableDisableViews(true)

        setLightsRGB(0, ledCurrentOff)

        rainbowsFlow = null // Reset

        if (update) {
            progressValue = 0F
            updateProgressBar()
        }
    }

    private fun setLightsOn() {
        coroutineScope = CoroutineScope(Dispatchers.Default)
        coroutineScope.launch {
            val effect = getEffectSelectedIndex()
            val sleepDivisor = when (effect) {
                in 2..3 -> 1           // Police|RGB: 1 ea
                else -> threadSleepDivisor // 0-1 (Val: 5)
            }

            withContext(Dispatchers.IO) {
                rainbowsFlow = viewModel.getRainbowGroups(effect)
                rainbowsFlow?.collect { rainbows ->

                    val cycleTimes = binding.slidersLayout.cycleSlider.value.toInt()
                    val rainbowMax = cycleTimes.times(rainbows.size)
                    val percentage = (100.0F).div(rainbowMax) // Float
                    if (debug) {
                        Log.i("RAINBOWS_QTY", rainbows.size.toString())
                        Log.i("PERCENTAGE", percentage.toString())
                    }
                    millis = System.currentTimeMillis()

                    if (isEmulator()) { return@collect }

                    repeat(cycleTimes) { index ->
                        rainbows.forEach { rainbow ->
                            when (rainbow.ledNum) {
                                0 -> {
                                    Thread.sleep(
                                        binding.slidersLayout.delaySlider.value.div(
                                            sleepDivisor
                                        ).toLong()
                                    )
                                }
                                else -> {
                                    val brightnessPath = getPathToBrightness(
                                        rainbow.ledNum, rainbow.rgbValue
                                    )
                                    echoCommand(
                                        rainbow.id.times(index.plus(1)),
                                        rainbow.ledCurrent,
                                        brightnessPath
                                    )

                                    val sliderValue = binding.slidersLayout.ledCurrentSlider.value
                                    if (
                                        brightnessValue != sliderValue ||
                                        rainbow.id == rainbowsStartId
                                    ) {
                                        setLightsRGB(
                                            rainbow.id.times(index.plus(
                                                1
                                            )).unaryMinus(),
                                            sliderValue.toInt()
                                        )
                                        if (debug) {
                                            Log.i(
                                                "BRIGHTNESS $index",
                                                "$brightnessValue | $sliderValue"
                                            )
                                        }
                                        brightnessValue = sliderValue
                                    }
                                }
                            }

                            withContext(Dispatchers.Main) {
                                progressValue = progressValue.plus(
                                    percentage.div(
                                        100.div(cycleTimes)
                                    )
                                )
                                if (debug) {
                                    Log.i(
                                        "PERCENT ${ rainbow.id }",
                                        progressValue.toString()
                                    )
                                }
                                updateProgressBar()
                            }
                        }
                    }

                    val time = System.currentTimeMillis().minus(
                        millis
                    ).div(1_000)
                    Log.i("SEQUENCE_END","$time seconds")
                    millis = 0L

                    withContext(Dispatchers.Main) {
                        setLightsOff(cancel = false, update = true)
                    }
                }
            }
        }
    }

    private fun setLightsRGB(id: Int, value: Int) {
        val commands = mutableListOf<String>()

        val colours = listOf(red, green, blue)
        colours.forEach { rgbValue ->
            for (i in 1..3) {
                commands.add(
                    getPathToLedCurrent(i, rgbValue)
                )
            }
        }

        commands.forEach { command ->
            echoCommand(id, value, command)
        }
    }

    private fun setSliderHeaderText(slider: Slider) {
        with (binding.slidersLayout) {
            val stringId = when (slider.id) {
                cycleSlider.id -> R.string.cycle_text
                delaySlider.id -> R.string.delay_text
                else -> R.string.brightness_text
            }
            val textView = when (slider.id) {
                cycleSlider.id -> cycleText
                delaySlider.id -> delayText
                else -> brightnessText
            }
            textView.text = String.format(
                getString(stringId),
                slider.value
            )
        }
    }

    private fun setSubtitle(infoModel: String) {
        supportActionBar?.subtitle = String.format(
            getString(R.string.app_subtitle),
            infoModel
        )
    }

    private fun setSwitchRainText() {
        val switchRainText = resources.getStringArray(
            R.array.rainbow_messages
        )[getEffectSelectedIndex()]
        binding.slidersLayout.switchRain.text = switchRainText
    }

    private fun settingsResetConfirm() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setIcon(R.drawable.baseline_restart_alt_black_24)
            setTitle(getString(R.string.menu_reset))
            setMessage(getString(R.string.reset_confirm))

            val cancel = getString(android.R.string.cancel)
            setNegativeButton(cancel,null)

            setOnCancelListener {
                binding.root.showSnackbar(
                    R.string.action_cancelled,
                    Snackbar.LENGTH_SHORT
                )
            }

            val okay = getString(android.R.string.ok)
            setPositiveButton(okay) { _, _ ->
                settingsResetAll()
            }
        }

        alertDialog = alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun settingsResetAll() {
        val entries = sharedPrefs.all
         when (entries.size) {
            0 -> fancyToast(
                this,4, R.string.reset_none
            )
            else -> {
                val editor = sharedPrefs.edit()
                // Clear all preferences EXCEPT "first_run"
                for (entry in entries) {
                    if (entry.key != APP_FIRST_RUN_PREF) {
                        editor.remove(entry.key)
                    }
                }
                if (editor.commit()) {
                    settingsRestart()
                }
            }
         }
    }

    private fun settingsRestart() {
        val newIntent = intent
        newIntent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        newIntent.putExtra(SETTINGS_RESET_EXTRA,true)
        startActivity(intent)
        finish()
    }

    private fun showErrorMessage() {
        errorMessage?.let { message ->
            try { // Just in case messageFormat is null
                fancyToast(this, messageFormat, message)
            } catch (ignore: IllegalStateException) {}
        }
    }

    private fun updateProgressBar() {
        val cycleValue = binding.slidersLayout.cycleSlider.value.toInt()
        binding.segmentedProgress.apply {
            setViewCompositionStrategy(DisposeOnDetachedFromWindow)
            setContent {
                /* We're in JetPack Compose world here ... not to
                   mention, a world of pain, in the proverbial */
                MaterialTheme {
                    SegmentedProgressBar(
                        angle = 30F,
                        progress = progressValue,
                        segmentColor = SegmentColor(
                            color = Color.Gray,
                            alpha = 0.33F
                        ),
                        segmentCount = cycleValue,
                        spacing = 10.dp
                    )
                }
            }
        }
    }

    private fun verifyRootAccess() {
        lifecycleScope.launch {
            val context = this@MainActivity
            DeviceName.init(context)
            DeviceName.with(context).request { info, error ->
                if (debug) {
                    // DeviceName.info.manufacturer is deprecated
                    Log.i("DEV_MAKE", manufacturer)  // e.g. Sony
                    Log.i("DEV_MODE", info.model)    // e.g. Xperia SP
                    Log.i("DEV_CODE", info.codename) // e.g. huashan
                    Log.i("DEV_NAME", info.name)     // e.g. huashan
                }

                when (error) {
                    null -> {
                        when (info.model) {
                            xperiaSP -> {
                                when (RootTools.isRootAvailable()) {
                                    true -> {
                                        if (RootTools.isAccessGiven()) {
                                            if (debug) {
                                                Log.i(
                                                    "ROOT",
                                                    "App granted root access"
                                                )
                                            }
                                            setSubtitle(info.model)
                                            initUi(true)
                                            return@request
                                        }

                                    }
                                    else -> {
                                        errorMessage = getString(
                                            R.string.error_no_root
                                        )
                                        Log.e("ROOT","No root!")
                                        messageFormat = 3
                                    }
                                }
                            }
                            else -> {
                                errorMessage = getString(
                                    R.string.device_error
                                )
                                messageFormat = 4
                            }
                        }
                    }
                    else -> {
                        errorMessage = getString(
                            R.string.device_unknown
                        )
                        Log.e("DEV_ERR!", error.toString())
                        messageFormat = 3
                    }
                }

                initUi(false)
                showErrorMessage()
            }
        }
    }
}
