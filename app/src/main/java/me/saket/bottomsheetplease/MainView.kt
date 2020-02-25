package me.saket.bottomsheetplease

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.squareup.contour.ContourLayout

@SuppressLint("SetTextI18n")
class MainView(context: Context) : ContourLayout(context) {

  private val toggleWithoutAnimButton = Button(context).apply {
    text = "Toggle without anim"
    applyLayout(
        x = centerHorizontallyTo { parent.centerX() },
        y = topTo { parent.top() + 16.ydip }
    )
  }

  private val toggleWithAnimButton = Button(context).apply {
    text = "Toggle with anim"
    applyLayout(
        x = centerHorizontallyTo { parent.centerX() },
        y = topTo { toggleWithoutAnimButton.bottom() }
    )
  }

  private val sheetContainer = CoordinatorLayout(context).apply {
    applyLayout(
        x = matchParentX(),
        y = matchParentY()
    )
  }

  init {
    setBackgroundColor(context.getColor(R.color.gray_200))

    toggleWithAnimButton.setOnClickListener {
      toggleSheet(withAnim = true)
    }
    toggleWithoutAnimButton.setOnClickListener {
      toggleSheet(withAnim = false)
    }
    sheetContainer.setOnClickListener {
      hideSheet()
    }
  }

  private fun toggleSheet(withAnim: Boolean) = when {
    sheetContainer.childCount > 0 -> hideSheet()
    else -> showSheet(withAnim)
  }

  private fun showSheet(withAnim: Boolean) {
    val sheetView = SheetView(context)
    val sheetBehavior = BottomSheetBehavior<View>(context, null)

    val sheetParams = CoordinatorLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
      behavior = sheetBehavior
    }
    sheetContainer.addView(sheetView, sheetParams)

    sheetBehavior.isHideable = true

    if (withAnim) {
      sheetBehavior.state = STATE_HIDDEN
      sheetView.post { sheetBehavior.state = STATE_EXPANDED }
    } else {
      sheetBehavior.state = STATE_EXPANDED
    }

    sheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
      override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
      override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (newState == STATE_HIDDEN) {
          hideSheet()
        }
      }
    })
  }

  private fun hideSheet() {
    sheetContainer.removeAllViews()
  }
}