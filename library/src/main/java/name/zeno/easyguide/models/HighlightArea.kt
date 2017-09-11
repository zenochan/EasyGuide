package name.zeno.easyguide.models

import android.graphics.RectF
import android.view.View
import name.zeno.easyguide.support.HShape

/** 高亮区域显示 */
class HighlightArea @JvmOverloads constructor(var targetView: View, var shape: HShape = HShape.RECTANGLE) {
  /** 高亮视图占据窗口的 rect */
  val rectF: RectF
    get() {
      val rectF = RectF()
      val location = IntArray(2)
      targetView.getLocationOnScreen(location)
      rectF.left = location[0].toFloat()
      rectF.top = location[1].toFloat()
      rectF.right = (location[0] + targetView.width).toFloat()
      rectF.bottom = (location[1] + targetView.height).toFloat()
      return rectF
    }
}
