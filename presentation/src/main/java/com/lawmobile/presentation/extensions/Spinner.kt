package com.lawmobile.presentation.extensions

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

inline fun Spinner.onItemSelected(crossinline callback: (Int) -> Unit) {
    onItemSelectedListener = object :
        AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            // empty because not needed
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            callback(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // empty because not needed
        }
    }
}
