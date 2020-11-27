package com.safefleet.lawmobile.helpers

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun isActivated(): Matcher<View?> {
    return object : BoundedMatcher<View?, View>(View::class.java) {
        override fun matchesSafely(view: View): Boolean {
            return view.isActivated
        }

        override fun describeTo(description: Description) {
            description.appendText("is activated")
        }
    }
}

fun isNotActivated(): Matcher<View?> {
    return object : BoundedMatcher<View?, View>(View::class.java) {
        override fun matchesSafely(view: View): Boolean {
            return !view.isActivated
        }

        override fun describeTo(description: Description) {
            description.appendText("is activated")
        }
    }
}