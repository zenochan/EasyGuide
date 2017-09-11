package name.zeno.easyguide

import android.content.res.Resources

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/9/8
 */

fun max(vararg a: Float): Float {
  var max = a[0]
  a.map {
    max = Math.max(max, it)
  }
  return max
}

fun max(vararg a: Double): Double {
  var max = a[0]
  a.map {
    max = Math.max(max, it)
  }
  return max
}

fun max(vararg a: Int): Int {
  var max = a[0]
  a.map {
    max = Math.max(max, it)
  }
  return max
}

fun dip2px(dpValue: Float): Int {
  val scale = Resources.getSystem().displayMetrics.density
  return (dpValue * scale + 0.5f).toInt()
}
