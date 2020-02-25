package me.saket.bottomsheetplease.shiet

import android.content.Context
import android.view.Gravity.BOTTOM
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.updateLayoutParams

class BottomShietOverlay(context: Context) : FrameLayout(context), NestedScrollingParent3 {

  private lateinit var shietView: View

  override fun onViewAdded(child: View) {
    super.onViewAdded(child)
    check(childCount == 1) { "Can only have one direct child that acts as the sheet." }

    shietView = child
    shietView.updateLayoutParams<LayoutParams> {
      gravity = BOTTOM
    }
  }

  override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean =
    onStartNestedScroll(child, target, axes)

  override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean =
  // Accept all nested scroll events from the child. The decision of whether
    // or not to actually scroll is calculated inside onNestedPreScroll().
    true

  override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) =
    Unit

  override fun onStopNestedScroll(target: View, type: Int) = Unit

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    onNestedPreScroll(target, dx, dy, consumed)
  }

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
    // TODO: Handle.
  }

  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int,
    consumed: IntArray
  ) = onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)

  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int
  ) = Unit
}