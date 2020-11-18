package com.lawmobile.presentation.ui.linkSnapshotsToVideo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsToLink
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants.SNAPSHOTS_DATE_SELECTED
import com.lawmobile.presentation.utils.Constants.SNAPSHOTS_LINKED
import com.lawmobile.presentation.utils.Constants.SNAPSHOTS_SELECTED
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.android.synthetic.main.activity_link_snapshots.*
import javax.inject.Inject

class LinkSnapshotsActivity : BaseActivity() {

    @Inject
    lateinit var linkSnapshotsViewModel: LinkSnapshotsViewModel

    private lateinit var linkSnapshotsAdapter: LinkSnapshotsAdapter
    private var snapshotsLinked: ArrayList<String?>? = null
    private var snapshotsLinkedDate: ArrayList<String?>? = null
    private var currentPage = 1

    private lateinit var loadingDialog: AlertDialog
    private var tmpImageList = ArrayList<DomainInformationImage>()
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_snapshots)

        snapshotsLinked = intent.getStringArrayListExtra(SNAPSHOTS_LINKED)
        snapshotsLinkedDate = intent.getStringArrayListExtra(SNAPSHOTS_DATE_SELECTED)
        SnapshotsToLink.selectedImages = arrayListOf()
        SnapshotsToLink.selectedImagesDate = arrayListOf()
        if (!snapshotsLinked.isNullOrEmpty()) {
            SnapshotsToLink.selectedImages.addAll(snapshotsLinked as ArrayList)
            SnapshotsToLink.selectedImagesDate.addAll(snapshotsLinkedDate as ArrayList)
        }
        configureLoadingViews()
        linkSnapshotsAdapter = LinkSnapshotsAdapter()
        linkSnapshotsViewModel.getImageBytesList(currentPage)

        setObservers()
        setListeners()
        configureRecyclerView()
    }

    private fun configureLoadingViews() {
        loadingDialog = this.createAlertProgress()
        loadingDialog.show()
        constrain_loading.isVisible = false
    }

    private fun configureRecyclerView() {
        linkSnapshotRecyclerView.apply {
            val manager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            layoutManager = manager
            adapter = linkSnapshotsAdapter
            addOnScrollListener(scrollListenerForPagination())
        }
    }

    private fun setListeners() {
        cancelButtonSnapshotLink.setOnClickListenerCheckConnection {
            onBackPressed()
        }
        addButtonSnapshotLink.setOnClickListenerCheckConnection {
            addSnapshotsToVideo()
        }
    }

    private fun addSnapshotsToVideo() {
        if (areSnapshotsSelected()) {
            val returnIntent = Intent()
            returnIntent.putStringArrayListExtra(SNAPSHOTS_SELECTED, SnapshotsToLink.selectedImages)
            returnIntent.putStringArrayListExtra(
                SNAPSHOTS_DATE_SELECTED,
                SnapshotsToLink.selectedImagesDate
            )
            setResult(Activity.RESULT_OK, returnIntent)
            onBackPressed()
        } else {
            this.showToast(
                getString(R.string.no_new_photo_selected_message),
                Toast.LENGTH_SHORT
            )
        }
    }

    private fun areSnapshotsSelected(): Boolean {
        return if (snapshotsLinked.isNullOrEmpty())
            SnapshotsToLink.selectedImages.isNotEmpty()
        else snapshotsLinked != SnapshotsToLink.selectedImages
    }

    private fun setObservers() {
        linkSnapshotsViewModel.imageListLiveData.observe(this, Observer(::handleImageList))
    }

    private fun handleImageList(result: Result<List<DomainInformationImage>>) {
        when (result) {
            is Result.Success -> {
                setImagesInAdapter(result.data)
            }
            is Result.Error -> this.showToast(
                result.exception.message ?: getString(R.string.link_images_error),
                Toast.LENGTH_SHORT
            )
        }
    }

    private fun setImagesInAdapter(it: List<DomainInformationImage>) {
        if (it.isEmpty()) {
            linkSnapshotRecyclerView.isVisible = false
            noImagesTextView.isVisible = true
        } else {
            linkSnapshotRecyclerView.isVisible = true
            noImagesTextView.isVisible = false
            constrain_loading.isVisible = false

            it.forEach {
                tmpImageList.add(it)
            }

            linkSnapshotsAdapter.imageList = setImagesLinkedToVideo(
                tmpImageList,
                SnapshotsToLink.selectedImages
            ) as ArrayList<DomainInformationImage>

            if (thereAreSixOrMoreAndInAdapterLess()) {
                constrain_loading.isVisible = true
            }
        }
        isLoading = false
        loadingDialog.dismiss()
    }

    private fun scrollListenerForPagination() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = recyclerView.childCount
            val totalItemCount = linkSnapshotsAdapter.itemCount
            val firstVisibleItemPosition =
                (recyclerView.layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
            if (!isLoading && !isLastPage()) {
                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    constrain_loading.isVisible = true
                    currentPage++
                    isLoading = true
                    linkSnapshotsViewModel.getImageBytesList(currentPage)
                }
            }
        }
    }

    private fun isLastPage() =
        linkSnapshotsAdapter.imageList.size == linkSnapshotsViewModel.getImageListSize()

    private fun thereAreSixOrMoreAndInAdapterLess() =
        linkSnapshotsAdapter.imageList.size < 6 && linkSnapshotsViewModel.getImageListSize() >= 6

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
}