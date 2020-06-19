package com.lawmobile.presentation.ui.linkSnapshotsToVideo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsToLink
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseActivity
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
    private var currentPage = 1

    private lateinit var loadingDialog: AlertDialog
    private var tmpImageList = ArrayList<DomainInformationImage>()
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_snapshots)

        snapshotsLinked = intent.getStringArrayListExtra(SNAPSHOTS_LINKED)
        SnapshotsToLink.selectedImages = arrayListOf()
        SnapshotsToLink.selectedImages.addAll(snapshotsLinked as ArrayList)
        loadingDialog = this.createAlertProgress()
        loadingDialog.show()

        linkSnapshotsAdapter = LinkSnapshotsAdapter()
        linkSnapshotsViewModel.getImageBytesList(currentPage)

        setObservers()
        setListeners()
        configureRecyclerView()
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
        cancelButtonSnapshotLink.setOnClickListener {
            onBackPressed()
        }
        addButtonSnapshotLink.setOnClickListener {
            addSnapshotsToVideo()
        }
    }

    private fun addSnapshotsToVideo() {
        if (areSnapshotsSelected()) {
            val returnIntent = Intent()
            val selectedItems = SnapshotsToLink.selectedImages
            returnIntent.putStringArrayListExtra(SNAPSHOTS_SELECTED, selectedItems)
            setResult(Activity.RESULT_OK, returnIntent)
            onBackPressed()
        } else {
            this.showToast(
                getString(R.string.no_new_photo_selected_message),
                Toast.LENGTH_SHORT
            )
        }
    }

    private fun areSnapshotsSelected() =
        snapshotsLinked != SnapshotsToLink.selectedImages

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
        isLoading = false
        loadingDialog.dismiss()
        it.forEach {
            tmpImageList.add(it)
        }

        linkSnapshotsAdapter.imageList = setImagesLinkedToVideo(
            tmpImageList,
            snapshotsLinked
        ) as ArrayList<DomainInformationImage>
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
                    currentPage++
                    loadingDialog.show()
                    isLoading = true
                    linkSnapshotsViewModel.getImageBytesList(currentPage)
                }
            }
        }
    }

    private fun isLastPage() =
        linkSnapshotsAdapter.imageList.size == linkSnapshotsViewModel.getImageListSize()

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