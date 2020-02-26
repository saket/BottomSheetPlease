package me.saket.bottomsheetplease.shiet

import android.view.View
import androidx.core.view.NestedScrollingParent2
import timber.log.Timber

interface SimpleNestedScrollingParent : NestedScrollingParent2 {

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