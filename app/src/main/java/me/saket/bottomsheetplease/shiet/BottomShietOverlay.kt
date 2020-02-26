package me.saket.bottomsheetplease.shiet

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity.BOTTOM
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.core.view.ViewCompat.TYPE_TOUCH
import androidx.core.view.doOnLayout
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

  private val hasSheet
    get() = ::shietView.isInitialized

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

    // Setting the state again will ensure the
    // sheet is re-positioned w.r.t. the new bounds.
    if (hasSheet) {
      setState(currentState, animate = false)
    }
  }

  private fun moveSheetTo(y: Int) {
    if (shietView.top != y) {
      shietView.offsetTopAndBottom(y - shietView.top)
    }
  }

  private fun moveSheetBy(dy: Int) {
    shietView.offsetTopAndBottom(dy)
  }

  /** setState()? moveToState()? animateToState()? */
  fun setState(state: BottomShietState, animate: Boolean = false) {
    if (state == PEEKING) {
      require(peekHeight != null && peekHeight!! > 0) { "What's there to peek even?" }
    }

    currentState = state

    // TODO: animate?

    if (hasSheet) {
      doOnLayout {
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
    }
  }

  override fun onStopNestedScroll(target: View, type: Int) {
    super.onStopNestedScroll(target)

    // For backward compatibility reasons, a nested scroll stops _twice_.
    // Once when the user stops dragging and once again when the content
    // stops flinging.
    val hasStoppedDragging = type == TYPE_TOUCH

    if (hasStoppedDragging) {
      // TODO: do something for real.
      //setState(currentState, animate = true)
    }
  }

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    val consumeResult = computeNestedScrollToConsume(dy, isFling = type != TYPE_TOUCH)
    consumed[1] = consumeResult.dyToConsume
    moveSheetBy(-consumeResult.moveSheetBy)
  }

  private fun computeNestedScrollToConsume(dy: Int, isFling: Boolean): ConsumeResult {
    val isScrollingUpwards = dy > 0
    val sheetTopBound = max(paddingTop, heightMinusPadding - shietView.height)
    val sheetBottomBound = height - paddingBottom

    var moveSheetBy = 0

    if (isFling) {
      moveSheetBy = 0

    } else if (isScrollingUpwards) {
      val canSheetScrollUp = shietView.top > sheetTopBound
      if (canSheetScrollUp) {
        moveSheetBy = when {
          // Don't let the sheet go beyond its top bounds.
          shietView.top - dy < sheetTopBound -> shietView.top - sheetTopBound
          else -> dy
        }
      }

    } else {
      val canSheetContentScrollDown = shietView.canScrollVertically(-1)
      if (canSheetContentScrollDown.not()) {
        moveSheetBy = when {
          // Don't let the sheet go beyond its bottom bounds.
          shietView.top - dy > sheetBottomBound -> shietView.top - sheetBottomBound
          else -> dy
        }
      }
    }

    // Allow flings on the content only once the sheet can't scroll further.
    val shouldBlockFlings = shietView.top != sheetTopBound
    val dyToConsume = if (shouldBlockFlings) dy else moveSheetBy
    return ConsumeResult(dyToConsume, moveSheetBy)
  }

  class ConsumeResult(val dyToConsume: Int, val moveSheetBy: Int)
}
