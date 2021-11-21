package top.mcwebsite.novel.ui.read.view.animation

import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.View
import java.lang.Exception
import kotlin.io.path.moveTo
import kotlin.math.atan2
import kotlin.math.hypot

class SimulationPageAnim(
    w: Int, h: Int, view: View?, listener: OnPageChangeListener
) : HorizontalPageAnim(w, h, 0, 0, view, listener) {
    private var mCornerX = 1 // 拖拽点对应的页脚
    private var mCornerY = 1
    private val mPath0: Path = Path()
    private val mPath1: Path = Path()
    private val mBezierStart1: PointF = PointF() // 贝塞尔曲线起始点
    private val mBezierControl1: PointF = PointF() // 贝塞尔曲线控制点
    private val mBeziervertex1: PointF = PointF() // 贝塞尔曲线顶点
    private var mBezierEnd1: PointF = PointF() // 贝塞尔曲线结束点
    private val mBezierStart2: PointF = PointF() // 另一条贝塞尔曲线
    private val mBezierControl2: PointF = PointF()
    private val mBeziervertex2: PointF = PointF()
    private var mBezierEnd2: PointF = PointF()
    private var mMiddleX = 0f
    private var mMiddleY = 0f
    private var mDegrees = 0f
    private var mTouchToCornerDis = 0f
    private val mColorMatrixFilter: ColorMatrixColorFilter
    private val mMatrix: Matrix
    private val mMatrixArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 1.0f)
    private var mIsRTandLB // 是否属于右上左下
            = false
    private val mMaxLength: Float = hypot(viewWidth.toDouble(), viewHeight.toDouble()).toFloat()
    private var mBackShadowDrawableLR // 有阴影的GradientDrawable
            : GradientDrawable? = null
    private var mBackShadowDrawableRL: GradientDrawable? = null
    private var mFolderShadowDrawableLR: GradientDrawable? = null
    private var mFolderShadowDrawableRL: GradientDrawable? = null
    private var mFrontShadowDrawableHBT: GradientDrawable? = null
    private var mFrontShadowDrawableHTB: GradientDrawable? = null
    private var mFrontShadowDrawableVLR: GradientDrawable? = null
    private var mFrontShadowDrawableVRL: GradientDrawable? = null
    private val mPaint: Paint = Paint()

    // 适配 android 高版本无法使用 XOR 的问题
    private val mXORPath: Path = Path()

    override fun drawMove(canvas: Canvas) {
        when (direction) {
            Direction.NEXT -> {
                calcPoints()
                drawCurrentPageArea(canvas, curBitmap, mPath0)
                drawNextPageAreaAndShadow(canvas, mNextBitmap)
                drawCurrentPageShadow(canvas)
                drawCurrentBackArea(canvas, curBitmap)
            }
            else -> {
                calcPoints()
                drawCurrentPageArea(canvas, mNextBitmap, mPath0)
                drawNextPageAreaAndShadow(canvas, curBitmap)
                drawCurrentPageShadow(canvas)
                drawCurrentBackArea(canvas, mNextBitmap)
            }
        }
    }

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            mNextBitmap = curBitmap.copy(Bitmap.Config.RGB_565, true)
            canvas.drawBitmap(curBitmap, 0F, 0F, null)
        } else {
            canvas.drawBitmap(mNextBitmap, 0F, 0F, null)
        }
    }

    override fun startAnim() {
        super.startAnim()
        var dx: Int
        val dy: Int
        // dx 水平方向滑动的距离，负值会使滚动向左滚动
        // dy 垂直方向滑动的距离，负值会使滚动向上滚动
        if (isCancel) {
            if (mCornerX > 0 && direction == Direction.NEXT) {
                dx = ((viewWidth - touchX).toInt())
            } else {
                dx = (-touchX).toInt()
            }
            if (direction != Direction.NEXT) {
                dx = (-(viewWidth + touchX)).toInt()
            }
            if (mCornerY > 0) {
                dy = ((viewHeight - touchY).toInt())
            } else {
                dy = (-touchY).toInt() // 防止touchY最终变为0
            }
        } else {
            if (mCornerX > 0 && direction == Direction.NEXT) {
                dx = (-(viewWidth + touchX)).toInt()
            } else {
                dx = ((viewWidth - touchX + viewWidth).toInt())
            }
            if (mCornerY > 0) {
                dy = ((viewHeight - touchY).toInt())
            } else {
                dy = (1 - touchY).toInt() // 防止touchY最终变为0
            }
        }
        scroller.startScroll(touchX.toInt(), touchY.toInt(), dx, dy, 400)
    }

    override var direction: Direction = Direction.NONE
        set(value) {
            field = value
            when (direction) {
                Direction.PRE ->                 //上一页滑动不出现对角
                    if (startX > viewWidth / 2) {
                        calcCornerXY(startX, viewHeight.toFloat())
                    } else {
                        calcCornerXY(viewWidth - startX, viewHeight.toFloat())
                    }
                Direction.NEXT -> if (viewWidth / 2 > startX) {
                    calcCornerXY(viewWidth - startX, startY)
                }
                else -> {
                }
            }
    }



    override fun setStartPoint(x: Float, y: Float) {
        super.setStartPoint(x, y)
        calcCornerXY(x, y)
    }

    override fun setTouchPoint(x: Float, y: Float) {
        super.setTouchPoint(x, y)
        //触摸y中间位置吧y变成屏幕高度
        if (startY > viewHeight / 3 && startY < viewHeight * 2 / 3 || direction.equals(
                Direction.PRE
            )
        ) {
            touchY = viewHeight.toFloat()
        }
        if (startY > viewHeight / 3 && startY < viewHeight / 2 && direction.equals(
                Direction.NEXT
            )
        ) {
            touchY = 1F
        }
    }

    /**
     * 创建阴影的GradientDrawable
     */
    private fun createDrawable() {
        val color = intArrayOf(0x333333, -0x4fcccccd)
        mFolderShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, color
        ).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }

        mFolderShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, color
        ).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
        // 背面颜色组
        val mBackShadowColors = intArrayOf(-0xeeeeef, 0x111111)
        mBackShadowDrawableRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mBackShadowColors
        ).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }

        mBackShadowDrawableLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mBackShadowColors
        ).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }

        // 前面颜色组
        val mFrontShadowColors = intArrayOf(-0x7feeeeef, 0x111111)
        mFrontShadowDrawableVLR = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, mFrontShadowColors
        ).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }

        mFrontShadowDrawableVRL = GradientDrawable(
            GradientDrawable.Orientation.RIGHT_LEFT, mFrontShadowColors
        ).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }

        mFrontShadowDrawableHTB = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM, mFrontShadowColors
        ).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }


        mFrontShadowDrawableHBT = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP, mFrontShadowColors
        ).apply {
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
    }

    /**
     * 是否能够拖动过去
     *
     * @return
     */
    fun canDragOver(): Boolean {
        return mTouchToCornerDis > viewWidth / 10
    }

    fun right(): Boolean {
        return mCornerX <= -4
    }

    /**
     * 绘制翻起页背面
     *
     * @param canvas
     * @param bitmap
     */
    private fun drawCurrentBackArea(canvas: Canvas, bitmap: Bitmap) {
        val i = (mBezierStart1.x + mBezierControl1.x).toInt() / 2
        val f1: Float = Math.abs(i - mBezierControl1.x)
        val i1 = (mBezierStart2.y + mBezierControl2.y).toInt() / 2
        val f2: Float = Math.abs(i1 - mBezierControl2.y)
        val f3 = Math.min(f1, f2)
        mPath1.reset()
        mPath1.moveTo(mBeziervertex2.x, mBeziervertex2.y)
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y)
        mPath1.lineTo(mBezierEnd1.x, mBezierEnd1.y)
        mPath1.lineTo(touchX, touchY)
        mPath1.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath1.close()
        val mFolderShadowDrawable: GradientDrawable?
        val left: Int
        val right: Int
        if (mIsRTandLB) {
            left = ((mBezierStart1.x - 1).toInt())
            right = ((mBezierStart1.x + f3 + 1).toInt())
            mFolderShadowDrawable = mFolderShadowDrawableLR
        } else {
            left = ((mBezierStart1.x - f3 - 1).toInt())
            right = ((mBezierStart1.x + 1).toInt())
            mFolderShadowDrawable = mFolderShadowDrawableRL
        }
        canvas.save()
        try {
            canvas.clipPath(mPath0)
            canvas.clipPath(mPath1, Region.Op.INTERSECT)
        } catch (e: Exception) {
        }
        mPaint.setColorFilter(mColorMatrixFilter)
        //对Bitmap进行取色
        val color: Int = bitmap.getPixel(1, 1)
        //获取对应的三色
        val red = color and 0xff0000 shr 16
        val green = color and 0x00ff00 shr 8
        val blue = color and 0x0000ff
        //转换成含有透明度的颜色
        val tempColor: Int = Color.argb(200, red, green, blue)
        val dis = Math.hypot(
            (mCornerX - mBezierControl1.x).toDouble(),
            (mBezierControl2.y - mCornerY).toDouble()
        ).toFloat()
        val f8: Float = (mCornerX - mBezierControl1.x) / dis
        val f9: Float = (mBezierControl2.y - mCornerY) / dis
        mMatrixArray[0] = 1 - 2 * f9 * f9
        mMatrixArray[1] = 2 * f8 * f9
        mMatrixArray[3] = mMatrixArray[1]
        mMatrixArray[4] = 1 - 2 * f8 * f8
        mMatrix.reset()
        mMatrix.setValues(mMatrixArray)
        mMatrix.preTranslate(-mBezierControl1.x, -mBezierControl1.y)
        mMatrix.postTranslate(mBezierControl1.x, mBezierControl1.y)
        canvas.drawBitmap(bitmap, mMatrix, mPaint)
        //背景叠加
        canvas.drawColor(tempColor)
        mPaint.colorFilter = null
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
        mFolderShadowDrawable?.setBounds(
            left, mBezierStart1.y.toInt(), right,
            (mBezierStart1.y + mMaxLength).toInt()
        )
        mFolderShadowDrawable?.draw(canvas)
        canvas.restore()
    }

    /**
     * 绘制翻起页的阴影
     *
     * @param canvas
     */
    private fun drawCurrentPageShadow(canvas: Canvas) {
        val degree: Double
        if (mIsRTandLB) {
            degree = Math.PI / 4 - atan2(
                (mBezierControl1.y - touchY).toDouble(), (touchX - mBezierControl1.x).toDouble()
            )
        } else {
            degree = Math.PI / 4 - atan2(
                (touchY - mBezierControl1.y).toDouble(), (touchX - mBezierControl1.x).toDouble()
            )
        }
        // 翻起页阴影顶点与touch点的距离
        val d1 = 25.toFloat() * 1.414 * Math.cos(degree)
        val d2 = 25.toFloat() * 1.414 * Math.sin(degree)
        val x = (touchX + d1).toFloat()
        val y: Float
        if (mIsRTandLB) {
            y = ((touchY + d2).toFloat())
        } else {
            y = ((touchY - d2).toFloat())
        }
        mPath1.reset()
        mPath1.moveTo(x, y)
        mPath1.lineTo(touchX, touchY)
        mPath1.lineTo(mBezierControl1.x, mBezierControl1.y)
        mPath1.lineTo(mBezierStart1.x, mBezierStart1.y)
        mPath1.close()
        var rotateDegrees: Float
        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mXORPath.reset()
                mXORPath.moveTo(0f, 0f)
                mXORPath.lineTo(canvas.width.toFloat(), 0f)
                mXORPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
                mXORPath.lineTo(0f, canvas.height.toFloat())
                mXORPath.close()

                // 取 path 的补集，作为 canvas 的交集
                mXORPath.op(mPath0, Path.Op.XOR)
                canvas.clipPath(mXORPath)
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR)
            }
            canvas.clipPath(mPath1, Region.Op.INTERSECT)
        } catch (e: Exception) {
            // TODO: handle exception
        }
        var leftx: Int
        var rightx: Int
        var mCurrentPageShadow: GradientDrawable?
        if (mIsRTandLB) {
            leftx = mBezierControl1.x.toInt()
            rightx = mBezierControl1.x.toInt() + 25
            mCurrentPageShadow = mFrontShadowDrawableVLR
        } else {
            leftx = ((mBezierControl1.x - 25).toInt())
            rightx = mBezierControl1.x.toInt() + 1
            mCurrentPageShadow = mFrontShadowDrawableVRL
        }
        rotateDegrees = Math.toDegrees(
            atan2((touchX - mBezierControl1.x).toDouble(), (mBezierControl1.y - touchY).toDouble())
        ).toFloat()
        canvas.rotate(rotateDegrees, mBezierControl1.x, mBezierControl1.y)
        mCurrentPageShadow?.setBounds(
            leftx,
            (mBezierControl1.y - mMaxLength).toInt(), rightx,
            (mBezierControl1.y).toInt()
        )
        mCurrentPageShadow?.draw(canvas)
        canvas.restore()
        mPath1.reset()
        mPath1.moveTo(x, y)
        mPath1.lineTo(touchX, touchY)
        mPath1.lineTo(mBezierControl2.x, mBezierControl2.y)
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1.close()
        canvas.save()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mXORPath.reset()
                mXORPath.moveTo(0f, 0f)
                mXORPath.lineTo(canvas.width.toFloat(), 0f)
                mXORPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
                mXORPath.lineTo(0f, canvas.height.toFloat())
                mXORPath.close()

                // 取 path 的补给，作为 canvas 的交集
                mXORPath.op(mPath0, Path.Op.XOR)
                canvas.clipPath(mXORPath)
            } else {
                canvas.clipPath(mPath0, Region.Op.XOR)
            }
            canvas.clipPath(mPath1, Region.Op.INTERSECT)
        } catch (e: Exception) {
        }
        if (mIsRTandLB) {
            leftx = (mBezierControl2.y.toInt())
            rightx = ((mBezierControl2.y + 25).toInt())
            mCurrentPageShadow = mFrontShadowDrawableHTB
        } else {
            leftx = ((mBezierControl2.y - 25).toInt())
            rightx = ((mBezierControl2.y + 1).toInt())
            mCurrentPageShadow = mFrontShadowDrawableHBT
        }
        rotateDegrees = Math.toDegrees(
            atan2(
                (mBezierControl2.y - touchY).toDouble(),
                (mBezierControl2.x - touchX).toDouble()
            )
        ).toFloat()
        canvas.rotate(rotateDegrees, mBezierControl2.x, mBezierControl2.y)
        val temp: Float
        if (mBezierControl2.y < 0) temp = mBezierControl2.y - viewHeight else temp =
            mBezierControl2.y
        val hmg = hypot(mBezierControl2.x.toDouble(), temp.toDouble()).toInt()
        if (hmg > mMaxLength) {
            mCurrentPageShadow?.setBounds(
                (mBezierControl2.x - 25).toInt() - hmg, leftx,
                (mBezierControl2.x + mMaxLength).toInt() - hmg,
                rightx
            )
        } else mCurrentPageShadow?.setBounds(
            (mBezierControl2.x - mMaxLength).toInt(), leftx,
            (mBezierControl2.x).toInt(), rightx
        )
        mCurrentPageShadow?.draw(canvas)
        canvas.restore()
    }

    private fun drawNextPageAreaAndShadow(canvas: Canvas, bitmap: Bitmap) {
        mPath1.reset()
        mPath1.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath1.lineTo(mBeziervertex1.x, mBeziervertex1.y)
        mPath1.lineTo(mBeziervertex2.x, mBeziervertex2.y)
        mPath1.lineTo(mBezierStart2.x, mBezierStart2.y)
        mPath1.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath1.close()
        mDegrees = Math.toDegrees(
            atan2(
                (mBezierControl1.x - mCornerX).toDouble(),
                (mBezierControl2.y - mCornerY).toDouble()
            )
        ).toFloat()
        val leftx: Int
        val rightx: Int
        val mBackShadowDrawable: GradientDrawable?
        if (mIsRTandLB) {  //左下及右上
            leftx = (mBezierStart1.x.toInt())
            rightx = ((mBezierStart1.x + mTouchToCornerDis / 4).toInt())
            mBackShadowDrawable = mBackShadowDrawableLR
        } else {
            leftx = ((mBezierStart1.x - mTouchToCornerDis / 4).toInt())
            rightx = mBezierStart1.x.toInt()
            mBackShadowDrawable = mBackShadowDrawableRL
        }
        canvas.save()
        try {
            canvas.clipPath(mPath0)
            canvas.clipPath(mPath1, Region.Op.INTERSECT)
        } catch (e: Exception) {
        }
        canvas.drawBitmap(bitmap, 0F, 0F, null)
        canvas.rotate(mDegrees, mBezierStart1.x, mBezierStart1.y)
        mBackShadowDrawable?.setBounds(
            leftx, mBezierStart1.y.toInt(), rightx,
            (mMaxLength + mBezierStart1.y).toInt()
        ) //左上及右下角的xy坐标值,构成一个矩形
        mBackShadowDrawable?.draw(canvas)
        canvas.restore()
    }

    private fun drawCurrentPageArea(canvas: Canvas, bitmap: Bitmap, path: Path) {
        mPath0.reset()
        mPath0.moveTo(mBezierStart1.x, mBezierStart1.y)
        mPath0.quadTo(
            mBezierControl1.x, mBezierControl1.y, mBezierEnd1.x,
            mBezierEnd1.y
        )
        mPath0.lineTo(touchX, touchY)
        mPath0.lineTo(mBezierEnd2.x, mBezierEnd2.y)
        mPath0.quadTo(
            mBezierControl2.x, mBezierControl2.y, mBezierStart2.x,
            mBezierStart2.y
        )
        mPath0.lineTo(mCornerX.toFloat(), mCornerY.toFloat())
        mPath0.close()
        canvas.save()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mXORPath.reset()
            mXORPath.moveTo(0f, 0f)
            mXORPath.lineTo(canvas.width.toFloat(), 0f)
            mXORPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
            mXORPath.lineTo(0f, canvas.height.toFloat())
            mXORPath.close()

            // 取 path 的补给，作为 canvas 的交集
            mXORPath.op(path, Path.Op.XOR)
            canvas.clipPath(mXORPath)
        } else {
            canvas.clipPath(path, Region.Op.XOR)
        }
        canvas.drawBitmap(bitmap, 0F, 0F, null)
        try {
            canvas.restore()
        } catch (e: Exception) {
        }
    }

    /**
     * 计算拖拽点对应的拖拽脚
     *
     * @param x
     * @param y
     */
    fun calcCornerXY(x: Float, y: Float) {
        if (x <= viewWidth / 2) {
            mCornerX = 0
        } else {
            mCornerX = viewWidth
        }
        if (y <= viewHeight / 2) {
            mCornerY = 0
        } else {
            mCornerY = viewHeight
        }
        mIsRTandLB = ((mCornerX == 0 && mCornerY == viewHeight)
                || (mCornerX == viewWidth && mCornerY == 0))
    }

    private fun calcPoints() {
        mMiddleX = (touchX + mCornerX) / 2
        mMiddleY = (touchY + mCornerY) / 2
        mBezierControl1.x =
            mMiddleX - (mCornerY - mMiddleY) * (mCornerY - mMiddleY) / (mCornerX - mMiddleX)
        mBezierControl1.y = mCornerY.toFloat()
        mBezierControl2.x = mCornerX.toFloat()
        val f4 = mCornerY - mMiddleY
        if (f4 == 0f) {
            mBezierControl2.y = mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / 0.1f
        } else {
            mBezierControl2.y =
                mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / (mCornerY - mMiddleY)
        }
        mBezierStart1.x = mBezierControl1.x - (mCornerX - mBezierControl1.x) / 2
        mBezierStart1.y = mCornerY.toFloat()

        // 当mBezierStart1.x < 0或者mBezierStart1.x > 480时
        // 如果继续翻页，会出现BUG故在此限制
        if (touchX > 0 && touchX < viewWidth) {
            if (mBezierStart1.x < 0 || mBezierStart1.x > viewWidth) {
                if (mBezierStart1.x < 0) mBezierStart1.x = viewWidth - mBezierStart1.x
                val f1: Float = Math.abs(mCornerX - touchX)
                val f2: Float = viewWidth * f1 / mBezierStart1.x
                touchX = Math.abs(mCornerX - f2)
                val f3: Float = Math.abs(mCornerX - touchX) * Math.abs(mCornerY - touchY) / f1
                touchY = Math.abs(mCornerY - f3)
                mMiddleX = (touchX + mCornerX) / 2
                mMiddleY = (touchY + mCornerY) / 2
                mBezierControl1.x =
                    mMiddleX - (mCornerY - mMiddleY) * (mCornerY - mMiddleY) / (mCornerX - mMiddleX)
                mBezierControl1.y = mCornerY.toFloat()
                mBezierControl2.x = mCornerX.toFloat()
                val f5 = mCornerY - mMiddleY
                if (f5 == 0f) {
                    mBezierControl2.y =
                        mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / 0.1f
                } else {
                    mBezierControl2.y =
                        mMiddleY - (mCornerX - mMiddleX) * (mCornerX - mMiddleX) / (mCornerY - mMiddleY)
                }
                mBezierStart1.x = (mBezierControl1.x
                        - (mCornerX - mBezierControl1.x) / 2)
            }
        }
        mBezierStart2.x = mCornerX.toFloat()
        mBezierStart2.y = mBezierControl2.y - (mCornerY - mBezierControl2.y) / 2
        mTouchToCornerDis = hypot(
            ((touchX - mCornerX).toDouble()),
            ((touchY - mCornerY).toDouble())
        ).toFloat()
        mBezierEnd1 = getCross(
            PointF(touchX, touchY), mBezierControl1, mBezierStart1,
            mBezierStart2
        )
        mBezierEnd2 = getCross(
            PointF(touchX, touchY), mBezierControl2, mBezierStart1,
            mBezierStart2
        )
        mBeziervertex1.x = (mBezierStart1.x + (2 * mBezierControl1.x) + mBezierEnd1.x) / 4
        mBeziervertex1.y = ((2 * mBezierControl1.y) + mBezierStart1.y + mBezierEnd1.y) / 4
        mBeziervertex2.x = (mBezierStart2.x + (2 * mBezierControl2.x) + mBezierEnd2.x) / 4
        mBeziervertex2.y = ((2 * mBezierControl2.y) + mBezierStart2.y + mBezierEnd2.y) / 4
    }

    /**
     * 求解直线P1P2和直线P3P4的交点坐标
     *
     * @param P1
     * @param P2
     * @param P3
     * @param P4
     * @return
     */
    private fun getCross(P1: PointF, P2: PointF, P3: PointF, P4: PointF): PointF {
        val CrossP = PointF()
        // 二元函数通式： y=ax+b
        val a1: Float = (P2.y - P1.y) / (P2.x - P1.x)
        val b1: Float = ((P1.x * P2.y) - (P2.x * P1.y)) / (P1.x - P2.x)
        val a2: Float = (P4.y - P3.y) / (P4.x - P3.x)
        val b2: Float = ((P3.x * P4.y) - (P4.x * P3.y)) / (P3.x - P4.x)
        CrossP.x = (b2 - b1) / (a1 - a2)
        CrossP.y = a1 * CrossP.x + b1
        return CrossP
    }

    companion object {
        private val TAG = "SimulationPageAnim"
    }

    init {
        mPaint.style = Paint.Style.FILL
        createDrawable()
        val cm = ColorMatrix() //设置颜色数组
        val array = floatArrayOf(
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f,
            0f,
            0f,
            0f,
            0f,
            1f,
            0f
        )
        cm.set(array)
        mColorMatrixFilter = ColorMatrixColorFilter(cm)
        mMatrix = Matrix()
        touchX = 0.01f // 不让x,y为0,否则在点计算时会有问题
        touchY = 0.01f
    }
}
