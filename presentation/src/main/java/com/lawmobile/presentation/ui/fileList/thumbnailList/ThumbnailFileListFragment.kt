package com.lawmobile.presentation.ui.fileList.thumbnailList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.entities.FilesAssociatedByUser
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentFileListBinding
import com.lawmobile.presentation.entities.ImageFilesPathManager
import com.lawmobile.presentation.entities.ImageWithPathSaved
import com.lawmobile.presentation.extensions.getPathFromTemporalFile
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.verifySessionBeforeAction
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import java.io.File
import kotlin.math.min

class ThumbnailFileListFragment : FileListBaseFragment() {

    private var _binding: FragmentFileListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ThumbnailListViewModel by activityViewModels()
    private var imagesFailedToLoad: ArrayList<String> = arrayListOf()
    private var isLoading = false
    private var currentImageLoading: DomainCameraFile? = null

    private lateinit var flexLayoutManager: FlexboxLayoutManager

    val listAdapter: ThumbnailFileListAdapter by lazy {
        ThumbnailFileListAdapter(::onImageClick, onFileCheck)
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
        setViews()
        configureLayoutManager()
    }

    private fun configureLayoutManager() {
        flexLayoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.FLEX_START
            justifyContent = JustifyContent.CENTER
        }
        binding.fileListRecycler.layoutManager = flexLayoutManager
    }

    override fun onResume() {
        super.onResume()
        viewModel.cancelGetImageBytes()
        listType = arguments?.getString(FILE_LIST_TYPE)
        getSnapshotList()
    }

    private fun setViews() {
        binding.textViewEvent.isVisible = false
        binding.textViewDateAndTime.isVisible = false
        binding.dividerViewList.isVisible = false
    }

    private fun getSnapshotList() {
        showLoadingDialog()
        viewModel.getSnapshotList()
    }

    private fun fillAdapter(listItems: MutableList<DomainInformationFile>) {
        listAdapter.apply {
            showCheckBoxes = isSelectionActive
            val imageList = listItems.map { DomainInformationImage(it.domainCameraFile) }
            imageList.forEach { listAdapter.addItemToList(it) }
            listBackup = imageList.toMutableList()
        }

        restoreFilters()
        setRecyclerView()
        startRetrievingImages()
    }

    private fun startRetrievingImages() {
        isLoading = true
        val itemToLoad = listAdapter.fileList.firstOrNull()?.domainCameraFile
        itemToLoad?.let { viewModel.getImageBytes(it) }
    }

    override fun setSelectedFiles(selectedFiles: List<DomainCameraFile>) {
        listAdapter.setSelectedFiles(selectedFiles)
    }

    override fun applyFiltersToList() {
        if (activity != null) viewModel.cancelGetImageBytes()
        val filteredList = filter?.filteredList?.filterIsInstance<DomainInformationImage>()
            as MutableList<DomainInformationImage>?
        filteredList?.let { listAdapter.fileList = it }
        manageFragmentContent(_binding?.fileListRecycler, _binding?.noFilesTextView)
        if (activity != null) loadNewImage()
    }

    override fun getListOfSelectedItems(): List<DomainCameraFile> =
        listAdapter.fileList.filter { it.isSelected }.map { it.domainCameraFile }

    private fun restoreFilters() {
        listAdapter.fileList =
            getFilteredList(listBackup).filterIsInstance<DomainInformationImage>() as MutableList
    }

    private fun onImageClick(file: DomainInformationImage) {
        startFileListIntent(file.domainCameraFile)
    }

    override fun toggleCheckBoxes(show: Boolean) {
        isSelectionActive = show
        listAdapter.apply {
            showCheckBoxes = show
            if (!show) uncheckAllItems()
        }
        setRecyclerView()
    }

    private fun setRecyclerView() {
        _binding?.fileListRecycler?.apply {
            setHasFixedSize(true)
            adapter = listAdapter
            addOnScrollListener(scrollListenerForPagination())
        }
    }

    private fun setObservers() {
        with(viewModel) {
            thumbnailListLiveData.observe(
                viewLifecycleOwner,
                Observer(::handleImageBytes)
            )
            imageListLiveData.observe(
                viewLifecycleOwner,
                Observer(::handleSnapshotList)
            )
        }
    }

    private fun handleSnapshotList(result: Result<DomainInformationFileResponse>) {
        hideLoadingDialog()
        with(result) {
            doIfSuccess {
                if (it.errors.isNotEmpty()) showErrorInSomeFiles(it.errors)
                if (it.items.isNotEmpty()) {
                    showFileListRecycler(binding.fileListRecycler, binding.noFilesTextView)
                    fillAdapter(it.items)
                } else showEmptyListMessage(binding.fileListRecycler, binding.noFilesTextView)
            }
            doIfError {
                binding.fileListLayout.showErrorSnackBar(
                    getString(R.string.file_list_failed_load_files),
                    Snackbar.LENGTH_INDEFINITE
                ) {
                    context?.verifySessionBeforeAction {
                        showLoadingDialog()
                        viewModel.getSnapshotList()
                    }
                }
            }
        }
    }

    private fun showErrorInSomeFiles(errors: MutableList<String>) {
        binding.fileListLayout.showErrorSnackBar(
            getString(R.string.getting_files_error_description),
            Snackbar.LENGTH_LONG
        ) {
            context?.verifySessionBeforeAction {
                showLoadingDialog()
                viewModel.getSnapshotList()
            }
        }
        showFailedFoldersInLog(errors)
    }

    private fun handleImageBytes(result: Event<Result<DomainInformationImage>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess { setImagesInAdapter(it) }
            doIfError { manageErrorLoadingImageBytes(it) }
        }
    }

    private fun manageErrorLoadingImageBytes(e: Exception) {
        e.printStackTrace()
        isLoading = false

        currentImageLoading?.let { cameraConnectFile ->
            if (checkLastItemStatusIsSavedWhenSwitchingLists(cameraConnectFile)) return

            imagesFailedToLoad.add(cameraConnectFile.name)
            ImageFilesPathManager.saveImageWithPath(
                ImageWithPathSaved(cameraConnectFile.name, PATH_ERROR_IN_PHOTO)
            )
            val domainInformationImage =
                DomainInformationImage(cameraConnectFile, null, false, PATH_ERROR_IN_PHOTO)
            listAdapter.addItemToList(domainInformationImage)
            isLoading = false
            loadNewImage()
        }
    }

    private fun setImagesInAdapter(image: DomainInformationImage) {
        if (image.imageBytes != null) {
            image.internalPath =
                image.imageBytes!!.getPathFromTemporalFile(
                    requireContext(),
                    image.domainCameraFile.name
                )

            ImageFilesPathManager.saveImageWithPath(
                ImageWithPathSaved(image.domainCameraFile.name, image.internalPath!!)
            )
        }

        val isImageInAdapter = listAdapter.isImageInAdapter(image)
        if (isImageInAdapter) {
            image.isSelected = FilesAssociatedByUser.isImageAssociated(image.domainCameraFile.name)
            listAdapter.addItemToList(image)
            isLoading = false
            loadNewImage()
        }
    }

    private fun checkLastItemStatusIsSavedWhenSwitchingLists(domainCameraFile: DomainCameraFile) =
        listBackup.last().domainCameraFile.name == domainCameraFile.name && listBackup.size != listAdapter.fileList.size

    private fun scrollListenerForPagination() = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val visibleItemCount = recyclerView.childCount
            val firstVisibleItemPosition =
                (recyclerView.layoutManager as FlexboxLayoutManager).findFirstVisibleItemPosition()
            if (!isLoading && !isLastPage()) {
                listAdapter.itemWithImagesLoaded().size.let { items ->
                    if (visibleItemCount + firstVisibleItemPosition >= items && firstVisibleItemPosition >= 0) {
                        loadNewImage()
                    }
                }
            }
        }
    }

    private fun getVisibleSubListToLoad(): List<DomainInformationImage> {
        var subList: List<DomainInformationImage>? = null

        listAdapter.fileList.let {
            val lastPosition = flexLayoutManager.findLastVisibleItemPosition() + 1
            val lastPositionToFilter = min(lastPosition, it.size)
            subList = it.subList(0, lastPositionToFilter).filter { image ->
                image.internalPath == null
            }
        }

        return subList ?: emptyList()
    }

    private fun loadNewImage() {
        val subList = getVisibleSubListToLoad()
        if (!subList.isNullOrEmpty()) uploadImageInAdapter(subList)
    }

    private fun uploadImageInAdapter(subList: List<DomainInformationImage>) {
        isLoading = true
        val imageToLoad =
            subList.firstOrNull {
                !imagesFailedToLoad.contains(it.domainCameraFile.name)
            }?.domainCameraFile

        imageToLoad?.let {
            val file = ImageFilesPathManager.getImageIfExist(it.name)

            if (file != null) {
                val domainImage =
                    DomainInformationImage(it, null, false, file.absolutePath)

                if (checkIfFileExist(file.absolutePath)) {
                    listAdapter.addItemToList(domainImage)
                    isLoading = false
                    loadNewImage()
                    return
                }
            }

            currentImageLoading = it
            viewModel.getImageBytes(it)
        } ?: run {
            val cameraConnectFile = subList.firstOrNull()
            cameraConnectFile?.let {
                val domainImage =
                    DomainInformationImage(it.domainCameraFile, null, false, PATH_ERROR_IN_PHOTO)
                listAdapter.addItemToList(domainImage)
                isLoading = false
                loadNewImage()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        viewModel.cancelGetImageBytes()
    }

    private fun checkIfFileExist(path: String) = File(path).exists()

    private fun isLastPage(): Boolean = listAdapter.getItemsWithImageToLoading().isEmpty()

    override val viewTag: String
        get() = TAG

    companion object {
        val TAG: String = ThumbnailFileListFragment::class.java.simpleName
        const val PATH_ERROR_IN_PHOTO = "errorPath"
    }
}
