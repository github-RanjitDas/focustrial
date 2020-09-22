package com.lawmobile.presentation.ui.thumbnailList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.FilePathSaved
import com.lawmobile.presentation.entities.ImageWithPathSaved
import com.lawmobile.presentation.entities.SnapshotsToLink
import com.lawmobile.presentation.extensions.getPathFromTemporalFile
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.snapshotDetail.SnapshotDetailActivity
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import kotlinx.android.synthetic.main.fragment_file_list.*
import java.io.File
import java.lang.Exception

class ThumbnailFileListFragment : BaseFragment() {

    private val thumbnailListFragmentViewModel: ThumbnailListFragmentViewModel by viewModels()
    private var temporalImageListBytes = ArrayList<DomainInformationImage>()
    private var imageListNames = ArrayList<DomainInformationFile>()
    private var errorsNames: ArrayList<String> = arrayListOf()
    private var isLoading = false
    private var currentImageLoading: CameraConnectFile? = null

    var onImageCheck: ((Boolean) -> Unit)? = null
    var thumbnailFileListAdapter: ThumbnailFileListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setObserver()
        return inflater.inflate(R.layout.fragment_file_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureLayoutItems()
        configureLoadingViews()
        thumbnailFileListAdapter = ThumbnailFileListAdapter(::onImageClick, onImageCheck)
        setThumbnailRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        thumbnailListFragmentViewModel.getImageList()
    }

    fun resetList() {
        thumbnailFileListAdapter?.resetList()
    }

    private fun configureLayoutItems() {
        textViewEvent.isVisible = false
        textViewDateAndTime.isVisible = false
        dividerViewList.isVisible = false
    }

    private fun onImageClick(file: DomainInformationImage) {
        showLoadingDialog()
        startFileListIntent(file.cameraConnectFile)
    }

    private fun startFileListIntent(cameraConnectFile: CameraConnectFile) {
        val fileListIntent = Intent()
        context?.let { fileListIntent.setClass(it, SnapshotDetailActivity::class.java) }
        fileListIntent.putExtra(Constants.CAMERA_CONNECT_FILE, cameraConnectFile)
        hideLoadingDialog()
        startActivity(fileListIntent)
        activity?.finish()
    }

    fun showCheckBoxes() {
        thumbnailFileListAdapter?.run {
            showCheckBoxes = !showCheckBoxes
            if (!showCheckBoxes) uncheckAllItems()
        }
        setThumbnailRecyclerView()
    }

    private fun configureLoadingViews() {
        showLoadingDialog()
    }

    private fun setThumbnailRecyclerView() {
        fileListRecycler?.apply {
            val manager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
            layoutManager = manager
            adapter = thumbnailFileListAdapter
            addOnScrollListener(scrollListenerForPagination())
        }
    }

    private fun setObserver() {
        thumbnailListFragmentViewModel.thumbnailListLiveData.observe(
            viewLifecycleOwner,
            Observer(::handleImageListBytes)
        )

        thumbnailListFragmentViewModel.imageListLiveData.observe(
            viewLifecycleOwner,
            Observer(::handleImageList)
        )
    }

    private fun handleImageList(result: Result<List<DomainInformationFile>>) {
        hideLoadingDialog()
        with(result) {
            doIfSuccess {
                if (it.size >= imageListNames.size) {
                    setImageLoadingInAdapter(it)
                    manageVisibilityItemsInView()
                }
            }
            doIfError {
                activity?.showToast(
                    getString(R.string.file_list_failed_load_files),
                    Toast.LENGTH_LONG
                )
                activity?.finish()
            }
        }
    }

    private fun handleImageListBytes(result: Result<List<DomainInformationImage>>) {
        with(result) {
            doIfSuccess {
                if (it.isNotEmpty()) {
                    fileListRecycler.isVisible = true
                    noFilesTextView.isVisible = false
                    setImagesInAdapter(it)
                } else {
                    fileListLayout.showErrorSnackBar(getString(R.string.thumbnail_bytes_error))
                }
            }
            doIfError { manageErrorInGetBytes(it) }
        }
    }

