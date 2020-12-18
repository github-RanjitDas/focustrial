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
import com.lawmobile.presentation.databinding.FragmentFileListBinding
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class SimpleFileListFragment : FileListBaseFragment() {

    private var _fragmentFileListBinding: FragmentFileListBinding? = null
    private val fragmentFileListBinding get() = _fragmentFileListBinding!!

    private val simpleListViewModel: SimpleListViewModel by activityViewModels()
    var simpleFileListAdapter: SimpleFileListAdapter? = null
    var onFileCheck: ((Boolean, Int) -> Unit)? = null
    var fileListBackup = mutableListOf<DomainInformationFile>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setObservers()
        _fragmentFileListBinding =
            FragmentFileListBinding.inflate(inflater, container, false)
        return fragmentFileListBinding.root
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
                fragmentFileListBinding.textViewEvent.isVisible = false
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
        manageFragmentContent(
            fragmentFileListBinding.fileListRecycler,
            fragmentFileListBinding.noFilesTextView
        )
    }

    private fun restoreFilters() {
        simpleFileListAdapter?.fileList =
            simpleFileListAdapter?.fileList?.let { getFilteredList(it) }
                ?.filterIsInstance<DomainInformationFile>() as MutableList
    }

    private fun setListeners() {
        fragmentFileListBinding.textViewDateAndTime.setOnClickListenerCheckConnection { simpleFileListAdapter?.sortByDateAndTime() }
        fragmentFileListBinding.textViewEvent.setOnClickListenerCheckConnection { simpleFileListAdapter?.sortByEvent() }
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
        fragmentFileListBinding.fileListRecycler.apply {
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
                if (it.items.isNotEmpty()) {
                    showFileListRecycler(
                        fragmentFileListBinding.fileListRecycler,
                        fragmentFileListBinding.noFilesTextView
                    )
                    setAdapter(it.items)
                } else {
                    showEmptyListMessage(
                        fragmentFileListBinding.fileListRecycler,
                        fragmentFileListBinding.noFilesTextView
                    )
                }
            }
            doIfError {
                fragmentFileListBinding.fileListLayout.showErrorSnackBar(
                    getString(R.string.file_list_failed_load_files),
                    Snackbar.LENGTH_INDEFINITE
                ) {
                    context?.verifySessionBeforeAction { getFileList() }
                }
            }
        }
        hideLoadingDialog()
    }

    private fun handleErrors(errors: MutableList<String>) {
        fragmentFileListBinding.fileListLayout.showErrorSnackBar(
            getString(R.string.getting_files_error_description),
            Snackbar.LENGTH_LONG
        ) {
            context?.verifySessionBeforeAction {
                when (listType) {
                    SNAPSHOT_LIST -> simpleListViewModel.getSnapshotList()
                    VIDEO_LIST -> simpleListViewModel.getVideoList()
                }
            }
        }
        showFailedFoldersInLog(errors)
    }

    private fun setAdapter(listItems: MutableList<DomainInformationFile>) {
        simpleFileListAdapter =
            SimpleFileListAdapter(
                ::onFileClick,
                onFileCheck
            ).apply {
                showCheckBoxes = checkableListInit
                fileList =
                    listItems.sortedByDescending { it.domainCameraFile.getCreationDate() } as MutableList
                fileListBackup = fileList
            }
        if (checkableListInit) setAssociatedRecyclerView()
        else setRecyclerView()
    }

    private fun onFileClick(file: DomainInformationFile) {
        startFileListIntent(file.domainCameraFile)
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
