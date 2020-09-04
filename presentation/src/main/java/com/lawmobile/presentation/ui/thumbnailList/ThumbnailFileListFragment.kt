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
import com.lawmobile.presentation.entities.SnapshotsToLink
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.fileList.FileListActivity
import com.lawmobile.presentation.ui.snapshotDetail.SnapshotDetailActivity
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import kotlinx.android.synthetic.main.fragment_file_list.*

class ThumbnailFileListFragment : BaseFragment() {

    private val thumbnailListFragmentViewModel: ThumbnailListFragmentViewModel by viewModels()
    private var temporalImageListBytes = ArrayList<DomainInformationImage>()
    private var imageListNames = ArrayList<DomainInformationFile>()
    private var isLoading = false

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
        fileListRecycler.apply {
            val manager = GridLayoutManager(context, 2)
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
        with(result) {
            doIfSuccess {
                if (it.size > imageListNames.size) {
                    setImageLoadingInAdapter(it)
                }
                manageVisibilityItemsInView()
            }
            doIfError {
                activity?.showToast(
                    it.localizedMessage ?: "Error getting images",
                    Toast.LENGTH_LONG
                )
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
                    noFilesTextView.text = getString(R.string.no_images_found)
                    fileListRecycler.isVisible = false
                    noFilesTextView.isVisible = true
                    hideLoadingDialog()
                }
            }
            doIfError {
                isLoading = false
                fileListLayout.showErrorSnackBar(
                    it.message ?: getString(R.string.link_images_error)
                )
                hideLoadingDialog()
            }
        }
    }

    private fun manageVisibilityItemsInView() {
        if (imageListNames.size >= 0) {
            fileListRecycler.isVisible = true
            (activity as FileListActivity).noFilesTextView.isVisible = false
            thumbnailListFragmentViewModel.getImageBytesList(imageListNames.first().cameraConnectFile)
            isLoading = true
            return
        }

        fileListRecycler.isVisible = false
        (activity as FileListActivity).noFilesTextView.isVisible = true
    }

    private fun setImageLoadingInAdapter(list: List<DomainInformationFile>) {
        imageListNames.addAll(list)
        imageListNames.forEach { domainFile ->
            val domain = DomainInformationImage(domainFile.cameraConnectFile)
            thumbnailFileListAdapter?.addItemToList(domain)
        }
    }

    private fun setImagesInAdapter(it: List<DomainInformationImage>) {

        it.forEach {
            temporalImageListBytes.add(it)
        }

        val imageList =
            setImagesLinkedToVideo(temporalImageListBytes, SnapshotsToLink.selectedImages)
        imageList.forEach {
            thumbnailFileListAdapter?.addItemToList(it)
        }

        isLoading = false
        hideLoadingDialog()
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

    private fun uploadNewImage(){
        val lastPosition =
            (fileListRecycler.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
        val subList = thumbnailFileListAdapter?.fileList?.subList(0, lastPosition + 1)
            ?.filter { it.imageBytes == null }

        if (!subList.isNullOrEmpty()) {
            isLoading = true
            thumbnailListFragmentViewModel.getImageBytesList(subList.first().cameraConnectFile)
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
        fun getActualInstance(): ThumbnailFileListFragment {
            this.instance = instance ?: ThumbnailFileListFragment()
            return instance!!
        }
    }
}