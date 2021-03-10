package com.lawmobile.presentation.ui.live.navigation

import android.content.Intent
import android.widget.Button
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.FileListActivity
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
        val fileListIntent = Intent(requireContext(), FileListActivity::class.java)
        fileListIntent.putExtra(Constants.FILE_LIST_SELECTOR, fileType)
        startActivity(fileListIntent)
    }
}
