package me.saket.bottomsheetplease.shiet

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.animation.Interpolator
import android.widget.FrameLayout
import androidx.annotation.Px
import androidx.core.view.ViewCompat.TYPE_TOUCH
import androidx.core.view.doOnLayout
import me.saket.bottomsheetplease.shiet.BottomShietState.EXPANDED
import me.saket.bottomsheetplease.shiet.BottomShietState.HIDDEN
import me.saket.bottomsheetplease.shiet.BottomShietState.PEEKING
import kotlin.math.max

// TODO: offer callbacks et al
// TODO: support sheets that don't implement NestedScrollingChild
@SuppressLint("ViewConstructor")
class BottomShietOverlay(
  context: Context
) : FrameLayout(context), SimpleNestedScrollingParent {

  private var _shietView: View? = null
  private val shietView: View
    get() {
      check(_shietView != null) { "Sheet isn't added yet" }
      return _shietView!!
    }
  private val hasSheet
    get() = _shietView != null

  @Px var peekHeight: Int? = null
  private var currentState: BottomShietState = HIDDEN

  private var sheetAnimator = ValueAnimator()
  private var dragReleasedAtTop = false
  private var distanceDragged = 0

  override fun onViewAdded(child: View) {
    super.onViewAdded(child)
    check(childCount == 1) { "Can only have one direct child that acts as the sheet." }
    _shietView = child
  }

  override fun onViewRemoved(child: View) {
    super.onViewRemoved(child)
    _shietView = null
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    if (!hasSheet) {
      return super.onLayout(changed, left, top, right, bottom)
    }

    // Maintain sheet's y-offset across size changes.
    retainSheetY {
      super.onLayout(changed, left, top, right, bottom)
    }

    // Setting the state again will ensure the
    // sheet is re-positioned w.r.t. the new bounds.
    setState(currentState, animate = true)
  }

  private fun retainSheetY(runnable: () -> Unit) {
    // If the sheet hasn't been laid out yet, move it
    // to the bottom so that it can animate upwards.
    val savedY = when {
      shietView.isLaidOut.not() -> bottom
      else -> sheetY()
    }
    runnable()
    moveSheetTo(savedY, animate = false)
  }

  private fun sheetY(): Int {
    return shietView.top
  }

  private fun moveSheetTo(y: Int, animate: Boolean = false) {
    if (shietView.top == y) {
      return
    }

    sheetAnimator.cancel()

    if (animate) {
      // Copied from BottomSheetBehavior.
      val interpolator = Interpolator { t ->
        var t = t
        t -= 1.0f
        t * t * t * t * t + 1.0f
      }

      sheetAnimator = ObjectAnimator.ofInt(sheetY(), y).apply {
        duration = 400
        setInterpolator(interpolator)
        addUpdateListener { anim ->
          val nextY = anim.animatedValue as Int
          shietView.offsetTopAndBottom(nextY - shietView.top)
        }
        start()
      }
    } else {
      shietView.offsetTopAndBottom(y - shietView.top)
    }
  }

  private fun moveSheetBy(dy: Int) {
    if (dy != 0) {
      sheetAnimator.cancel()
      shietView.offsetTopAndBottom(dy)
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
            moveSheetTo(sheetTopBound, animate = animate)
          }
          PEEKING -> {
            // Keep the sheet at peek height.
            val clampedPeekHeight = peekHeight!!.coerceAtMost(shietView.height)
            val peekOffsetFromTop = heightMinusPadding - clampedPeekHeight
            moveSheetTo(peekOffsetFromTop, animate = animate)
          }
          HIDDEN -> moveSheetTo(bottom, animate = animate)
        }
      }
    }
  }

  override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
    // Accept all nested scroll events from the child. The decision of whether
    // or not to actually scroll is calculated inside onNestedPreScroll().
    if (type == TYPE_TOUCH) {
      distanceDragged = 0
    }
    return true
  }

  override fun onStopNestedScroll(target: View, type: Int) {
    super.onStopNestedScroll(target)

    // For backward compatibility reasons, a nested scroll stops _twice_.
    // Once when the user stops dragging and once again when the content
    // stops flinging.
    if (type == TYPE_TOUCH && distanceDragged != 0) {
      val upwards = distanceDragged < 0
      val nextState = when (currentState) {
        EXPANDED -> if (upwards) EXPANDED else PEEKING
        PEEKING -> if (upwards) EXPANDED else HIDDEN
        HIDDEN -> HIDDEN
      }
      setState(nextState)
    }
  }

  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    if (hasSheet) {
      when (ev.action) {
        ACTION_DOWN -> dragReleasedAtTop = false
        ACTION_UP -> dragReleasedAtTop = sheetY() == sheetTopBound
      }
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

    // dy is negative when the scroll is downwards.
    moveSheetBy(-dyToConsume)
    distanceDragged -= dyToConsume
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
}
