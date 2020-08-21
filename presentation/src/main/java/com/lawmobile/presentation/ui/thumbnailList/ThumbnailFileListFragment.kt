package com.lawmobile.presentation.ui.thumbnailList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsToLink
import com.lawmobile.presentation.extensions.showErrorSnackBar
import com.lawmobile.presentation.ui.base.BaseActivity
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
    private var currentPage = 1
    private var tmpImageList = ArrayList<DomainInformationImage>()
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
        thumbnailListFragmentViewModel.getImageBytesList(currentPage)

        setThumbnailRecyclerView()
    }

    private fun configureLayoutItems() {
        textViewEvent.isVisible = false
        textViewDateAndTime.isVisible = false
        dividerViewList.isVisible = false
    }

    private fun onImageClick(file: DomainInformationImage) {
        (activity as BaseActivity).showLoadingDialog()
        startFileListIntent(file.cameraConnectFile)
    }

    private fun startFileListIntent(cameraConnectFile: CameraConnectFile) {
        val fileListIntent = Intent()
        context?.let { fileListIntent.setClass(it, SnapshotDetailActivity::class.java) }
        fileListIntent.putExtra(Constants.CAMERA_CONNECT_FILE, cameraConnectFile)
        (activity as BaseActivity).hideLoadingDialog()
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
        (activity as BaseActivity).showLoadingDialog()
        loadingThumbnailView.isVisible = false
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
            Observer(::handleImageList)
        )
    }

    private fun handleImageList(result: Result<List<DomainInformationImage>>) {
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
                    (activity as BaseActivity).hideLoadingDialog()
                }
            }
            doIfError {
                fileListLayout.showErrorSnackBar(
                    it.message ?: getString(R.string.link_images_error)
                )
                (activity as BaseActivity).hideLoadingDialog()
            }
        }
    }

    private fun setImagesInAdapter(it: List<DomainInformationImage>) {
        if (it.isEmpty()) {
            fileListRecycler.isVisible = false
            (activity as FileListActivity).noFilesTextView.isVisible = true
        } else {
            fileListRecycler.isVisible = true
            (activity as FileListActivity).noFilesTextView.isVisible = false
            loadingThumbnailView.isVisible = false

            it.forEach {
                tmpImageList.add(it)
            }

            thumbnailFileListAdapter?.fileList =
                setImagesLinkedToVideo(
                    tmpImageList,
                    SnapshotsToLink.selectedImages
                ) as ArrayList<DomainInformationImage>

            if (thereAreSixOrMoreAndInAdapterLess()) {
                loadingThumbnailView.isVisible = true
            }
        }
        isLoading = false
        (activity as BaseActivity).hideLoadingDialog()
    }

    private fun scrollListenerForPagination() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView.childCount
            val totalItemCount = thumbnailFileListAdapter?.itemCount
            val firstVisibleItemPosition =
                (recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
            if (!isLoading && !isLastPage()) {
                totalItemCount?.let {
                    if (visibleItemCount + firstVisibleItemPosition >= it && firstVisibleItemPosition >= 0) {
                        loadingThumbnailView.isVisible = true
                        currentPage++
                        isLoading = true
                        thumbnailListFragmentViewModel.getImageBytesList(currentPage)
                    }
                }
            }
        }
    }

    private fun isLastPage() =
        thumbnailFileListAdapter?.fileList?.size == thumbnailListFragmentViewModel.getImageListSize()

    private fun thereAreSixOrMoreAndInAdapterLess() =
        thumbnailFileListAdapter?.fileList?.size ?: 0 < 6 && thumbnailListFragmentViewModel.getImageListSize() >= 6

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
            val instanceFragment = instance ?: ThumbnailFileListFragment()
            instance = instanceFragment
            return instance!!
        }
    }
}