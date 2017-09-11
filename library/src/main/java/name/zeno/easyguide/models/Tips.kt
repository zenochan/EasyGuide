package name.zeno.easyguide.models

import android.view.View
import android.widget.RelativeLayout

import name.zeno.easyguide.support.Constants

/**
 * 箭头视图数据
 */
class Tips @JvmOverloads constructor(
    val view: View,
    val offsetX: Int = Constants.CENTER,
    val offsetY: Int = Constants.CENTER,
    val params: RelativeLayout.LayoutParams? = null
)
