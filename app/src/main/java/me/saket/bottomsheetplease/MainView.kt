package me.saket.bottomsheetplease

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.Button
import com.jakewharton.rxbinding3.view.detaches
import com.squareup.contour.ContourLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import me.saket.bottomsheetplease.shiet.BottomShietOverlay
import me.saket.bottomsheetplease.shiet.BottomShietState.Expanded
import me.saket.bottomsheetplease.shiet.BottomShietState.Hidden
import me.saket.bottomsheetplease.shiet.BottomShietState.Peeking
import java.util.concurrent.TimeUnit.SECONDS

@SuppressLint("SetTextI18n", "CheckResult")
class MainView(context: Context) : ContourLayout(context) {

  private val toggleButton = Button(context).apply {
    text = "Toggle sheet"
    applyLayout(
        x = centerHorizontallyTo { parent.centerX() },
        y = topTo { parent.top() + 16.ydip }
    )
  }

  private val sheetStates = listOf(
      Hidden,
      Peeking(height = 200.dip),
      Expanded
  )

  private val sheetOverlay = BottomShietOverlay(context, sheetStates).apply {
    setBackgroundColor(Color.CYAN)
    applyLayout(
        x = matchParentX(),
        y = matchParentY()
    )
  }

  init {
    setBackgroundColor(context.getColor(R.color.gray_200))

    toggleButton.setOnClickListener {
      toggleSheet(withAnim = true)
    }
    sheetOverlay.setOnClickListener {
      hideSheet()
    }

    Observable.timer(1, SECONDS, mainThread())
        .takeUntil(detaches())
        .subscribe {
          showSheet(withAnim = true)
        }
  }

  private fun toggleSheet(withAnim: Boolean) = when {
    sheetOverlay.childCount > 0 -> hideSheet()
    else -> showSheet(withAnim)
  }

  private fun showSheet(withAnim: Boolean) {
    val sheetView = BatmanSheetView(context)
    sheetOverlay.addView(sheetView)
  }

  private fun hideSheet() {
    sheetOverlay.removeAllViews()
  }
}