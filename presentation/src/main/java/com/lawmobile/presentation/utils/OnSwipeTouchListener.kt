package com.lawmobile.presentation.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

open class OnSwipeTouchListener(context: Context?) : OnTouchListener {

    private val gestureDetector: GestureDetector

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipeRight() {
        // This method is empty because can be override where needed
    }
    open fun onClick() {
        // This method is empty because can be override where needed
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            onClick()
            return true
        }

        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = event2.y - event1.y
                val diffX = event2.x - event1.x
                if (isMovementFromLeftToRight(diffX, diffY, velocityX)) {
                    val isMoveToRight = diffX > 0
                    if (isMoveToRight) {
                        onSwipeRight()
                    }
                    result = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }
    }

    private fun isMovementFromLeftToRight(diffX: Float, diffY: Float, velocityX: Float) =
        (
            abs(diffX) > abs(diffY) &&
                abs(diffX) > SWIPE_THRESHOLD &&
                abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
            )

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }
}
