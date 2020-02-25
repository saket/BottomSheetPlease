package me.saket.bottomsheetplease.shiet

import android.view.View
import androidx.core.view.NestedScrollingParent2
import androidx.core.view.ViewCompat.TYPE_NON_TOUCH
import timber.log.Timber

interface SimpleNestedScrollingParent : NestedScrollingParent2 {

  override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
    return onStartNestedScroll(child, target, nestedScrollAxes, TYPE_NON_TOUCH)
  }

  override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
    // Accept all nested scroll events from the child. The decision of whether
    // or not to actually scroll is calculated inside onNestedPreScroll().
    Timber.i("---------------------------")
    Timber.i("onStartNestedScroll(type=$type)")
    return true
  }

  override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    Timber.i("onNestedScrollAccepted(type=$type)")
  }

  override fun onStopNestedScroll(target: View, type: Int) {
    Timber.i("onStopNestedScroll(type=$type)")
  }

  override fun onStopNestedScroll(target: View) {
    onStopNestedScroll(target, TYPE_NON_TOUCH)
  }

  override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
    onNestedPreScroll(target, dx, dy, consumed, TYPE_NON_TOUCH)
  }

  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int
  ) = onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, TYPE_NON_TOUCH)

  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int
  ) {
    Timber.i("onNestedScroll(type=$type)")
  }
}