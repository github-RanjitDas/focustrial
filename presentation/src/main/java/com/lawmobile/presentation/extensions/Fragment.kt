package com.lawmobile.presentation.extensions

import androidx.lifecycle.lifecycleScope
import com.lawmobile.presentation.ui.base.BaseFragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun BaseFragment.runWithDelay(
    delay: Long = 200,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    callback: () -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch(dispatcher) {
        delay(delay)
        callback()
    }
}
