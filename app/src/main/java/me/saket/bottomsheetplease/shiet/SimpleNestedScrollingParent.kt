package me.saket.bottomsheetplease.shiet

import android.view.View
import androidx.core.view.NestedScrollingParent2

interface SimpleNestedScrollingParent : NestedScrollingParent2 {

//  override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
//    // Accept all nested scroll events from the child. The decision of whether
//    // or not to actually scroll is calculated inside onNestedPreScroll().
//    return true
//  }

  override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
  }

  override fun onNestedScroll(
    target: View,
    dxConsumed: Int,
    dyConsumed: Int,
    dxUnconsumed: Int,
    dyUnconsumed: Int,
    type: Int
  ) = Unit
}