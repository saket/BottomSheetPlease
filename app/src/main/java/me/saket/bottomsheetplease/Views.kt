package me.saket.bottomsheetplease

import android.view.View
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener

fun View.doOnEveryLayout(
  callback: () -> Unit
) {
  if (isLaidOut) {
    callback()
  }
  val observer = viewTreeObserver ?: throw IllegalStateException(
      "View $this has no tree observer."
  )
  val listener = object : OnGlobalLayoutListener {
    override fun onGlobalLayout() {
      if (!isLaidOut) return
      callback()
    }
    private fun removeLayoutListener(observer: ViewTreeObserver) {
      observer.removeOnGlobalLayoutListener(this)
    }
  }
  observer.addOnGlobalLayoutListener(listener)
  addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
    override fun onViewAttachedToWindow(v: View) {
      if (viewTreeObserver.isAlive) {
        viewTreeObserver.addOnGlobalLayoutListener(listener)
      }
    }
    override fun onViewDetachedFromWindow(v: View) {
      if (viewTreeObserver.isAlive) {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
      }
    }
  })
}