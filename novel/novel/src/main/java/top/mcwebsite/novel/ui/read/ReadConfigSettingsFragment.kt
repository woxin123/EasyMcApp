package top.mcwebsite.novel.ui.read

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.common.ui.utils.dp
import top.mcwebsite.common.ui.view.CircleView
import top.mcwebsite.novel.R
import top.mcwebsite.novel.config.ReadColor
import top.mcwebsite.novel.config.ReadConfig
import top.mcwebsite.novel.config.readColors
import top.mcwebsite.novel.databinding.LayoutReadSettingsBinding
import top.mcwebsite.novel.ui.read.page.PageMode
import java.util.zip.Inflater

class ReadConfigSettingsFragment(
) : BottomSheetDialogFragment(), KoinComponent {

    interface OnReadSettingChangeListener {
        fun onColorChange(readColor: ReadColor)

        fun onTextChange()

        fun onBrightnessChange(brightness: Float)

        fun setPageMode(pageMode: PageMode)
    }

    private val readConfig: ReadConfig by inject()

    var onReadSettingChangeListener: OnReadSettingChangeListener? = null

    private lateinit var binding: LayoutReadSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = LayoutReadSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initReadColorList()
        initTextSize()
        initBrightness()
    }

    private fun initReadColorList() {
        readColors.forEach { readColor ->
            val colorView = LayoutInflater.from(context)
                .inflate(R.layout.layout_read_color, binding.backgroundList, false).apply {
                    findViewById<CircleView>(R.id.background).color =
                        readColor.backgroundColor
                    val selected = findViewById<ImageView>(R.id.selected).apply {
                        val drawable = resources.getDrawable(R.drawable.circle_bg) as GradientDrawable
                        drawable.setStroke(2F.dp.toInt(), readColor.selectedColor)
                        setImageDrawable(drawable)
                        if (readConfig.backgroundColor == readColor.backgroundColor) {
                            visibility = View.VISIBLE
                        } else {
                            visibility = View.GONE
                        }
                    }
                    setOnClickListener {

                        for (index in 0 until binding.backgroundList.childCount) {
                            binding.backgroundList.getChildAt(index).apply {
                                findViewById<View>(R.id.selected).visibility = View.GONE
                            }
                        }
                        selected.visibility = View.VISIBLE
                        readConfig.textColor = readColor.textColor
                        readConfig.backgroundColor = readColor.backgroundColor
                        onReadSettingChangeListener?.onColorChange(readColor)
                    }
                }
            binding.backgroundList.addView(
                colorView,
                LinearLayout.LayoutParams(40F.dp.toInt(), 40F.dp.toInt()).apply {
                    marginEnd = 30F.dp.toInt()
                }
            )
        }
        binding.pageModeRecyclerView.adapter =
            PageModeAdapter(arrayListOf(PageMode.SIMULATION, PageMode.COVER, PageMode.SLIDE, PageMode.NONE), readConfig.pageMode) { pageMode ->
                onReadSettingChangeListener?.setPageMode(pageMode)
            }
        binding.pageModeRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
    }

    private fun initTextSize() {
        binding.textSize.text = readConfig.textSize.toInt().toString()
        binding.addTextSizeBtn.setOnClickListener {
            if (readConfig.textSize < ReadConfig.MAX_TEXT_SIZE) {
                readConfig.textSize++
            }
            binding.textSize.text = readConfig.textSize.toInt().toString()
            onReadSettingChangeListener?.onTextChange()
        }

        binding.minusTextSizeBtn.setOnClickListener {
            if (readConfig.textSize > ReadConfig.MIN_TEXT_SIZE) {
                readConfig.textSize--
            }
            binding.textSize.text = readConfig.textSize.toInt().toString()
            onReadSettingChangeListener?.onTextChange()
        }
    }

    private fun initBrightness() {
        binding.brightnessSeekBar.max = 100
        if (readConfig.brightness == -1F) {
            binding.followSystemCb.isChecked = true
            val screenBrightness = (getScreenBrightness() * 100).toInt()
            binding.brightnessSeekBar.progress = screenBrightness
        } else {
            binding.brightnessSeekBar.progress = (readConfig.brightness * 100).toInt()
        }
        binding.brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                onReadSettingChangeListener?.onBrightnessChange(progress.toFloat() / 100)
                if (binding.followSystemCb.isChecked) {
                    binding.followSystemCb.isChecked = false
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        binding.followSystemCb.setOnClickListener {
            onReadSettingChangeListener?.onBrightnessChange(-1F)
        }
    }

    private fun getScreenBrightness(): Float {
        return Settings.System.getInt(requireActivity().contentResolver, Settings.System.SCREEN_BRIGHTNESS, 125).toFloat() / 255F
    }

}