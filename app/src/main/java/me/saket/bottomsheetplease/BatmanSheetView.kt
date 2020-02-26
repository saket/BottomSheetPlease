package me.saket.bottomsheetplease

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import kotlin.random.Random

@SuppressLint("CheckResult", "SetTextI18n")
class BatmanSheetView(context: Context) : NestedScrollView(context) {

  private val textView = TextView(context).apply {
    text = BATMAN_IPSUM
    setPadding(dip(16), dip(16), dip(16), dip(16))
    setBackgroundColor(Color.YELLOW)
  }

  private val dimensionsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.DKGRAY
    strokeWidth = dip(2).toFloat()
  }

  init {
    isFillViewport = true
    addView(textView)
    setPadding(dip(16), dip(16), dip(16), dip(16))
    setBackgroundColor(Color.BLUE)
    setWillNotDraw(false)

    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)

    // Change the height while the sheet's entry animation is ongoing.
    //Observable.interval(1, SECONDS, mainThread())
    //    .takeUntil(detaches())
    //    .subscribe {
    //      textView.text = BATMAN_IPSUM.substring(0, Random.nextInt(until = BATMAN_IPSUM.length / 2))
    //    }

    textView.setOnClickListener {
      textView.text = BATMAN_IPSUM.substring(0, Random.nextInt(until = BATMAN_IPSUM.length))
    }
  }

  override fun draw(canvas: Canvas) {
    super.draw(canvas)
    canvas.drawLine(0f, 0f, width.toFloat(), textView.height.toFloat() + paddingTop, dimensionsPaint)
    canvas.drawLine(width.toFloat(), 0f, 0f, textView.height.toFloat() + paddingTop, dimensionsPaint)
  }

  companion object {
    private val BATMAN_IPSUM = """
       I'll be standing where l belong. Between you and the peopIe of Gotham. I will go back to Gotham and I will fight men Iike this but I will not become an executioner.

       Bats frighten me. It's time my enemies shared my dread. It was a dog. It was a big dog. The first time I stole so that I wouldn't starve, yes. I lost many assumptions about the simple nature of right and wrong. And when I traveled I learned the fear before a crime and the thrill of success. But I never became one of them.

       I'm Batman Bruce Wayne, eccentric billionaire. I'm not wearing hockey pads. Well, you see... I'm buying this hotel and setting some new rules about the pool area. My anger outweights my guilt.

       This isn't a car. Swear to me! Bats frighten me. It's time my enemies shared my dread.
       
       I'm not wearing hockey pads. Bats frighten me. It's time my enemies shared my dread. I will go back to Gotham and I will fight men Iike this but I will not become an executioner.

       I will go back to Gotham and I will fight men Iike this but I will not become an executioner. I'm not wearing hockey pads.

       This isn't a car. Bats frighten me. It's time my enemies shared my dread. No guns, no killing.

       I'm Batman Well, you see... I'm buying this hotel and setting some new rules about the pool area. It was a dog. It was a big dog. I will go back to Gotham and I will fight men Iike this but I will not become an executioner.
    """.trimIndent()
  }
}

fun View.dip(value: Int): Int =
  TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      value.toFloat(),
      resources.displayMetrics
  ).toInt()