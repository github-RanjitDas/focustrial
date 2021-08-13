package com.safefleet.lawmobile.helpers

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import org.hamcrest.Matcher

class RecyclerViewHelper(private var matcher: Matcher<Int>) : ViewAssertion {

    companion object {
        var currentLength = 0
    }

    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) throw noViewFoundException
        val recyclerView = view as RecyclerView
        val itemsCount = recyclerView.adapter?.itemCount
        if (itemsCount != null) {
            currentLength = itemsCount
            assertThat(itemsCount, matcher)
        }
    }
}
