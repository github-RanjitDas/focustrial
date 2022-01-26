package com.lawmobile.presentation.ui.fileList

import android.content.Intent
import android.util.Log
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.audioDetail.AudioDetailActivity
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.shared.FileList
import com.lawmobile.presentation.ui.snapshotDetail.SnapshotDetailActivity
import com.lawmobile.presentation.ui.videoPlayback.VideoPlaybackActivity
import com.lawmobile.presentation.utils.Constants
import com.lawmobile.presentation.utils.Constants.AUDIO_LIST
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.lawmobile.presentation.widgets.CustomFilterDialog

abstract class FileListBaseFragment : BaseFragment(), FileList {

    var listType: String? = null
    var isLoadedOnCreate = false

    override var filter: CustomFilterDialog? = null
    override var onFileCheck: ((Int) -> Unit)? = null
    override var listBackup: MutableList<out DomainInformationForList> = mutableListOf()

    var isSelectionActive = false

    fun showFileListRecycler(fileListRecycler: RecyclerView?, noFilesTextView: TextView?) {
        fileListRecycler?.isVisible = true
        noFilesTextView?.isVisible = false
    }

    fun showEmptyListMessage(fileListRecycler: RecyclerView?, noFilesTextView: TextView?) {
        noFilesTextView?.isVisible = true
        fileListRecycler?.isVisible = false
        when (listType) {
            SNAPSHOT_LIST -> noFilesTextView?.text = getString(R.string.no_images_found)
            VIDEO_LIST -> noFilesTextView?.text = getString(R.string.no_videos_found)
            AUDIO_LIST -> noFilesTextView?.text = getString(R.string.no_audios_found)
        }
    }

    fun manageFragmentContent(fileListRecycler: RecyclerView?, noFilesTextView: TextView?) {
        if (!filter?.filteredList.isNullOrEmpty()) showFileListRecycler(
            fileListRecycler,
            noFilesTextView
        )
        else showNoFilterResults(fileListRecycler, noFilesTextView)
    }

    private fun showNoFilterResults(fileListRecycler: RecyclerView?, noFilesTextView: TextView?) {
        noFilesTextView?.isVisible = true
        fileListRecycler?.isVisible = false
        when (listType) {
            SNAPSHOT_LIST -> noFilesTextView?.text = getString(R.string.no_snapshots_filter)
            VIDEO_LIST -> noFilesTextView?.text = getString(R.string.no_videos_filter)
            AUDIO_LIST -> noFilesTextView?.text = getString(R.string.no_audios_filter)
        }
    }

    fun startFileListIntent(domainCameraFile: DomainCameraFile) {
        showLoadingDialog()
        val fileListIntent = Intent()
        context?.let {
            when (listType) {
                SNAPSHOT_LIST -> fileListIntent.setClass(it, SnapshotDetailActivity::class.java)
                VIDEO_LIST -> fileListIntent.setClass(it, VideoPlaybackActivity::class.java)
                AUDIO_LIST -> fileListIntent.setClass(it, AudioDetailActivity::class.java)
                else -> throw Exception("List type not supported")
            }
        }
        fileListIntent.putExtra(Constants.DOMAIN_CAMERA_FILE, domainCameraFile)
        hideLoadingDialog()
        startActivity(fileListIntent)
        isLoadedOnCreate = false
    }

    fun showFailedFoldersInLog(errors: MutableList<String>) {
        CameraInfo.areNewChanges = true
        Log.d(getString(R.string.getting_files_error), errors.toString())
    }

    fun getFilteredList(listToFilter: List<DomainInformationForList>): List<DomainInformationForList> {
        filter?.run {
            if (currentFilters.isNotEmpty()) {
                this.listToFilter = listToFilter
                applyFiltersToLists()
                return filteredList
            }
        }
        return listToFilter
    }
}
