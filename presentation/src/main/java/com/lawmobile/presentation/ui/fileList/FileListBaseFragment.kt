package com.lawmobile.presentation.ui.fileList

import android.content.Intent
import android.util.Log
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.snapshotDetail.SnapshotDetailActivity
import com.lawmobile.presentation.ui.videoPlayback.VideoPlaybackActivity
import com.lawmobile.presentation.utils.Constants
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.lawmobile.presentation.widgets.CustomFilterDialog
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import kotlinx.android.synthetic.main.fragment_file_list.*

open class FileListBaseFragment: BaseFragment() {

    var listType: String? = null
    var isLoadedOnCreate = false
    var filter: CustomFilterDialog? = null

    fun showFileListRecycler() {
        fileListRecycler?.isVisible = true
        noFilesTextView?.isVisible = false
    }

    fun showEmptyListMessage() {
        noFilesTextView?.isVisible = true
        fileListRecycler?.isVisible = false
        when (listType) {
            SNAPSHOT_LIST -> noFilesTextView?.text = getString(R.string.no_images_found)
            VIDEO_LIST -> noFilesTextView?.text = getString(R.string.no_videos_found)
        }
    }

    fun manageFragmentContent(){
        if (!filter?.filteredList.isNullOrEmpty()) showFileListRecycler()
        else showNoFilterResults()
    }

    private fun showNoFilterResults() {
        noFilesTextView?.isVisible = true
        fileListRecycler?.isVisible = false
        when (listType) {
            SNAPSHOT_LIST -> noFilesTextView?.text = getString(R.string.no_snapshots_filter)
            VIDEO_LIST -> noFilesTextView?.text = getString(R.string.no_videos_filter)
        }
    }

    fun startFileListIntent(cameraConnectFile: CameraConnectFile) {
        showLoadingDialog()
        val fileListIntent = Intent()
        context?.let {
            when (listType) {
                SNAPSHOT_LIST -> fileListIntent.setClass(it, SnapshotDetailActivity::class.java)
                VIDEO_LIST -> fileListIntent.setClass(it, VideoPlaybackActivity::class.java)
                else -> throw Exception("List type not supported")
            }
        }
        fileListIntent.putExtra(Constants.CAMERA_CONNECT_FILE, cameraConnectFile)
        hideLoadingDialog()
        startActivity(fileListIntent)
        isLoadedOnCreate = false
    }

    fun showFailedFoldersInLog(errors: ArrayList<String>) {
        CameraInfo.areNewChanges = true
        Log.d(getString(R.string.getting_files_error), errors.toString())
    }

    fun getFilteredList(listToFilter: List<DomainInformationForList>): List<DomainInformationForList>{
        filter?.run {
            if (!currentFilters.isNullOrEmpty()) {
                this.listToFilter = listToFilter
                applyFiltersToLists()
                return filteredList
            }
        }
        return listToFilter
    }

    companion object {
        var checkableListInit = false
    }
}