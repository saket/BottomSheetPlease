package me.saket.bottomsheetplease.shiet

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity.BOTTOM
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.TYPE_NON_TOUCH
import androidx.core.view.ViewCompat.TYPE_TOUCH
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import me.saket.bottomsheetplease.shiet.BottomShietState.EXPANDED
import me.saket.bottomsheetplease.shiet.BottomShietState.HIDDEN
import me.saket.bottomsheetplease.shiet.BottomShietState.PEEKING
import timber.log.Timber
import kotlin.math.max

@SuppressLint("ViewConstructor")
class BottomShietOverlay(
  context: Context
) : FrameLayout(context), SimpleNestedScrollingParent {

  private var _shietView: View? = null
  private val shietView: View
    get() {
      check(_shietView != null) { "Sheet isn't added yte" }
      return _shietView!!
    }
  private val hasSheet
    get() = _shietView != null

  @Px var peekHeight: Int? = null
  private var currentState: BottomShietState = HIDDEN

  override fun onViewAdded(child: View) {
    super.onViewAdded(child)
    check(childCount == 1) { "Can only have one direct child that acts as the sheet." }

    _shietView = child
    shietView.updateLayoutParams<LayoutParams> {
      gravity = BOTTOM
    }
  }

  override fun onViewRemoved(child: View) {
    super.onViewRemoved(child)
    _shietView = null
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)

    // Setting the state again will ensure the
    // sheet is re-positioned w.r.t. the new bounds.
    if (hasSheet) {
      setState(currentState, animate = false)
    }
  }

  // TODO: Wanna move everything to floats?
  private fun sheetY(): Int {
//    return shietView.top
    return shietView.translationY.toInt()
  }

  private fun moveSheetTo(y: Int) {
//    if (shietView.top != y) {
//      shietView.offsetTopAndBottom(y - shietView.top)
//    }
//    Timber.d("animating to $y")

    // Copied from BottomSheetBehavior.
    val interpolator = Interpolator { t ->
      var t = t
      t -= 1.0f
      t * t * t * t * t + 1.0f
    }

    shietView.animate().cancel()
    shietView.animate()
        .translationY(y.toFloat())
        .setInterpolator(interpolator)
        .setDuration(400)
        .start()
  }

  private fun moveSheetBy(dy: Int) {
//    shietView.offsetTopAndBottom(dy)
    if (dy != 0) {
      shietView.animate().cancel()
//      Timber.i("moving by $dy")
      shietView.translationY += dy
    }
  }

  /** setState()? moveToState()? animateToState()? */
  fun setState(state: BottomShietState, animate: Boolean = true) {
    if (state == PEEKING) {
      require(peekHeight != null && peekHeight!! > 0) { "What's there to peek even?" }
    }

    currentState = state

    if (hasSheet) {
      doOnLayout {
        val exhausted = when (currentState) {
          EXPANDED -> {
            // Keep aligned with the top if the sheet extends beyond
            // the overlay's bounds. Otherwise, align with the bottom.
            // Update: this seems to happen automatically.
            moveSheetTo(sheetTopBound)
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

  private var sheetYOnStart = 0

  override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
    // Accept all nested scroll events from the child. The decision of whether
    // or not to actually scroll is calculated inside onNestedPreScroll().
    if (type == TYPE_TOUCH) {
      dragReleasedAtTop = true
      sheetYOnStart = sheetY()
      isDragging = true
    }
    return true
  }

  override fun onStopNestedScroll(target: View, type: Int) {
    super.onStopNestedScroll(target)

//    Timber.i("onStopNestedScroll(type=$type)")

    ViewCompat.stopNestedScroll(this, TYPE_TOUCH)
    ViewCompat.stopNestedScroll(this, TYPE_NON_TOUCH)

    // For backward compatibility reasons, a nested scroll stops _twice_.
    // Once when the user stops dragging and once again when the content
    // stops flinging.
    if (type == TYPE_TOUCH) {
      isDragging = false
      Timber.i("------------")
      Timber.i("Released at top? $dragReleasedAtTop")
      onRelease()
    }
  }

  private var dragReleasedAtTop = false
  private var isDragging = false

  private fun onRelease() {
    val dy = sheetY() - sheetYOnStart
//    Timber.i("------------------")
//    Timber.d("Settling sheet")
//    Timber.i("sheetYOnStart: $sheetYOnStart")
//    Timber.i("sheetY: ${sheetY()}")
//    Timber.i("dy: $dy")

    if (dy != 0) {
      val upwards = dy < 0
      val nextState = when (currentState) {
        EXPANDED -> if (upwards) EXPANDED else PEEKING
        PEEKING -> if (upwards) EXPANDED else HIDDEN
        HIDDEN -> HIDDEN
      }
      setState(nextState)
    }
  }

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    if (ev.action == MotionEvent.ACTION_UP) {
      dragReleasedAtTop = sheetY() == sheetTopBound
    }
    return super.dispatchTouchEvent(ev)
  }

  override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
    // TODO: explain better.
    // The sheet doesn't react to flings so transferring the velocity
    // to the sheet's content when its released looks very sudden.
    return if (dragReleasedAtTop) super.onNestedPreFling(target, velocityX, velocityY) else true
  }

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    val dyToConsume = computeNestedScrollToConsume(dy, isFling = type != TYPE_TOUCH)
    consumed[1] = dyToConsume
    moveSheetBy(-dyToConsume)
  }

  private fun computeNestedScrollToConsume(dy: Int, isFling: Boolean): Int {
    val isScrollingUpwards = dy > 0
    var moveSheetBy = 0

    if (isFling) {
      moveSheetBy = 0

    } else
      if (isScrollingUpwards) {
        val canSheetScrollUp = sheetY() > sheetTopBound
        if (canSheetScrollUp) {
          moveSheetBy = when {
            // Don't let the sheet go beyond its top bounds.
            sheetY() - dy < sheetTopBound -> sheetY() - sheetTopBound
            else -> dy
          }
        }

      } else {
        val canSheetContentScrollDown = shietView.canScrollVertically(-1)
        if (canSheetContentScrollDown.not()) {
          moveSheetBy = when {
            // Don't let the sheet go beyond its bottom bounds.
            sheetY() - dy > sheetBottomBound -> sheetY() - sheetBottomBound
            else -> dy
          }
        }
      }

    return moveSheetBy
  }

  private val heightMinusPadding: Int
    get() {
      check(isLaidOut)
      return height - paddingTop - paddingBottom
    }

  private val sheetTopBound: Int
    get() {
      check(isLaidOut)
      return max(paddingTop, heightMinusPadding - shietView.height)
    }

  private val sheetBottomBound: Int
    get() {
      check(isLaidOut)
      return height - paddingBottom
    }

  class ConsumeResult(val dyToConsume: Int, val moveSheetBy: Int)
}
