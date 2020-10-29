package ru.raider.date.adapter_models

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager


class NonSwipeableViewPager : ViewPager {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    private var allowSwipe = false
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        Log.i("Dev", "onInterceptTouchEvent"+event.action.toString())
        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i("Dev", "onTouchEvent" +event.action.toString())
        if (event.action == 1) {
            // click occured
            allowSwipe = true
            return super.onTouchEvent(event)
        }
        if (event.action == 2) {
            return if (allowSwipe) {
                super.onTouchEvent(event)
            } else {
                false
            }
        }
        return super.onTouchEvent(event)
    }
}