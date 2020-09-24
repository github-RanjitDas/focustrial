package com.lawmobile.presentation.ui.thumbnailList

import android.content.Intent
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
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.ImageFilesPathManager
import com.lawmobile.presentation.entities.ImageWithPathSaved
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.getPathFromTemporalFile
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.snapshotDetail.SnapshotDetailActivity
import com.lawmobile.presentation.utils.Constants.CAMERA_CONNECT_FILE
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Event
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import kotlinx.android.synthetic.main.fragment_file_list.*
import java.io.File

class ThumbnailFileListFragment : BaseFragment() {

    private val thumbnailListFragmentViewModel: ThumbnailListFragmentViewModel by activityViewModels()
    private var errorNames: ArrayList<String> = arrayListOf()
    private var isLoading = false
    private var currentImageLoading: CameraConnectFile? = null
    private var isLoadedOnCreate = false
    private var loadedImagesList = ArrayList<DomainInformationImage>()

    private lateinit var gridLayoutManager: GridLayoutManager

    var onImageCheck: ((Boolean) -> Unit)? = null
    var thumbnailFileListAdapter: ThumbnailFileListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setObservers()
        return inflater.inflate(R.layout.fragment_file_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureLayoutItems()
        isLoadedOnCreate = true
        getSnapshotList()
    }

    private fun setAssociatedRecyclerView() {
        thumbnailFileListAdapter?.run {
            fileList.let { completeList ->
                fileList = SnapshotsAssociatedByUser
                    .getListOfImagesAssociatedToVideo(completeList)
                    .filterIsInstance<DomainInformationImage>() as MutableList
            }
        }
        setRecyclerView()
    }

    private fun configureLayoutItems() {
        textViewEvent.isVisible = false
        textViewDateAndTime.isVisible = false
        dividerViewList.isVisible = false
    }

    private fun getSnapshotList() {
        showLoadingDialog()
        thumbnailListFragmentViewModel.getSnapshotList()
    }

    private fun setAdapter(listItems: ArrayList<DomainInformationFile>) {
        fileListRecycler.isVisible = true
        noFilesTextView.isVisible = false

        thumbnailFileListAdapter =
            ThumbnailFileListAdapter(::onImageClick, onImageCheck)
                .apply { showCheckBoxes = checkableListInit }

        listItems.forEach {
            thumbnailFileListAdapter?.addItemToList(DomainInformationImage(it.cameraConnectFile))
        }

        if (checkableListInit) setAssociatedRecyclerView()
        else setRecyclerView()
        startRetrievingImages()
    }

    private fun startRetrievingImages() {
        isLoading = true
        val itemToLoad = thumbnailFileListAdapter?.fileList?.firstOrNull()?.cameraConnectFile
        itemToLoad?.let { thumbnailListFragmentViewModel.getImageBytes(it) }
    }

    fun resetList() {
        thumbnailFileListAdapter?.resetList()
    }

    private fun onImageClick(file: DomainInformationImage) {
        showLoadingDialog()
        startFileListIntent(file.cameraConnectFile)
    }

    private fun startFileListIntent(cameraConnectFile: CameraConnectFile) {
        thumbnailListFragmentViewModel.cancelGetImageBytes()
        val fileListIntent = Intent()
        context?.let { fileListIntent.setClass(it, SnapshotDetailActivity::class.java) }
        fileListIntent.putExtra(CAMERA_CONNECT_FILE, cameraConnectFile)
        hideLoadingDialog()
        startActivity(fileListIntent)
        isLoadedOnCreate = false
        activity?.finish()
    }

    fun showCheckBoxes() {
        thumbnailFileListAdapter?.run {
            showCheckBoxes = !showCheckBoxes
            if (!showCheckBoxes) uncheckAllItems()
        }
        setRecyclerView()
    }

