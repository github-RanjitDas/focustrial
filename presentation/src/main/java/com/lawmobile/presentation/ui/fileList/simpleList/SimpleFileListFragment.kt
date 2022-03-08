package com.lawmobile.presentation.ui.fileList.simpleList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.enums.RequestError
import com.lawmobile.domain.extensions.getDateDependingOnNameLength
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentFileListBinding
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment
import com.lawmobile.presentation.utils.Constants.AUDIO_LIST
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.VIDEO_LIST
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class SimpleFileListFragment : FileListBaseFragment() {

    private var _binding: FragmentFileListBinding? = null
    private val binding get() = _binding!!

    private val simpleListViewModel: SimpleListViewModel by activityViewModels()

    val listAdapter: SimpleFileListAdapter by lazy {
        SimpleFileListAdapter(::onFileClick, onFileCheck)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setObservers()
        _binding = FragmentFileListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setListType()
        isLoadedOnCreate = true
        getFileList()
    }

    private fun setListType() {
        listType = arguments?.getString(FILE_LIST_TYPE)
    }

    private fun getFileList() {
        showLoadingDialog()
        when (listType) {
            VIDEO_LIST -> simpleListViewModel.getVideoList()
            SNAPSHOT_LIST -> {
                binding.textViewEvent.isVisible = false
                simpleListViewModel.getSnapshotList()
            }
            AUDIO_LIST -> {
                binding.textViewEvent.isVisible = false
                simpleListViewModel.getAudioList()
            }
        }
    }

    override fun setSelectedFiles(selectedFiles: List<DomainCameraFile>) {
        listAdapter.setSelectedFiles(selectedFiles)
    }

    override fun applyFiltersToList() {
        val filteredList = filter?.filteredList?.filterIsInstance<DomainInformationFile>()
            as MutableList<DomainInformationFile>?
        filteredList?.let { listAdapter.fileList = it }
        manageFragmentContent(_binding?.fileListRecycler, _binding?.noFilesTextView)
    }

    override fun getListOfSelectedItems(): List<DomainCameraFile> =
        listAdapter.fileList.filter { it.isSelected }.map { it.domainCameraFile }

    private fun restoreFilters() {
        listAdapter.fileList =
            getFilteredList(listBackup).filterIsInstance<DomainInformationFile>() as MutableList
    }

    private fun setListeners() {
        binding.textViewDateAndTime.setOnClickListenerCheckConnection { listAdapter.sortByDateAndTime() }
        binding.textViewEvent.setOnClickListenerCheckConnection { listAdapter.sortByEvent() }
    }

    override fun toggleCheckBoxes(show: Boolean) {
        isSelectionActive = show
        listAdapter.run {
            showCheckBoxes = show
            if (!show) uncheckAllItems()
        }
        setRecyclerView()
    }

    fun setRecyclerView() {
        _binding?.fileListRecycler?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    private fun setObservers() {
        simpleListViewModel.fileListResult.observe(viewLifecycleOwner, ::handleFileListResult)
    }

    private fun handleFileListResult(result: Result<DomainInformationFileResponse>) {
        with(result) {
            doIfSuccess {
                if (it.errors.isNotEmpty()) showErrors(it.errors)
                if (it.items.isNotEmpty()) {
                    showFileListRecycler(binding.fileListRecycler, binding.noFilesTextView)
                    fillAdapter(it.items)
                } else {
                    showEmptyListMessage(binding.fileListRecycler, binding.noFilesTextView)
                }
            }
            doIfError {
                val errorMessage =
                    RequestError.getErrorMessage(
                        getString(R.string.file_list_failed_load_files),
                        it
                    )
                binding.fileListLayout.showErrorSnackBar(
                    errorMessage,
                    Snackbar.LENGTH_INDEFINITE
                ) {
                    context?.verifySessionBeforeAction { getFileList() }
                }
            }
        }
        hideLoadingDialog()
        onInformationLoaded()
    }

    private fun onInformationLoaded() {
        CameraInfo.onReadyToGetNotifications?.invoke()
    }

    private fun showErrors(errors: MutableList<String>) {
        binding.fileListLayout.showErrorSnackBar(
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

    private fun fillAdapter(listItems: MutableList<DomainInformationFile>) {
        listAdapter.apply {
            val sortedList = listItems.sortedByDescending {
                it.domainCameraFile.getDateDependingOnNameLength()
            } as MutableList

            when (listType) {
                SNAPSHOT_LIST, AUDIO_LIST -> addOnlyNewItemsToList(sortedList)
                VIDEO_LIST -> updateItems(sortedList)
            }
            showCheckBoxes = isSelectionActive
            listBackup = listItems
        }
        restoreFilters()
        setRecyclerView()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = SimpleFileListFragment::class.java.simpleName
    }
}