    private fun manageErrorInGetBytes(exception: Exception) {
        isLoading = false
        val message = exception.message ?: getString(R.string.thumbnail_bytes_error)
        fileListLayout.showErrorSnackBar(message)
        currentImageLoading?.let { cameraConnectFile ->
            errorsNames.add(cameraConnectFile.name)
            FilePathSaved.saveImageWithPath(
                ImageWithPathSaved(cameraConnectFile.name, PATH_ERROR_IN_PHOTO)
            )
            temporalImageListBytes.add(
                DomainInformationImage(
                    cameraConnectFile,
                    null,
                    false,
                    PATH_ERROR_IN_PHOTO
                )
            )
            uploadImagesInAdapterWithPath()
        }
    }

    private fun manageVisibilityItemsInView() {
        if (imageListNames.size > 0) {
            fileListRecycler.isVisible = true
            noFilesTextView.isVisible = false
            val itemToLoad = imageListNames.first().cameraConnectFile
            isLoading = true
            thumbnailListFragmentViewModel.getImageBytes(itemToLoad)

        } else {
            fileListRecycler.isVisible = false
            noFilesTextView.text = getString(R.string.no_images_found)
            noFilesTextView.isVisible = true
            hideLoadingDialog()
        }
    }

    private fun setImageLoadingInAdapter(list: List<DomainInformationFile>) {
        thumbnailFileListAdapter?.fileList = ArrayList()
        imageListNames = ArrayList()
        imageListNames.addAll(list)

        imageListNames.forEach { domainFile ->
            val domain = DomainInformationImage(domainFile.cameraConnectFile)
            thumbnailFileListAdapter?.addItemToList(domain)
        }
    }

    private fun setImagesInAdapter(it: List<DomainInformationImage>) {
        it.forEach {
            if (it.imageBytes != null) {
                activity?.let { context ->
                    it.internalPath =
                        it.imageBytes!!.getPathFromTemporalFile(context, it.cameraConnectFile.name)

                    FilePathSaved.saveImageWithPath(
                        ImageWithPathSaved(it.cameraConnectFile.name, it.internalPath!!)
                    )
                }
            }
            temporalImageListBytes.add(it)
        }

        uploadImagesInAdapterWithPath()
    }

    private fun uploadImagesInAdapterWithPath() {
        val imageList =
            setImagesLinkedToVideo(temporalImageListBytes, SnapshotsToLink.selectedImages)
        imageList.forEach {
            thumbnailFileListAdapter?.addItemToList(it)
        }

        isLoading = false
        uploadNewImage()
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
                        uploadNewImage()
                    }
                }
            }
        }
    }

    private fun uploadNewImage() {
        val lastPosition =
            (fileListRecycler.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
        val subList = thumbnailFileListAdapter?.fileList?.subList(0, lastPosition + 1)
            ?.filter { it.internalPath == null }

        if (!subList.isNullOrEmpty()) {
            isLoading = true
            val itemToLoad =
                subList.firstOrNull { !errorsNames.contains(it.cameraConnectFile.name) }?.cameraConnectFile
            itemToLoad?.let { itemToLoaded ->
                val item = FilePathSaved.getImageIfExist(itemToLoaded.name)
                item?.let {
                    val domainInformation =
                        DomainInformationImage(itemToLoaded, null, false, it.absolutePath)
                    if (File(it.absolutePath).exists()) {
                        setImagesInAdapter(listOf(domainInformation))
                        return
                    }
                }

                currentImageLoading = itemToLoaded
                thumbnailListFragmentViewModel.getImageBytes(itemToLoaded)
            }
        }
    }

    private fun isLastPage(): Boolean =
        thumbnailFileListAdapter?.getItemsWithImageToLoading()?.isEmpty() ?: true

    private fun setImagesLinkedToVideo(
        imageList: List<DomainInformationImage>,
        linkedImages: ArrayList<String?>?
    ): List<DomainInformationImage> {
        linkedImages?.forEach { image ->
            val index = imageList.indexOfFirst { it.cameraConnectFile.name == image }
            if (index >= 0) imageList[index].isAssociatedToVideo = true
        }
        return imageList
    }

    companion object {
        var instance: ThumbnailFileListFragment? = null
        fun getActualInstance(recreateInstance: Boolean = false): ThumbnailFileListFragment {
            if (recreateInstance) {
                return ThumbnailFileListFragment()
            }
            this.instance = instance ?: ThumbnailFileListFragment()
            return instance!!
        }

        const val PATH_ERROR_IN_PHOTO = "errorPath"
    }
}