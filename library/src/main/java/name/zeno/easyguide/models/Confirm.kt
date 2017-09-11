package name.zeno.easyguide.models

import android.view.View

/**
 * 确定按钮的信息携带
 */
class Confirm @JvmOverloads constructor(
    var text: String,
    var textSize: Int = 13,
    var listener: View.OnClickListener? = null
)
