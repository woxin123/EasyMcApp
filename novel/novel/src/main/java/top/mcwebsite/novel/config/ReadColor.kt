package top.mcwebsite.novel.config

import android.graphics.Color
import androidx.annotation.ColorInt

data class ReadColor(
    @ColorInt val backgroundColor: Int,
    @ColorInt val textColor: Int,
    @ColorInt val primaryColor: Int,
    @ColorInt val selectedColor: Int,
)

val readColors = listOf(
    ReadColor(
        Color.parseColor("#F6F5F6"), Color.parseColor("#424242"),
        Color.parseColor("#FDFCFD"), Color.parseColor("#344C34")
    ),
    ReadColor(
        Color.parseColor("#F6DDB1"), Color.parseColor("#6B4620"),
        Color.parseColor("#EFDEBF"), Color.parseColor("#344C34")
    ),
    ReadColor(
        Color.parseColor("#D6EFD2"), Color.parseColor("#5A775B"),
        Color.parseColor("#E8FCE5"), Color.parseColor("#344C34")
    ),
    ReadColor(
        Color.parseColor("#26374D"), Color.parseColor("#9AABC1"),
        Color.parseColor("#274262"), Color.parseColor("#9CB2CD")
    ),
    ReadColor(
        Color.parseColor("#F2FCF2"), Color.parseColor("#516246"),
        Color.parseColor("#FCFFFC"), Color.parseColor("#485045")
    ),
    ReadColor(
        Color.parseColor("#574036"), Color.parseColor("#C5A48D"),
        Color.parseColor("#624A40"), Color.parseColor("#C8AB9D")
    ),
)
