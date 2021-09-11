package top.mcwebsite.novel.ui.read.view

import android.content.Context
import android.graphics.*
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import top.mcwebsite.common.ui.utils.convertString
import top.mcwebsite.common.ui.utils.dip2px
import top.mcwebsite.common.ui.utils.dp
import top.mcwebsite.common.ui.utils.halfToFull
import top.mcwebsite.novel.config.ReadConfig
import top.mcwebsite.novel.model.BookModel
import top.mcwebsite.novel.model.Chapter
import top.mcwebsite.novel.model.Page
import top.mcwebsite.novel.ui.read.PageProvider
import java.util.*
import kotlin.collections.ArrayList
class PageViewDrawer : KoinComponent {


    companion object {
        // 当前页面的状态
        const val STATUS_LOADING = 1 // 正在加载

        const val STATUS_FINISH = 2 // 加载完成

        const val STATUS_ERROR = 3 // 加载错误 (一般是网络加载情况)

        const val STATUS_EMPTY = 4 // 空数据

        const val STATUS_PARING = 5 // 正在解析 (装载本地数据)

        const val STATUS_PARSE_ERROR = 6 // 本地文件解析错误(暂未被使用)

        const val STATUS_CATEGORY_EMPTY = 7 // 获取到的目录为空

        const val STATUS_INIT = 8

    }

    var status = STATUS_INIT

    private val context: Context by inject()

    val readConfig = ReadConfig()


    // 绘制的配置


    private var batterFontSize: Float = dip2px(context, 12F).toFloat()

    // 电池边界宽度
    private var borderWidth: Int = dip2px(context, 1F)


    // 页面的宽度
    private val width: Int

    // 页面的高度
    private val height: Int

    // 文字的字体大小
    private var fontSize: Float = dip2px(context, 14F).toFloat()

    // 上下与边缘的距离
    private var marginHeight: Float = dip2px(context, 30F).toFloat()

    // 左右与边缘的距离
    private var marginWidth: Float = dip2px(context, 15F).toFloat()

    // 左右与边缘的距离
    private var measureMarginWidth: Float = 0F


    // 绘制的宽度
    private val visibleWidth: Float

    // 绘制的高度
    private val visibleHeight: Float

    // 行间距
    private val lineSpace: Float = dip2px(context, 10F).toFloat()

    // 段间距
    private val paragraphSpace: Float = dip2px(context, 30F).toFloat()
    private var titlePara: Float = readConfig.textSize + dip2px(context, 4F).toFloat()


    // 绘制的配置
    // PageWidget
    var pageWidget: PageWidget? = null

    // 绘制标题的画笔
    private val titlePaint = Paint()

    // 绘制内容的画笔
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // 绘制提示的画笔
    private val tipPaint = Paint()

    // 绘制电池的画笔
    private val batteryPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    private var textInterval: Float = fontSize / 2
    private var titleInterval: Float = fontSize / 2

    private var textPara: Float = fontSize


    private var pageMode: PageMode = PageMode.COVER

    lateinit var pageProvider: PageProvider

    // TODO
    private var safeInsetTop: Int = 0

    private val batteryLevel = 50


    init {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metric = DisplayMetrics()
        wm.defaultDisplay.getMetrics(metric)
        width = metric.widthPixels
        height = metric.heightPixels

        visibleWidth = width - marginWidth * 2
        visibleHeight = height - marginHeight * 2

        // 提示画笔初始化
        tipPaint.apply {
            color = Color.BLACK
            textAlign = Paint.Align.LEFT
            textSize = 12F.dp
            isAntiAlias = true
            isSubpixelText = true
        }

        textPaint.textAlign = Paint.Align.LEFT // 左对齐
        textPaint.textSize = readConfig.textSize
        textPaint.color = Color.BLACK
        textPaint.isSubpixelText = true

        titlePaint.textSize = readConfig.titleSize
        titlePaint.style = Paint.Style.FILL_AND_STROKE
        titlePaint.typeface = Typeface.DEFAULT_BOLD

        batteryPaint.textSize = dip2px(context, 12F).toFloat()
        batteryPaint.color = Color.BLACK

        measureMarginWidth()
    }

