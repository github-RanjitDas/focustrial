package com.lawmobile.presentation.ui.simpleList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.AlertInformation
import com.lawmobile.presentation.extensions.createAlertInformation
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.snapshotDetail.SnapshotDetailActivity
import com.lawmobile.presentation.ui.videoPlayback.VideoPlaybackActivity
import com.lawmobile.presentation.utils.Constants
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import kotlinx.android.synthetic.main.fragment_file_list.*

class SimpleFileListFragment : BaseFragment() {

    private val simpleListViewModel: SimpleListViewModel by viewModels()
    var simpleFileListAdapter: SimpleFileListAdapter? = null
    private var listType: String? = null
    var onFileCheck: ((Boolean) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setObservers()
        return inflater.inflate(R.layout.fragment_file_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        listType = arguments?.getString(FILE_LIST_TYPE)
        getFileList()
    }

    private fun getFileList() {
        showLoadingDialog()
        when (listType) {
            VIDEO_LIST -> {
                simpleListViewModel.getVideoList()
            }
            SNAPSHOT_LIST -> {
                textViewEvent.isVisible = false
                simpleListViewModel.getSnapshotList()
            }
        }
    }

    private fun setListeners() {
        textViewDateAndTime.setOnClickListenerCheckConnection { simpleFileListAdapter?.sortByDateAndTime() }
        textViewEvent.setOnClickListenerCheckConnection { simpleFileListAdapter?.sortByEvent() }
    }

    fun showCheckBoxes() {
        simpleFileListAdapter?.run {
            showCheckBoxes = !showCheckBoxes
            if (!showCheckBoxes) uncheckAllItems()
        }
        setFileRecyclerView()
    }

    private fun setFileRecyclerView() {
        fileListRecycler?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = simpleFileListAdapter
        }
    }

    private fun setObservers() {
        simpleListViewModel.fileListLiveData.observe(
            viewLifecycleOwner,
            Observer(::handleFileListResult)
        )
    }

    private fun handleFileListResult(result: Result<DomainInformationFileResponse>) {
        with(result) {
            doIfSuccess {
                if (it.errors.isNotEmpty()) {
                    showErrors(it.errors)
                }
                if (it.listItems.isNotEmpty()) {
                    fileListRecycler.isVisible = true
                    noFilesTextView.isVisible = false
                    setAdapter(it.listItems)
                } else {
                    noFilesTextView.isVisible = true
                    fileListRecycler.isVisible = false
                    when (listType) {
                        SNAPSHOT_LIST -> noFilesTextView.text = getString(R.string.no_images_found)
                        VIDEO_LIST -> noFilesTextView.text = getString(R.string.no_videos_found)
                    }
                }
            }
            doIfError {
                fileListLayout.showErrorSnackBar(getString(R.string.file_list_failed_load_files))
            }
        }
        hideLoadingDialog()
    }

    private fun setAdapter(listItems: ArrayList<DomainInformationFile>) {
        simpleFileListAdapter =
            SimpleFileListAdapter(
                ::onFileClick,
                onFileCheck
            )
        simpleFileListAdapter?.fileList =
            listItems.sortedByDescending { it.cameraConnectFile.getCreationDate() }
        setFileRecyclerView()
    }

    private fun onFileClick(file: DomainInformationFile) {
        showLoadingDialog()
        startFileListIntent(file.cameraConnectFile)
    }

    private fun startFileListIntent(cameraConnectFile: CameraConnectFile) {
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
    }

    private fun showErrors(errors: ArrayList<String>) {
        var customMessage = getString(R.string.getting_files_error_description) + "\n"
        errors.forEach {
            customMessage += it + "\n"
        }
        val alertInformation = AlertInformation(R.string.getting_files_error, null, {
            it.dismiss()
        }, null, customMessage)
        activity?.createAlertInformation(alertInformation)
        CameraInfo.areNewChanges = true
    }

    override fun onResume() {
        super.onResume()
        if (CameraInfo.areNewChanges) {
            getFileList()
            CameraInfo.areNewChanges = false
        }
    }

    companion object {
        var instance: SimpleFileListFragment? = null
        fun getActualInstance(): SimpleFileListFragment {
            val instanceFragment = instance ?: SimpleFileListFragment()
            instance = instanceFragment
            return instance!!
        }
    }
}