    private fun setRecyclerView() {
        fileListRecycler?.apply {
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
                if (it.errors.isNotEmpty()) {
                    handleErrors(it.errors)
                }
                if (it.listItems.isNotEmpty()) setAdapter(it.listItems)
                else showNoFilesView()
            }
            doIfError {
                fileListLayout.showErrorSnackBar(
                    getString(R.string.file_list_failed_load_files),
                    Snackbar.LENGTH_INDEFINITE
                ) {
                    showLoadingDialog()
                    thumbnailListFragmentViewModel.getSnapshotList()
                }
            }
        }
    }

    private fun handleErrors(errors: ArrayList<String>) {
        fileListLayout.showErrorSnackBar(
            getString(R.string.getting_files_error_description),
            Snackbar.LENGTH_LONG
        ){
            thumbnailListFragmentViewModel.getSnapshotList()
        }
        showFailedFoldersInLog(errors)
    }

    private fun handleImageBytes(result: Event<Result<DomainInformationImage>>) {
        result.getContentIfNotHandled()?.run {
            doIfSuccess { setImagesInAdapter(it) }
            doIfError { manageErrorInGetBytes(it) }
        }
    }

    private fun showNoFilesView() {
        fileListRecycler.isVisible = false
        noFilesTextView.text = getString(R.string.no_images_found)
        noFilesTextView.isVisible = true
    }

    private fun manageErrorInGetBytes(exception: Exception) {
        isLoading = false
        val message = exception.message ?: getString(R.string.thumbnail_bytes_error)
        fileListLayout.showErrorSnackBar(message)

        currentImageLoading?.let { cameraConnectFile ->
            errorNames.add(cameraConnectFile.name)
            ImageFilesPathManager.saveImageWithPath(
                ImageWithPathSaved(cameraConnectFile.name, PATH_ERROR_IN_PHOTO)
            )
            thumbnailFileListAdapter?.addItemToList(DomainInformationImage(cameraConnectFile))
            isLoading = false
            loadNewImage()
        }
    }

    private fun setImagesInAdapter(it: DomainInformationImage) {
        if (it.imageBytes != null) {
            it.internalPath =
                it.imageBytes!!.getPathFromTemporalFile(requireContext(), it.cameraConnectFile.name)

            ImageFilesPathManager.saveImageWithPath(
                ImageWithPathSaved(it.cameraConnectFile.name, it.internalPath!!)
            )
        } else {
            fileListLayout.showErrorSnackBar(getString(R.string.thumbnail_bytes_error))
        }

        thumbnailFileListAdapter?.addItemToList(it)
        loadedImagesList.add(it)
        isLoading = false

        loadNewImage()
    }

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

    private fun loadNewImage() {
        val lastPosition =
            gridLayoutManager.findLastVisibleItemPosition() + 1
        val subList =
            thumbnailFileListAdapter?.fileList?.subList(0, lastPosition)
                ?.filter { it.internalPath == null }

        if (!subList.isNullOrEmpty()) {
            isLoading = true
            val imageToLoad =
                subList.firstOrNull { !errorNames.contains(it.cameraConnectFile.name) }?.cameraConnectFile

            imageToLoad?.let {
                val file = ImageFilesPathManager.getImageIfExist(it.name)

                if (file != null) {
                    val domainImage =
                        DomainInformationImage(it, null, false, file.absolutePath)

                    if (checkIfFileExist(file.absolutePath)) {
                        thumbnailFileListAdapter?.addItemToList(domainImage)
                        loadedImagesList.add(domainImage)
                        isLoading = false
                        loadNewImage()
                    }
                } else {
                    currentImageLoading = it
                    thumbnailListFragmentViewModel.getImageBytes(it)
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        thumbnailListFragmentViewModel.cancelGetImageBytes()
        isLoadedOnCreate = false
    }

    override fun onResume() {
        super.onResume()
        if (!isLoadedOnCreate) startRetrievingImages()
    }

    private fun checkIfFileExist(path: String) = File(path).exists()

    private fun isLastPage(): Boolean =
        thumbnailFileListAdapter?.getItemsWithImageToLoading()?.isEmpty() ?: true

    companion object {
        var instance: ThumbnailFileListFragment? = null
        fun getActualInstance(): ThumbnailFileListFragment {
            val fragmentInstance = instance ?: ThumbnailFileListFragment()
            instance = fragmentInstance
            return instance!!
        }

        fun destroyInstance() {
            instance = null
            checkableListInit = false
        }

        const val PATH_ERROR_IN_PHOTO = "errorPath"
        var checkableListInit = false
    }
}