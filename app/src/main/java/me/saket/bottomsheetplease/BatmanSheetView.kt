package me.saket.bottomsheetplease

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView

@SuppressLint("CheckResult", "SetTextI18n")
class BatmanSheetView(context: Context) : NestedScrollView(context) {

  private val textView = TextView(context).apply {
    text = BATMAN_IPSUM
    setPadding(dip(16), dip(16), dip(16), dip(16))
    setBackgroundColor(Color.YELLOW)
  }

  init {
    isFillViewport = true
    addView(textView)
    setPadding(dip(16), dip(16), dip(16), dip(16))
    setBackgroundColor(Color.BLUE)

    layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)

    textView.setOnClickListener {
      Toast.makeText(context, "Sheet clicked", Toast.LENGTH_SHORT).show()
    }

    // Change the height while the sheet's entry animation is ongoing.
//    Observable.timer(150, MILLISECONDS, mainThread())
//        .takeUntil(detaches())
//        .subscribe {
//          updateLayoutParams<LayoutParams> {
//            height = dip(200)
//          }
//        }
  }

  companion object {
    private val BATMAN_IPSUM = """
       I'll be standing where l belong. Between you and the peopIe of Gotham. I will go back to Gotham and I will fight men Iike this but I will not become an executioner.

       Bats frighten me. It's time my enemies shared my dread. It was a dog. It was a big dog. The first time I stole so that I wouldn't starve, yes. I lost many assumptions about the simple nature of right and wrong. And when I traveled I learned the fear before a crime and the thrill of success. But I never became one of them.

       I'm Batman Bruce Wayne, eccentric billionaire. I'm not wearing hockey pads. Well, you see... I'm buying this hotel and setting some new rules about the pool area. My anger outweights my guilt.

       This isn't a car. Swear to me! Bats frighten me. It's time my enemies shared my dread.
    """.trimIndent()
  }
}

fun View.dip(value: Int): Int =
  TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      value.toFloat(),
      resources.displayMetrics
  ).toInt()