package top.mcwebsite.novel.ui.read

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDialog
import androidx.core.view.forEach
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
import java.util.zip.Inflater

class ReadConfigSettingsFragment(
) : BottomSheetDialogFragment(), KoinComponent {

    interface OnReadSettingChangeListener {
        fun onColorChange(readColor: ReadColor)

        fun onTextChange()
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
    }


}