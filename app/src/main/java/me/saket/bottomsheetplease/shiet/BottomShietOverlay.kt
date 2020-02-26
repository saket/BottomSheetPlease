package me.saket.bottomsheetplease.shiet

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity.BOTTOM
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.core.view.ViewCompat.TYPE_TOUCH
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import me.saket.bottomsheetplease.shiet.BottomShietState.EXPANDED
import me.saket.bottomsheetplease.shiet.BottomShietState.HIDDEN
import me.saket.bottomsheetplease.shiet.BottomShietState.PEEKING
import kotlin.math.max

@SuppressLint("ViewConstructor")
class BottomShietOverlay(
  context: Context
) : FrameLayout(context), SimpleNestedScrollingParent {

  private lateinit var shietView: View

  @Px var peekHeight: Int? = null
  private var currentState: BottomShietState = HIDDEN

  private val heightMinusPadding
    get() = height - paddingTop - paddingBottom

  override fun onViewAdded(child: View) {
    super.onViewAdded(child)
    check(childCount == 1) { "Can only have one direct child that acts as the sheet." }

    shietView = child
    shietView.updateLayoutParams<LayoutParams> {
      gravity = BOTTOM
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)

    val hasSheet = ::shietView.isInitialized
    if (hasSheet.not()) {
      return
    }

    val exhausted = when (currentState) {
      EXPANDED -> {
        // Keep aligned with the top if the sheet extends beyond
        // the overlay's bounds. Otherwise, align with the bottom.
        // Update: this seems to happen automatically.
      }
      PEEKING -> {
        // Keep the sheet at peek height.
        val peekOffsetFromTop = height - peekHeight!!.coerceAtMost(shietView.height)
        moveSheetTo(peekOffsetFromTop)
      }
      HIDDEN -> moveSheetTo(bottom)
    }
  }

  private fun moveSheetTo(y: Int) {
    shietView.offsetTopAndBottom(y - shietView.top)
  }

  private fun moveSheetBy(dy: Int) {
    shietView.offsetTopAndBottom(dy)
  }

  /** setState()? moveToState()? animateToState()? */
  fun setState(state: BottomShietState, animate: Boolean = false) {
    if (state == PEEKING) {
      require(peekHeight != null && peekHeight!! > 0) { "Nothing to peek" }
    }

    if (isLaidOut.not()) {
      doOnPreDraw { setState(state, animate) }
      return
    }

    // TODO
    currentState = state
  }

  //<editor-fold desc="nested scrolling bloat">
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
  //</editor-fold>

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    val consumedDy = computeNestedScrollToConsume(dy)
    consumed[1] = consumedDy

    val isFling = type != TYPE_TOUCH
    if (isFling.not()) {
      moveSheetBy(-consumedDy)
    }
  }

  private fun computeNestedScrollToConsume(dy: Int): Int {
    val isScrollingUpwards = dy > 0
    val sheetTopBound = max(paddingTop, heightMinusPadding - shietView.height)
    val sheetBottomBound = height - paddingBottom

    if (isScrollingUpwards) {
      val canSheetScrollUp = shietView.top > sheetTopBound
      if (canSheetScrollUp) {
        return when {
          // Don't let the sheet go beyond its top bounds.
          shietView.top - dy < sheetTopBound -> shietView.top - sheetTopBound
          else -> dy
        }
      }
    } else {
      val canSheetContentScrollDown = shietView.canScrollVertically(-1)
      if (canSheetContentScrollDown.not()) {
        // Don't let the sheet go beyond its bottom bounds.
        return when {
          shietView.top - dy > sheetBottomBound -> shietView.top - sheetBottomBound
          else -> dy
        }
      }
    }

    return 0
  }
}
