package me.saket.bottomsheetplease.shiet

import androidx.annotation.Px

sealed class BottomShietState {
  object Expanded : BottomShietState()
  data class Peeking(@Px val height: Int) : BottomShietState()
  object Hidden : BottomShietState()
}