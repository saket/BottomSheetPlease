package me.saket.bottomsheetplease.shiet

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity.BOTTOM
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import kotlin.math.max

@SuppressLint("ViewConstructor")
class BottomShietOverlay(
  context: Context,
  private val states: List<BottomShietState>
) : FrameLayout(context), SimpleNestedScrollingParent {

  private lateinit var shietView: View
  private var state: BottomShietState = states[0]

  override fun onViewAdded(child: View) {
    super.onViewAdded(child)
    check(childCount == 1) { "Can only have one direct child that acts as the sheet." }

    shietView = child
    shietView.updateLayoutParams<LayoutParams> {
      gravity = BOTTOM
    }

    // TODO: introduce "peek" height instead.
    doOnPreDraw {
      shietView.offsetTopAndBottom(heightMinusPadding() / 2)
    }
  }

  override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
    return super<SimpleNestedScrollingParent>.onStartNestedScroll(child, target, nestedScrollAxes)
  }

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
    super<SimpleNestedScrollingParent>.onNestedPreScroll(target, dx, dy, consumed)
  }

  override fun onStopNestedScroll(target: View) {
    super<SimpleNestedScrollingParent>.onStopNestedScroll(target)
  }

  override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
    super<SimpleNestedScrollingParent>.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
  }

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    val consumedDy = tryConsumingNestedScroll(dy)
    consumed[1] = consumedDy
  }

  private fun heightMinusPadding() = height - paddingTop - paddingBottom

  private fun tryConsumingNestedScroll(dy: Int): Int {
    val isScrollingUpwards = dy > 0
    val sheetTopBound = max(paddingTop, heightMinusPadding() - shietView.height)
    val sheetBottomBound = height - paddingBottom

    if (isScrollingUpwards) {
      val canSheetScrollUp = shietView.top > sheetTopBound
      if (canSheetScrollUp) {
        val clampedDy = when {
          // Don't let the sheet go beyond its top bounds.
          shietView.top - dy < sheetTopBound -> shietView.top - sheetTopBound
          else -> dy
        }
        shietView.offsetTopAndBottom(-clampedDy)
        return clampedDy
      }
    } else {
      val canSheetContentScrollDown = shietView.canScrollVertically(-1)
      if (canSheetContentScrollDown.not()) {
        // Don't let the sheet go beyond its bottom bounds.
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
