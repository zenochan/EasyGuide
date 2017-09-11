package name.zeno.easyguide.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import name.zeno.easyguide.EasyGuide
import name.zeno.easyguide.support.HShape

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    tv.setOnClickListener {
      EasyGuide.Builder(this)
          .addHighlightArea(it, HShape.RECTANGLE)
          .addMessage("haha哈哈", 12)
          .build()
          .show()

    }
  }
}