    private fun measureMarginWidth() {
        val wordWidth = textPaint.measureText("\u3000")
        val width = visibleWidth % wordWidth
        measureMarginWidth = marginWidth + width / 2
    }

    fun drawPage(bitmap: Bitmap, isUpdate: Boolean) {
        if (pageWidget != null) {
            drawBackground(bitmap, isUpdate)
            if (!isUpdate) {
                drawContent(bitmap)
            }
        } else {
            Toast.makeText(context, "绘制出错，wuwuwuwuwu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun drawBackground(bitmap: Bitmap, isUpdate: Boolean) {
        val canvas = Canvas(bitmap)
        val tipMarginHeight = 3F.dp
        if (!isUpdate) {
            canvas.drawColor(Color.WHITE)
            if (status != STATUS_INIT) {
                val chapter = getCurrentChapter()
                val tipTop = tipMarginHeight - tipPaint.fontMetrics.top + safeInsetTop

                canvas.drawText(chapter.title, marginWidth, tipTop, tipPaint)
                if (status == STATUS_FINISH) {
                    val y = height - tipPaint.fontMetrics.bottom - tipMarginHeight
                    val percent: String =
                        (pageProvider.getCurPage().position + 1).toString() + "/" + pageProvider.getCurPageCount()
                            .toString()
                    canvas.drawText(percent, marginWidth, y, tipPaint)
                }
            }
        }
        // 绘制电池
        val visibleRight = width - marginWidth
        val visibleBottom = height - tipMarginHeight

        val outFrameWidth = tipPaint.measureText("xxx")
        val outFrameHeight = tipPaint.textSize
        val polarHeight = 6F.dp
        val polarWidth = 2F.dp
        val border = 1F
        val innerMargin = 1

        // 电极制作
        val polarLeft = visibleRight - polarWidth
        val polarTop = visibleBottom - (outFrameHeight + polarHeight) / 2
        val polar = RectF(polarLeft, polarTop, visibleRight, polarTop + polarHeight - 2F.dp)
        batteryPaint.style = Paint.Style.FILL
        canvas.drawRect(polar, batteryPaint)

        // 外框制作
        val outFrameLeft = polarLeft - outFrameWidth
        val outFrameTop = visibleBottom - outFrameHeight
        val outFrameBottom = visibleBottom - 2F.dp
        val outFrame = RectF(outFrameLeft, outFrameTop, polarLeft, outFrameBottom)

        batteryPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = border
        }
        canvas.drawRect(outFrame, batteryPaint)

        // 内框制作
        val innerWidth = (outFrame.width() - innerMargin * 2 - border).times(batteryLevel / 100F)
        val innerFrame = RectF(
            outFrameLeft + border + innerMargin, outFrameTop + border + innerMargin,
            outFrameLeft + border + innerWidth, outFrameBottom - border - innerMargin
        )
        batteryPaint.style = Paint.Style.FILL
        canvas.drawRect(innerFrame, batteryPaint)

        val y = height - tipPaint.fontMetrics.bottom - tipMarginHeight
        val time = Date().convertString("HH:mm")
        val x = outFrameLeft - tipPaint.measureText(time) - 4F.dp
        canvas.drawText(time, x, y, tipPaint)
    }

    private fun drawContent(bitmap: Bitmap) {
        val canvas = Canvas(bitmap)

        if (status != STATUS_FINISH) {
            val tip = when (status) {
                STATUS_LOADING -> "正在拼命加载..."
                STATUS_ERROR -> "加载失败"
                STATUS_EMPTY -> "内容为空"
                STATUS_PARING -> "正在排版中..."
                STATUS_CATEGORY_EMPTY -> "目录为空"
                STATUS_INIT -> "初始化中..."
                else -> "未知状态"
            }
            val fontMetrics = textPaint.fontMetrics
            val textHeight = fontMetrics.top - fontMetrics.bottom
            val textWidth = textPaint.measureText(tip)
            val pivotX = (visibleWidth - textWidth) / 2
            val pivotY = (visibleHeight - textHeight) / 2
            canvas.drawText(tip, pivotX, pivotY, textPaint)
            return
        }
        var top: Float = if (pageMode == PageMode.SCROLL) {
            -textPaint.fontMetrics.top + safeInsetTop
        } else {
            marginHeight - textPaint.fontMetrics.top + safeInsetTop
        }

        // 设置高度
        val interval = textInterval + textPaint.textSize
        val para = textPara + textPaint.textSize
        val titleInterval = titleInterval + titlePaint.textSize
        val realTitlePara = titlePara + textPaint.textSize
        var str = ""
        val currentPage = getCurrentPage()
        // 对标题进行绘制
        for (index in 0 until currentPage.titleLines) {
            str = currentPage.lines[index]
            // 设置顶部间距
            if (index == 0) {
                top += this.titlePara
            }

            // 计算文字显示的起始点
            val start = (visibleWidth - titlePaint.measureText(str)) / 2
            // 进行绘制
            canvas.drawText(str, start, top, titlePaint)

            // 设置尾部间距
            top += if (index == currentPage.titleLines - 1) {
                realTitlePara
            } else {
                // 行间距
                titleInterval
            }
        }

        // 对内容进行绘制
        for (index in currentPage.titleLines until currentPage.lines.size) {
            str = currentPage.lines[index]
            canvas.drawText(str, marginWidth, top, textPaint)
            if (str.endsWith("\n")) {
                top += para
            } else {
                top += interval
            }
        }

    }

    fun prePage(): Boolean {
        return if (pageProvider.changeToPrePage()) {
            pageWidget?.drawNextPage()
            true
        } else {
            false
        }
    }

    fun nextPage(): Boolean {
        return if (pageProvider.changeToNextPage()) {
            pageWidget?.drawNextPage()
            true
        } else {
            false
        }
    }

    fun cancelPage() {

    }



    private fun getCurrentChapter(): Chapter {
        return pageProvider.getCurChapter()
    }


    fun updateBattery(level: Int) {

    }

    fun updateTime() {

    }

    private fun getCurrentPage(): Page {
        return pageProvider.getCurPage()
    }


    fun loadPage(chapter: Chapter): List<Page> {
        // 生成的页面
        val pages = mutableListOf<Page>()
        var rHeight = visibleHeight
        var titleLineCount = 0
        var showTitle = true
        var paragraph = chapter.title
        val reader = (paragraph + "\r\n" + chapter.content).reader()
        val lines = mutableListOf<String>()
        for (line in reader.readLines()) {
            paragraph = line
            if (!showTitle) {
                paragraph = paragraph.replace("\\s", "")
                // 如果只有换行符，就不执行了
                if (paragraph == "") {
                    continue
                }
                paragraph = "  $paragraph\n".halfToFull()
            } else {
                // 设置顶部的高度
                rHeight -= titlePara
            }
            var wordCount = 0
            var subString = ""
            while (paragraph.isNotEmpty()) {
                if (showTitle) {
                    rHeight -= titlePaint.textSize
                } else {
                    rHeight -= textPaint.textSize
                }
                // 一页已经充满了，创建 Page
                if (rHeight <= 0) {
                    // 创建新的 page
                    val page = Page()
                    page.position = pages.size
                    page.title = chapter.title
                    page.titleLines = titleLineCount
                    page.lines = ArrayList(lines)
                    pages.add(page)
                    lines.clear()
                    rHeight = visibleHeight
                    titleLineCount = 0
                    continue
                }
                if (showTitle) {
                    wordCount = titlePaint.breakText(paragraph, true, visibleWidth, null)
                } else {
                    wordCount = textPaint.breakText(paragraph, true, visibleWidth, null)
                }
                subString = paragraph.substring(0, wordCount)
                if (subString != "\n") {
                    lines.add(subString)

                    // 设置段落间距
                    if (showTitle) {
                        titleLineCount += 1
                        rHeight -= titleInterval
                    } else {
                        rHeight -= textInterval
                    }
                }
                // 裁剪
                paragraph = paragraph.substring(wordCount)
            }

            // 增加段落的间距
            if (!showTitle && lines.size != 0) {
                rHeight = rHeight - textPara + textInterval
            }

            if (showTitle) {
                rHeight = rHeight - titlePara + titleInterval
                showTitle = false
            }

        }
        if (lines.isNotEmpty()) {
            val page = Page()
            page.position = pages.size
            page.title = chapter.title
            page.titleLines = titleLineCount
            page.lines = ArrayList(lines)
            pages.add(page)
            lines.clear()
        }
        return pages
    }
}