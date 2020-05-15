package com.lawmobile.presentation.ui.fileList

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.snapshotDetail.SnapshotDetailActivity
import com.lawmobile.presentation.ui.videoPlayback.VideoPlaybackActivity
import com.lawmobile.presentation.utils.Constants.CAMERA_CONNECT_FILE
import com.lawmobile.presentation.utils.Constants.FILE_LIST_SELECTOR
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.android.synthetic.main.activity_file_list.*
import maes.tech.intentanim.CustomIntent
import javax.inject.Inject

class FileListActivity : BaseActivity() {

    @Inject
    lateinit var fileListViewModel: FileListViewModel
    private lateinit var fileListAdapter: FileListAdapter
    private val snapshotListFragment = SnapshotListFragment.getActualInstance()
    private val videoListFragment = VideoListFragment.getActualInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_list)

        CustomIntent.customType(this, "bottom-to-up")

        when (intent.extras?.getString(FILE_LIST_SELECTOR)) {
            SNAPSHOT_LIST -> setSnapshotFragment()
            VIDEO_LIST -> setVideoFragment()
        }

        setObservers()
        setListeners()
    }

    override fun finish() {
        super.finish()
        CustomIntent.customType(this, "up-to-bottom")
    }

    private fun setObservers() {
        fileListViewModel.snapshotListLiveData.observe(this, Observer(::handleFileListResult))
        fileListViewModel.videoListLiveData.observe(this, Observer(::handleFileListResult))
    }

    private fun setListeners() {
        buttonSnapshotListSwitch.setOnClickListenerCheckConnection {
            if (!it.isActivated) {
                setSnapshotFragment()
                fileListCheckBox.isChecked = false
            }
        }

        buttonVideoListSwitch.setOnClickListenerCheckConnection {
            if (!it.isActivated) {
                setVideoFragment()
                fileListCheckBox.isChecked = false
            }
        }

        fileListCheckBox.setOnCheckedChangeListener { _, _ ->
            fileListAdapter.checkAllItems()
        }

        textViewFileListBack.setOnClickListenerCheckConnection {
            onBackPressed()
            application.onTerminate()
        }

        textViewFileListExit.setOnClickListener {
            killApp()
        }
    }

    private fun setSnapshotFragment() {
        snapshotListText.typeface = Typeface.DEFAULT_BOLD
        videoListText.typeface = Typeface.DEFAULT
        buttonSnapshotListSwitch.isActivated = true
        buttonVideoListSwitch.isActivated = false
        fileListViewModel.getSnapshotList()
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            snapshotListFragment,
            SNAPSHOT_LIST
        )
    }

    private fun setVideoFragment() {
        videoListText.typeface = Typeface.DEFAULT_BOLD
        snapshotListText.typeface = Typeface.DEFAULT
        buttonVideoListSwitch.isActivated = true
        buttonSnapshotListSwitch.isActivated = false
        fileListViewModel.getVideoList()
        supportFragmentManager.attachFragment(
            R.id.fragmentListHolder,
            videoListFragment,
            VIDEO_LIST
        )
    }

    private fun handleFileListResult(result: Result<List<CameraConnectFile>>) {
        when (result) {
            is Result.Success -> {
                if (result.data.isNotEmpty()) {
                    fragmentListHolder.isVisible = true
                    noFilesTextView.isVisible = false
                    fileListAdapter =
                        FileListAdapter(result.data.sortedByDescending { it.date }, ::fileItemClick)
                    when (buttonSnapshotListSwitch.isActivated) {
                        true -> snapshotListFragment.setFileListAdapter?.invoke(fileListAdapter)
                        false -> videoListFragment.setFileListAdapter?.invoke(fileListAdapter)
                    }
                } else {
                    noFilesTextView.isVisible = true
                    fragmentListHolder.isVisible = false
                    when (buttonSnapshotListSwitch.isActivated) {
                        true -> noFilesTextView.text = getString(R.string.no_images_found)
                        false -> noFilesTextView.text = getString(R.string.no_videos_found)
                    }
                }
            }
            is Result.Error -> {
                this.showToast(getString(R.string.file_list_failed_load_files), Toast.LENGTH_LONG)
            }
        }
    }

    private fun fileItemClick(cameraConnectFile: CameraConnectFile) {
        when (buttonSnapshotListSwitch.isActivated) {
            true -> startSnapshotIntent(cameraConnectFile)
            false -> startVideoIntent(cameraConnectFile)
        }
    }

    private fun startSnapshotIntent(cameraConnectFile: CameraConnectFile) {
        val fileListIntent = Intent(this, SnapshotDetailActivity::class.java)
        fileListIntent.putExtra(CAMERA_CONNECT_FILE, cameraConnectFile)
        startActivity(fileListIntent)
    }

    private fun startVideoIntent(cameraConnectFile: CameraConnectFile) {
        val fileListIntent = Intent(this, VideoPlaybackActivity::class.java)
        fileListIntent.putExtra(CAMERA_CONNECT_FILE, cameraConnectFile)
        startActivity(fileListIntent)
    }

}
