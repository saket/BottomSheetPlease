package me.saket.bottomsheetplease.shiet

import android.view.View
import androidx.core.view.NestedScrollingParent3

interface SimpleNestedScrollingParent : NestedScrollingParent3 {

  override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean =
    onStartNestedScroll(child, target, axes)

  override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
    // Accept all nested scroll events from the child. The decision of whether
    // or not to actually scroll is calculated inside onNestedPreScroll().
    return true
  }

  override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) =
    Unit

  override fun onStopNestedScroll(target: View, type: Int) = Unit

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    onNestedPreScroll(target, dx, dy, consumed)
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