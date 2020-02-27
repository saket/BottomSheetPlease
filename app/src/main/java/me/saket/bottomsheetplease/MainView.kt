package me.saket.bottomsheetplease

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.Button
import androidx.core.view.doOnLayout
import com.squareup.contour.ContourLayout
import me.saket.bottomsheetplease.shiet.BottomShietOverlay
import me.saket.bottomsheetplease.shiet.BottomShietState

@SuppressLint("SetTextI18n", "CheckResult")
class MainView(context: Context) : ContourLayout(context) {

  private val expandedButton = Button(context).apply {
    text = "Expanded"
    applyLayout(
        x = leftTo { parent.left() + 16.xdip },
        y = topTo { parent.top() + 16.ydip }
    )
  }

  private val peekButton = Button(context).apply {
    text = "Peeking"
    applyLayout(
        x = leftTo { expandedButton.right() },
        y = topTo { expandedButton.top() }
    )
  }

  private val hiddenButton = Button(context).apply {
    text = "Hidden"
    applyLayout(
        x = leftTo { peekButton.right() },
        y = topTo { peekButton.top() }
    )
  }

  private val sheetOverlay = BottomShietOverlay(context).apply {
    peekHeight = 200.dip
    setState(BottomShietState.PEEKING)

    setBackgroundColor(Color.CYAN)
    applyLayout(
        x = matchParentX(),
        y = matchParentY()
    )
  }

  init {
    setBackgroundColor(context.getColor(R.color.gray_200))

    val moveToState = { state: BottomShietState ->
      if (sheetOverlay.childCount == 0) {
        val sheetView = BatmanSheetView(context)
        sheetOverlay.addView(sheetView)
      }
      sheetOverlay.setState(state)
    }

    expandedButton.setOnClickListener {
      moveToState(BottomShietState.EXPANDED)
    }

    peekButton.setOnClickListener {
      moveToState(BottomShietState.PEEKING)
    }

    hiddenButton.setOnClickListener {
      moveToState(BottomShietState.HIDDEN)
    }

    sheetOverlay.setOnClickListener {
//      Timber.w("removing sheet")
//      sheetOverlay.removeAllViews()
    }

    doOnLayout {
      peekButton.performClick()
    }
  }
}