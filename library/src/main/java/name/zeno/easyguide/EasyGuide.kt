package name.zeno.easyguide

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.*
import android.view.MotionEvent.ACTION_UP
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import com.yuyh.library.R
import name.zeno.easyguide.models.Confirm
import name.zeno.easyguide.models.HighlightArea
import name.zeno.easyguide.models.Message
import name.zeno.easyguide.models.Tips
import name.zeno.easyguide.support.Constants
import name.zeno.easyguide.support.HShape
import java.util.*

@Suppress("unused")
/**
 * 新手引导
 */
class EasyGuide @JvmOverloads constructor(
    private val activity: Activity,
    private val mAreas: List<HighlightArea> = emptyList<HighlightArea>(),
    private val mIndicators: List<Tips>? = emptyList<Tips>(),
    private val mMessages: List<Message>? = emptyList<Message>(),
    private val mConfirm: Confirm? = null,
    private val dismissAnyWhere: Boolean = false,
    private val performViewClick: Boolean = false) {

  private val parentView: FrameLayout = this.activity.window.decorView as FrameLayout
  private lateinit var guideView: EasyGuideView
  private lateinit var tipContainer: LinearLayout

  private var onShow: Function0<Unit>? = null
  private var onDismiss: Function0<Unit>? = null
  private var onHighlightClick: Function1<View, Unit>? = null

  /**
   * 显示引导提示
   */
  fun show() {
    guideView = EasyGuideView(activity)
    guideView.setHighlightAreas(mAreas)

    tipContainer = LinearLayout(activity)
    tipContainer.gravity = Gravity.CENTER_HORIZONTAL
    tipContainer.layoutParams = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    tipContainer.orientation = LinearLayout.VERTICAL

    if (mIndicators != null) {
      for (tipsView in mIndicators) {
        addView(tipsView.view, tipsView.offsetX, tipsView.offsetY, tipsView.params)
      }
    }

    if (mMessages != null) {
      val padding = dip2px(5f)
      for (message in mMessages) {
        val tvMsg = TextView(activity)
        tvMsg.layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        tvMsg.setPadding(padding, padding, padding, padding)
        tvMsg.gravity = Gravity.CENTER
        tvMsg.text = message.message
        tvMsg.setTextColor(Color.WHITE)
        tvMsg.textSize = (if (message.textSize == -1) 12 else message.textSize).toFloat()

        tipContainer.addView(tvMsg)
      }
    }

    if (mConfirm != null) {
      val tvConfirm = TextView(activity)
      tvConfirm.gravity = Gravity.CENTER
      tvConfirm.text = mConfirm.text
      tvConfirm.setTextColor(Color.WHITE)
      tvConfirm.textSize = (if (mConfirm.textSize == -1) 13 else mConfirm.textSize).toFloat()
      tvConfirm.setBackgroundResource(R.drawable.btn_selector)
      val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
      params.topMargin = dip2px(10f)
      tvConfirm.layoutParams = params
      val lr = dip2px(8f)
      val tb = dip2px(5f)
      tvConfirm.setPadding(lr, tb, lr, tb)

      tvConfirm.setOnClickListener {
        if (mConfirm.listener != null) {
          mConfirm.listener!!.onClick(it)
        } else {
          dismiss()
        }
      }
      tipContainer.addView(tvConfirm)
    }

//    addView(tipContainer, Constants.CENTER, Constants.CENTER, RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    val lp = RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
    addView(tipContainer, Constants.CENTER, Constants.CENTER, lp)

    parentView.addView(guideView, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

    if (dismissAnyWhere || performViewClick) {
      guideView.setOnTouchListener(View.OnTouchListener { _, event ->
        if (event.action != ACTION_UP) return@OnTouchListener true

        if (mAreas.isNotEmpty()) {
          mAreas.map { it.targetView }.forEach {
            if (inRangeOfView(it, event)) {
              if (performViewClick) it.performClick()
              onHighlightClick?.invoke(it)
              dismiss()
              return@OnTouchListener false
            }
          }

          if (dismissAnyWhere) dismiss()
          return@OnTouchListener false
        } else {
          dismiss()
          return@OnTouchListener false
        }
      })
    }

    onShow?.invoke()
  }

  fun showOnViewReady(view: View) {
    var listener: ViewTreeObserver.OnGlobalLayoutListener? = null
    listener = ViewTreeObserver.OnGlobalLayoutListener {
      if (Build.VERSION.SDK_INT >= 16) {
        view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
      } else {
        @Suppress("DEPRECATION")
        view.viewTreeObserver.removeGlobalOnLayoutListener(listener)
      }
      show()
    }
    view.viewTreeObserver.addOnGlobalLayoutListener(listener)
  }

  /** 取消引导提示 */
  fun dismiss() {
    if (parentView.indexOfChild(guideView) > 0) {
      parentView.removeView(guideView)
      onDismiss?.invoke()
    }
  }

  /**
   * 添加任意 View 到引导提示的布局上
   * @param offsetX X轴偏移，正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量。[Constants.CENTER]表示居中
   * @param offsetY Y轴偏移，正数表示从上往下，负数表示从下往上。[Constants.CENTER]表示居中
   * @param params  参数
   */
  private fun addView(view: View, offsetX: Int, offsetY: Int, params: RelativeLayout.LayoutParams?) {
    var lp = params
    if (lp == null) {
      lp = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

      when (offsetX) {
        Constants.CENTER -> lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        in 0..Constants.CENTER -> lp.leftMargin = offsetX
        else -> {
          lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
          lp.rightMargin = -offsetX
        }
      }

      when (offsetY) {
        Constants.CENTER -> lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
        in 0..Constants.CENTER -> lp.topMargin = offsetY
        else -> {
          lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
          lp.bottomMargin = -offsetY
        }
      }
    }

    guideView.addView(view, lp)
  }

  val isShowing: Boolean
    get() = parentView.indexOfChild(guideView) > 0


  fun inRangeOfView(view: View, ev: MotionEvent): Boolean {
    val location = IntArray(2)
    view.getLocationOnScreen(location)
    val x = location[0]
    val y = location[1]
    if (ev.x < x || ev.x > x + view.width || ev.y < y || ev.y > y + view.height) {
      return false
    }
    return true
  }

  @Suppress("unused")
  class Builder(internal var activity: Activity) {

    internal var areas: MutableList<HighlightArea> = ArrayList()
    internal var views: MutableList<Tips> = ArrayList()
    internal var messages: MutableList<Message> = ArrayList()

    internal var confirm: Confirm? = null

    internal var dismissAnyWhere = true
    internal var performViewClick: Boolean = false

    private var onShow: (() -> Unit)? = null
    private var onDismiss: (() -> Unit)? = null
    private var onHighlightClick: ((view: View) -> Unit)? = null

    /** 添加高亮区域 */
    fun addHighlightArea(view: View, shape: HShape): Builder {
      val area = HighlightArea(view, shape)
      areas.add(area)
      return this
    }

    fun addHightLightArea(area: HighlightArea): Builder {
      areas.add(area)
      return this
    }

    /**
     * 添加箭头指示的图片资源
     * @param offX X轴偏移 正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量。[Constants.CENTER]表示居中
     * @param offY Y轴偏移 正数表示从布局的上侧往下偏移量，负数表示从布局的下侧往上偏移量。[Constants.CENTER]表示居中
     */
    fun addIndicator(resId: Int, offX: Int, offY: Int): Builder {
      val ivIndicator = ImageView(activity)
      ivIndicator.setImageResource(resId)
      return addView(ivIndicator, offX, offY)
    }

    fun addView(view: View, offX: Int, offY: Int): Builder {
      views.add(Tips(view, offX, offY))
      return this
    }

    /**
     * 添加任意的View
     * @param offX   X轴偏移 正数表示从布局的左侧往右偏移量，负数表示从布局的右侧往左偏移量。[Constants.CENTER]表示居中
     * @param offY   Y轴偏移 正数表示从布局的上侧往下偏移量，负数表示从布局的下侧往上偏移量。[Constants.CENTER]表示居中
     * @param params 参数
     */
    fun addView(view: View, offX: Int, offY: Int, params: RelativeLayout.LayoutParams): Builder {
      views.add(Tips(view, offX, offY, params))
      return this
    }

    /**
     * 添加提示信息，默认居中显示
     */
    fun addMessage(message: String, textSize: Int): Builder {
      messages.add(Message(message, textSize))
      return this
    }

    /** 添加确定按钮，默认居中显示在提示信息下方 */
    @JvmOverloads
    fun positive(text: String, textSize: Int = 0, listener: View.OnClickListener? = null): Builder {
      this.confirm = Confirm(text, textSize, listener)
      return this
    }

    /** 点击任意区域消失 */
    fun dismissAnyWhere(): Builder {
      this.dismissAnyWhere = true
      return this
    }

    /** 点击高亮区域执行高亮控件点击 */
    fun performViewClick(): Builder {
      this.performViewClick = true
      return this
    }

    fun onShow(onShow: (() -> Unit)?) = {
      this.onShow = onShow
      this
    }

    fun onDismiss(onDismiss: (() -> Unit)?) = {
      this.onDismiss = onDismiss
      this
    }

    fun onHighlightClick(onHighlightClick: ((view: View) -> Unit)?) = {
      this.onHighlightClick = onHighlightClick
      this
    }

    fun build(): EasyGuide {
      val easyGuide = EasyGuide(activity, areas, views, messages, confirm, dismissAnyWhere, performViewClick)
      easyGuide.onDismiss = this.onDismiss
      easyGuide.onShow = this.onShow
      easyGuide.onHighlightClick = this.onHighlightClick
      return easyGuide
    }
  }
}
