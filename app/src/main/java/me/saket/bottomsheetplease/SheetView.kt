package me.saket.bottomsheetplease

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.jakewharton.rxbinding3.view.detaches
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import java.util.concurrent.TimeUnit.MILLISECONDS

@SuppressLint("CheckResult")
class SheetView(context: Context) : AppCompatTextView(context) {

  private val dimensionsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    color = Color.DKGRAY
    strokeWidth = dip(2).toFloat()
  }

  init {
    val padding = dip(24)
    setPadding(padding, padding, padding, padding)
    setBackgroundColor(Color.YELLOW)

    height = dip(100)

    // Change the height while the sheet's entry animation is ongoing.
    Observable.timer(150, MILLISECONDS, mainThread())
        .takeUntil(detaches())
        .subscribe {
          height = dip(400)
        }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawLine(0f, 0f, width.toFloat(), height.toFloat(), dimensionsPaint)
    canvas.drawLine(width.toFloat(), 0f, 0f, height.toFloat(), dimensionsPaint)
  }
}

fun View.dip(value: Int): Int =
  TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      value.toFloat(),
      resources.displayMetrics
  ).toInt()