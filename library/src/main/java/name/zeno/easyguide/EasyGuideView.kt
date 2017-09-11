package name.zeno.easyguide

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.widget.RelativeLayout
import name.zeno.easyguide.models.HighlightArea
import name.zeno.easyguide.support.HShape

class EasyGuideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
  : RelativeLayout(context, attrs, defStyleAttr) {

  private val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
  private val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels

  private val outRect = RectF()
  private val bitmapRect: RectF = RectF()         // 所有高亮区域矩形的合并
  private val bgColor: Int = Color.parseColor("#aa000000")
  private var strokeWidth: Float = 0F
  private var bitmap: Bitmap? = null

  private lateinit var canvas: Canvas

  private val paint: Paint = Paint()
  private val mode: Xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
  private var highlightList: List<HighlightArea>? = null

  init {
    paint.isAntiAlias = true
    paint.color = bgColor
    paint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.INNER)

    setWillNotDraw(false)
    isClickable = true
  }

  private fun initCanvas() {
    var w = 10
    var h = 10
    if (bitmapRect.width() > 0 && bitmapRect.height() > 0) {
      w = bitmapRect.width().toInt()
      h = bitmapRect.height().toInt()
    }
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

    // 矩形最大边距
    strokeWidth = max(bitmapRect.left, bitmapRect.top, screenWidth - bitmapRect.right, screenHeight - bitmapRect.bottom)

    outRect.left = bitmapRect.left - strokeWidth / 2
    outRect.top = bitmapRect.top - strokeWidth / 2
    outRect.right = bitmapRect.right + strokeWidth / 2
    outRect.bottom = bitmapRect.bottom + strokeWidth / 2


    canvas = Canvas(bmp)
    canvas.drawColor(bgColor)

    bitmap?.recycle()
    bitmap = bmp
  }


  /**
   * 设置高亮区域
   */
  fun setHighlightAreas(list: List<HighlightArea>?) {
    highlightList = list
    if (list != null && list.isNotEmpty()) {
      list.map {
        // 合并矩形框
        bitmapRect.union(it.rectF)
      }
    }
    initCanvas()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val areas = highlightList
    if (areas == null || areas.isEmpty()) return

    paint.xfermode = mode
    paint.style = Paint.Style.FILL

    areas.forEach { area ->
      val rectF = area.rectF
      rectF.offset(-bitmapRect.left, -bitmapRect.top)
      when (area.shape) {
        HShape.CIRCLE -> {
          this.canvas.drawCircle(
              rectF.centerX(),
              rectF.centerY(),
              (Math.min(area.targetView.width, area.targetView.height) / 2).toFloat(),
              paint)
        }
        HShape.RECTANGLE -> this.canvas.drawRect(rectF, paint)
        HShape.OVAL -> this.canvas.drawOval(rectF, paint)
      }
    }

    canvas.drawBitmap(bitmap!!, bitmapRect.left, bitmapRect.top, null)
    //绘制剩余空间的矩形
    paint.xfermode = null
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = strokeWidth + 0.1F
    canvas.drawRect(outRect, paint)
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    // 回收 bitmap 释放内存
    bitmap?.recycle()
    bitmap = null
  }

  override fun performClick(): Boolean {
    return super.performClick()
  }
}
