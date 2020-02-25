package me.saket.bottomsheetplease.shiet

import android.content.Context
import android.view.Gravity.BOTTOM
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import timber.log.Timber

class BottomShietOverlay(context: Context) : FrameLayout(context), SimpleNestedScrollingParent {

  private lateinit var shietView: View

  override fun onViewAdded(child: View) {
    super.onViewAdded(child)
    check(childCount == 1) { "Can only have one direct child that acts as the sheet." }

    shietView = child
    shietView.updateLayoutParams<LayoutParams> {
      gravity = BOTTOM
    }

    // TODO: introduce "peek" height instead.
    doOnPreDraw {
      shietView.offsetTopAndBottom((height - paddingTop - paddingBottom) / 2)
    }
  }

  override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
    return super<SimpleNestedScrollingParent>.onStartNestedScroll(child, target, nestedScrollAxes)
  }

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
    val consumedDy = tryConsumingNestedScroll(dy)
    Timber.i("consumedDy: $consumedDy")
    Timber.i("sheet y: ${shietView.top}")
    consumed[1] = consumedDy
  }

  private fun tryConsumingNestedScroll(dy: Int): Int {
    val isScrollingUpwards = dy > 0
    Timber.i("---------------------------")
    Timber.i("dy = $dy, isScrollingUpwards? $isScrollingUpwards")
    Timber.i("shietView.top: ${shietView.top}")
    Timber.i("top: $top, paddingTop: $paddingTop")

    val sheetTopBound = paddingTop
    val sheetBottomBound = height - paddingBottom

    if (isScrollingUpwards) {
      val canSheetScrollUp = shietView.top > sheetTopBound
      if (canSheetScrollUp) {
        val clampedDy = when {
          // Don't let the sheet go beyond its bounds.
          shietView.top - dy < sheetTopBound -> shietView.top - sheetTopBound
          else -> dy
        }
        shietView.offsetTopAndBottom(-clampedDy)
        return clampedDy
      }
    } else {
      val canSheetContentScrollDown = shietView.canScrollVertically(-1)
      if (canSheetContentScrollDown.not()) {
        // Don't let the sheet go beyond its bounds.
        val clampedDy = when {
          shietView.top - dy > sheetBottomBound -> shietView.top - sheetBottomBound
          else -> dy
        }
        shietView.offsetTopAndBottom(-clampedDy)
        return clampedDy
      }
    }

    return 0
  }
}
