package com.lawmobile.presentation.ui.thumbnailList

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.imageHasCorrectFormat
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.thumbnailList.ThumbnailFileListFragment.Companion.PATH_ERROR_IN_PHOTO
import com.safefleet.mobile.commons.helpers.inflate
import kotlinx.android.synthetic.main.thumbnail_list_recycler_item.view.*
import java.io.File

class ThumbnailFileListAdapter(
    private val onImageClick: ((DomainInformationImage) -> Unit),
    private val onImageCheck: ((Boolean, Int) -> Unit)?
) : RecyclerView.Adapter<ThumbnailFileListAdapter.ThumbnailListViewHolder>() {

    var showCheckBoxes = false
    var fileList = mutableListOf<DomainInformationImage>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailListViewHolder {
        return ThumbnailListViewHolder(
            parent.inflate(R.layout.thumbnail_list_recycler_item),
            onImageClick,
            onImageCheck
        )
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(thumbnailListViewHolder: ThumbnailListViewHolder, position: Int) {
        thumbnailListViewHolder.bind(fileList[position])
    }

    fun uncheckAllItems() {
        val tmpList = fileList
        tmpList.forEach {
            it.isSelected = false
        }
        onImageCheck?.invoke(false, 0)
        fileList = tmpList
    }

    fun getItemsWithImageToLoading(): List<DomainInformationImage> {
        return fileList.filter { it.internalPath == null }
    }

    fun itemWithImagesLoaded(): List<DomainInformationImage> {
        return fileList.filter { it.internalPath != null }
    }

    fun addItemToList(domainInformationImage: DomainInformationImage) {
        val indexOrFirst =
            fileList.indexOfFirst { it.cameraConnectFile.name == domainInformationImage.cameraConnectFile.name }
        if (indexOrFirst >= 0) {
            fileList[indexOrFirst] =
                domainInformationImage.apply {
                    isSelected = fileList[indexOrFirst].isSelected
                }
        } else {
            fileList.add(domainInformationImage)
        }

        notifyDataSetChanged()
    }

    inner class ThumbnailListViewHolder(
        private val thumbnailView: View,
        private val onImageClick: ((DomainInformationImage) -> Unit),
        private val onImageCheck: ((Boolean, Int) -> Unit)?
    ) :
        RecyclerView.ViewHolder(thumbnailView) {

        fun bind(imageFile: DomainInformationImage) {
            onImageCheck?.invoke(isAnyFileChecked(), selectedItemsSize())
            setDataToViews(imageFile)
            enableCheckBoxes(imageFile)
            setListener(imageFile)
        }

        private fun setDataToViews(imageFile: DomainInformationImage) {
            with(thumbnailView) {
                dateImageListItem.text = imageFile.cameraConnectFile.getCreationDate()
                manageImagePath(imageFile)
                checkboxImageListItem.isActivated = imageFile.isSelected
            }
        }

        private fun manageImagePath(imageFile: DomainInformationImage) {
            with(thumbnailView) {
                imageFile.internalPath?.let {
                    if (!it.imageHasCorrectFormat()) {
                        imageErrorThumbnail.isVisible = true
                        photoImageLoading.isVisible = false
                        photoImageListItem.isVisible = false
                        return
                    }

                    try {
                        photoImageLoading.isVisible = false
                        imageErrorThumbnail.isVisible = it == PATH_ERROR_IN_PHOTO
                        if (it != PATH_ERROR_IN_PHOTO) {
                            Glide.with(this).load(File(it)).into(photoImageListItem)
                            photoImageListItem.isVisible = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        val domain =
                            fileList.first { item -> item.cameraConnectFile.name == imageFile.cameraConnectFile.name }
                        domain.internalPath = null
                        addItemToList(domain)
                    }
                } ?: run {
                    photoImageListItem.isVisible = false
                    photoImageLoading.isVisible = true
                }
            }

        }

        private fun enableCheckBoxes(imageFile: DomainInformationImage) {
            with(thumbnailView.checkboxImageListItem) {
                isVisible = showCheckBoxes
                if (showCheckBoxes) {
                    isActivated = imageFile.isSelected
                    onChecked = { buttonView, isChecked ->
                        if (buttonView.isPressed) {
                            onCheckedImage(imageFile, isChecked)
                        }
                    }
                }
            }
        }

        private fun setListener(imageFile: DomainInformationImage) {
            with(thumbnailView) {
                imageListLayout.setOnClickListenerCheckConnection {
                    if (showCheckBoxes) {
                        imageFile.isSelected = !imageFile.isSelected
                        checkboxImageListItem.isActivated = imageFile.isSelected
                        onCheckedImage(imageFile, checkboxImageListItem.isActivated)
                    } else {
                        onImageClick.invoke(imageFile)
                    }
                }
            }
        }

        private fun onCheckedImage(imageFile: DomainInformationImage, isChecked: Boolean) {
            val index =
                fileList.indexOfFirst { it.cameraConnectFile.name == imageFile.cameraConnectFile.name }
            fileList[index].isSelected = isChecked
            SnapshotsAssociatedByUser.updateAssociatedSnapshots(imageFile.cameraConnectFile)
            onImageCheck?.invoke(isAnyFileChecked(), selectedItemsSize())
        }

        private fun selectedItemsSize() = fileList.filter { it.isSelected }.size

        private fun isAnyFileChecked() = fileList.any { it.isSelected }
    }
}