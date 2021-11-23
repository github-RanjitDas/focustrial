package com.lawmobile.presentation.ui.live.navigation

import android.widget.Button
import com.lawmobile.presentation.extensions.getCameraTypeIntent
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.base.menu.MenuFragment.Companion.currentListView
import com.lawmobile.presentation.ui.base.menu.MenuFragment.Companion.isInMainScreen
import com.lawmobile.presentation.ui.fileList.x1.FileListX1Activity
import com.lawmobile.presentation.ui.fileList.x2.FileListX2Activity
import com.lawmobile.presentation.utils.Constants

open class LiveNavigationBaseFragment : BaseFragment() {

    lateinit var buttonSnapshotList: Button
    lateinit var buttonVideoList: Button

    fun setSharedListeners() {
        buttonSnapshotList.setOnClickListenerCheckConnection {
            startFileListIntent(Constants.SNAPSHOT_LIST)
        }

        buttonVideoList.setOnClickListenerCheckConnection {
            startFileListIntent(Constants.VIDEO_LIST)
        }
    }

    private fun startFileListIntent(fileType: String) {
        val fileListIntent =
            activity?.getCameraTypeIntent(
                FileListX1Activity::class.java,
                FileListX2Activity::class.java
            )
        fileListIntent?.putExtra(Constants.FILE_LIST_SELECTOR, fileType)
        startActivity(fileListIntent)
        currentListView = fileType
        isInMainScreen = false
    }
}
