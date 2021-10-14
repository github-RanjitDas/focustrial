package com.lawmobile.presentation.ui.fileList.simpleList

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
import com.lawmobile.domain.entities.FilesAssociatedByUser
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
    var simpleFileListAdapter: SimpleFileListAdapter? = null
    var onFileCheck: ((Boolean, Int) -> Unit)? = null
    var fileListBackup = mutableListOf<DomainInformationFile>()

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

    private fun setAssociatedRecyclerView() {
        simpleFileListAdapter?.run {
            fileList.let { completeList ->
                fileList = FilesAssociatedByUser
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
            binding.fileListRecycler,
            binding.noFilesTextView
        )
    }

    private fun restoreFilters() {
        simpleFileListAdapter?.fileList =
            simpleFileListAdapter?.fileList?.let { getFilteredList(it) }
            ?.filterIsInstance<DomainInformationFile>() as MutableList
    }

    private fun setListeners() {
        binding.textViewDateAndTime.setOnClickListenerCheckConnection { simpleFileListAdapter?.sortByDateAndTime() }
        binding.textViewEvent.setOnClickListenerCheckConnection { simpleFileListAdapter?.sortByEvent() }
    }

    fun reviewIfShowCheckBoxes() {
        if (simpleFileListAdapter?.showCheckBoxes == true) showCheckBoxes()
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
        binding.fileListRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = simpleFileListAdapter
        }
    }

    private fun setObservers() {
        simpleListViewModel.fileListResult.observe(
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
                        binding.fileListRecycler,
                        binding.noFilesTextView
                    )
                    setAdapter(it.items)
                } else {
                    showEmptyListMessage(
                        binding.fileListRecycler,
                        binding.noFilesTextView
                    )
                }
            }
            doIfError {
                val errorMessage =
                    RequestError.getErrorMessage(getString(R.string.file_list_failed_load_files), it)
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

    private fun handleErrors(errors: MutableList<String>) {
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

    private fun setAdapter(listItems: MutableList<DomainInformationFile>) {
        simpleFileListAdapter =
            SimpleFileListAdapter(
                ::onFileClick,
                onFileCheck
            ).apply {
                showCheckBoxes = checkableListInit
                fileList =
                    listItems.sortedByDescending { it.domainCameraFile.getDateDependingOnNameLength() } as MutableList
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
