package com.lawmobile.presentation.ui.simpleList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import kotlinx.android.synthetic.main.fragment_file_list.*

class SimpleFileListFragment : FileListBaseFragment() {

    private val simpleListViewModel: SimpleListViewModel by activityViewModels()
    var simpleFileListAdapter: SimpleFileListAdapter? = null
    var onFileCheck: ((Boolean) -> Unit)? = null
    var fileListBackup = mutableListOf<DomainInformationFile>()

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
        isLoadedOnCreate = true
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

    private fun setAssociatedRecyclerView() {
        simpleFileListAdapter?.run {
            fileList.let { completeList ->
                fileList = SnapshotsAssociatedByUser
                    .getListOfImagesAssociatedToVideo(completeList)
                    .filterIsInstance<DomainInformationFile>() as MutableList
            }
        }
        setRecyclerView()
    }

    fun applyFiltersToList() {
        simpleFileListAdapter?.fileList =
            filter?.filteredList?.filterIsInstance<DomainInformationFile>()
                    as MutableList<DomainInformationFile>
        manageFragmentContent()
    }

    private fun restoreFilters() {
        simpleFileListAdapter?.fileList =
            simpleFileListAdapter?.fileList?.let { getFilteredList(it) }
                ?.filterIsInstance<DomainInformationFile>() as MutableList
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
        setRecyclerView()
    }

    private fun setRecyclerView() {
        restoreFilters()
        fileListRecycler?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
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
                    handleErrors(it.errors)
                }
                if (it.listItems.isNotEmpty()) {
                    showFileListRecycler()
                    setAdapter(it.listItems)
                } else {
                    showEmptyListMessage()
                }
            }
            doIfError {
                fileListLayout.showErrorSnackBar(
                    getString(R.string.file_list_failed_load_files),
                    Snackbar.LENGTH_INDEFINITE
                ) {
                    getFileList()
                }
            }
        }
        hideLoadingDialog()
    }

    private fun handleErrors(errors: ArrayList<String>) {
        fileListLayout.showErrorSnackBar(
            getString(R.string.getting_files_error_description),
            Snackbar.LENGTH_LONG
        ) {
            when (listType) {
                SNAPSHOT_LIST -> simpleListViewModel.getSnapshotList()
                VIDEO_LIST -> simpleListViewModel.getVideoList()
            }
        }
        showFailedFoldersInLog(errors)
    }

    private fun setAdapter(listItems: ArrayList<DomainInformationFile>) {
        simpleFileListAdapter =
            SimpleFileListAdapter(
                ::onFileClick,
                onFileCheck
            ).apply {
                showCheckBoxes = checkableListInit
                fileList =
                    listItems.sortedByDescending { it.cameraConnectFile.getCreationDate() } as MutableList
                fileListBackup = fileList
            }
        if (checkableListInit) setAssociatedRecyclerView()
        else setRecyclerView()
    }

    private fun onFileClick(file: DomainInformationFile) {
        startFileListIntent(file.cameraConnectFile)
    }

    override fun onResume() {
        super.onResume()
        if (CameraInfo.areNewChanges && !isLoadedOnCreate) {
            getFileList()
            CameraInfo.areNewChanges = false
        }
        isLoadedOnCreate = false
    }
}
