package com.lawmobile.presentation.ui.thumbnailList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FragmentFileListBinding
import com.lawmobile.presentation.entities.ImageFilesPathManager
import com.lawmobile.presentation.entities.ImageWithPathSaved
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.getPathFromTemporalFile
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import java.io.File
import kotlin.math.min

class ThumbnailFileListFragment : FileListBaseFragment() {

    private var _fragmentFileListBinding: FragmentFileListBinding? = null
    private val fragmentFileListBinding get() = _fragmentFileListBinding!!

    private val thumbnailListFragmentViewModel: ThumbnailListFragmentViewModel by activityViewModels()
    private var imagesFailedToLoad: ArrayList<String> = arrayListOf()
    private var isLoading = false
    private var currentImageLoading: DomainCameraFile? = null
    var fileListBackup = mutableListOf<DomainInformationImage>()

    private lateinit var gridLayoutManager: GridLayoutManager

    var onImageCheck: ((Boolean, Int) -> Unit)? = null
    var thumbnailFileListAdapter: ThumbnailFileListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setObservers()
        _fragmentFileListBinding =
            FragmentFileListBinding.inflate(inflater, container, false)
        return fragmentFileListBinding.root
    }

    override fun onResume() {
        super.onResume()
        thumbnailListFragmentViewModel.cancelGetImageBytes()
        cleanFileList()
        setAdapter()
        listType = arguments?.getString(FILE_LIST_TYPE)
        configureLayoutItems()
        getSnapshotList()
    }

    private fun cleanFileList() {
        thumbnailFileListAdapter?.fileList = mutableListOf()
    }

    private fun setAssociatedRecyclerView() {
        thumbnailFileListAdapter?.run {
            fileList.let { completeList ->
                fileList = SnapshotsAssociatedByUser
                    .getListOfImagesAssociatedToVideo(completeList)
                    .filterIsInstance<DomainInformationImage>() as MutableList
            }
        }
        setAndFilterRecyclerView()
    }

    private fun configureLayoutItems() {
        fragmentFileListBinding.textViewEvent.isVisible = false
        fragmentFileListBinding.textViewDateAndTime.isVisible = false
        fragmentFileListBinding.dividerViewList.isVisible = false
    }

    private fun getSnapshotList() {
        showLoadingDialog()
        thumbnailListFragmentViewModel.getSnapshotList()
    }

    private fun setAdapter() {
        thumbnailFileListAdapter =
            ThumbnailFileListAdapter(::onImageClick, onImageCheck)
                .apply { showCheckBoxes = checkableListInit }
    }

    private fun fillAdapter(listItems: MutableList<DomainInformationFile>) {
        showFileListRecycler(
            fragmentFileListBinding.fileListRecycler,
            fragmentFileListBinding.noFilesTextView
        )

        fileListBackup = mutableListOf()

        thumbnailFileListAdapter?.fileList = mutableListOf()
        listItems.forEach {
            val domainInformationImage = DomainInformationImage(it.domainCameraFile)
            thumbnailFileListAdapter?.addItemToList(domainInformationImage)
            fileListBackup.add(domainInformationImage)
        }

        if (checkableListInit) setAssociatedRecyclerView()
        else setAndFilterRecyclerView()
        startRetrievingImages()
    }

    private fun startRetrievingImages() {
        isLoading = true
        val itemToLoad = thumbnailFileListAdapter?.fileList?.firstOrNull()?.domainCameraFile
        itemToLoad?.let { thumbnailListFragmentViewModel.getImageBytes(it) }
    }

    fun applyFiltersToList() {
        thumbnailListFragmentViewModel.cancelGetImageBytes()
        thumbnailFileListAdapter?.fileList =
            filter?.filteredList?.filterIsInstance<DomainInformationImage>()
                    as MutableList<DomainInformationImage>
        loadNewImage()
        manageFragmentContent(
            fragmentFileListBinding.fileListRecycler,
            fragmentFileListBinding.noFilesTextView
        )
    }

    private fun restoreFilters() {
        thumbnailFileListAdapter?.fileList =
            thumbnailFileListAdapter?.fileList?.let { getFilteredList(it) }
                ?.filterIsInstance<DomainInformationImage>() as MutableList
    }

    private fun onImageClick(file: DomainInformationImage) {
        startFileListIntent(file.domainCameraFile)
    }

    fun showCheckBoxes() {
        thumbnailFileListAdapter?.run {
            showCheckBoxes = !showCheckBoxes
            if (!showCheckBoxes) uncheckAllItems()
        }
        setAndFilterRecyclerView()
    }

    private fun setAndFilterRecyclerView() {
        restoreFilters()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        fragmentFileListBinding.fileListRecycler.apply {
            setHasFixedSize(true)
            gridLayoutManager = GridLayoutManager(requireContext(), 2)
            layoutManager = gridLayoutManager
            adapter = thumbnailFileListAdapter
            addOnScrollListener(scrollListenerForPagination())
        }
    }

    private fun setObservers() {
        with(thumbnailListFragmentViewModel) {
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
                if (it.items.isNotEmpty()) fillAdapter(it.items)
                else showEmptyListMessage(
                    fragmentFileListBinding.fileListRecycler,
                    fragmentFileListBinding.noFilesTextView
                )
            }
            doIfError {
                fragmentFileListBinding.fileListLayout.showErrorSnackBar(
                    getString(R.string.file_list_failed_load_files),
                    Snackbar.LENGTH_INDEFINITE
                ) {
                    showLoadingDialog()
                    thumbnailListFragmentViewModel.getSnapshotList()
                }
            }
        }
    }

    private fun showErrorInSomeFiles(errors: MutableList<String>) {
        fragmentFileListBinding.fileListLayout.showErrorSnackBar(
            getString(R.string.getting_files_error_description),
            Snackbar.LENGTH_LONG
        ) {
            thumbnailListFragmentViewModel.getSnapshotList()
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
            thumbnailFileListAdapter?.addItemToList(domainInformationImage)
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

        val isImageInAdapter = thumbnailFileListAdapter?.isImageInAdapter(image)
        if (isImageInAdapter != null && isImageInAdapter) {
            image.isSelected =
                SnapshotsAssociatedByUser.isImageAssociated(image.domainCameraFile.name)
            thumbnailFileListAdapter?.addItemToList(image)
            isLoading = false
            loadNewImage()
        }
    }

    private fun checkLastItemStatusIsSavedWhenSwitchingLists(domainCameraFile: DomainCameraFile) =
        fileListBackup.last().domainCameraFile.name == domainCameraFile.name && fileListBackup.size != thumbnailFileListAdapter?.fileList?.size

    private fun scrollListenerForPagination() = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val visibleItemCount = recyclerView.childCount
            val firstVisibleItemPosition =
                (recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
            if (!isLoading && !isLastPage()) {
                thumbnailFileListAdapter?.itemWithImagesLoaded()?.size.let { itemWithImagesLoaded ->
                    val items = itemWithImagesLoaded ?: 0
                    if (visibleItemCount + firstVisibleItemPosition >= items && firstVisibleItemPosition >= 0) {
                        loadNewImage()
                    }
                }
            }
        }
    }

    private fun getVisibleSubListToLoad(): List<DomainInformationImage> {
        var subList: List<DomainInformationImage>? = null

        thumbnailFileListAdapter?.fileList?.let {
            val lastPosition = gridLayoutManager.findLastVisibleItemPosition() + 1
            val lastPositionToFilter = min(lastPosition, it.size)
            subList = it.subList(0, lastPositionToFilter).filter { image ->
                image.internalPath == null
            }
        }

        return subList ?: emptyList()
    }

    private fun loadNewImage() {
        val subList = getVisibleSubListToLoad()

        if (!subList.isNullOrEmpty()) {
            uploadImageInAdapter(subList)
        }
    }

    private fun uploadImageInAdapter(subList: List<DomainInformationImage>) {
        isLoading = true
        val imageToLoad =
            subList.firstOrNull { !imagesFailedToLoad.contains(it.domainCameraFile.name) }?.domainCameraFile

        imageToLoad?.let {
            val file = ImageFilesPathManager.getImageIfExist(it.name)

            if (file != null) {
                val domainImage =
                    DomainInformationImage(it, null, false, file.absolutePath)

                if (checkIfFileExist(file.absolutePath)) {
                    thumbnailFileListAdapter?.addItemToList(domainImage)
                    isLoading = false
                    loadNewImage()
                    return
                }
            }

            currentImageLoading = it
            thumbnailListFragmentViewModel.getImageBytes(it)
        } ?: run {
            val cameraConnectFile = subList.firstOrNull()
            cameraConnectFile?.let {
                val domainImage =
                    DomainInformationImage(it.domainCameraFile, null, false, PATH_ERROR_IN_PHOTO)
                thumbnailFileListAdapter?.addItemToList(domainImage)
                isLoading = false
                loadNewImage()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        cleanFileList()
    }

    private fun checkIfFileExist(path: String) = File(path).exists()

    private fun isLastPage(): Boolean =
        thumbnailFileListAdapter?.getItemsWithImageToLoading()?.isEmpty() ?: true

    companion object {
        const val PATH_ERROR_IN_PHOTO = "errorPath"
    }
}