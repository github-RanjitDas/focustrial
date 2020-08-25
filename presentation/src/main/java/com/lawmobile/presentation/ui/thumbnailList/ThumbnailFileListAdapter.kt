package com.lawmobile.presentation.ui.thumbnailList

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsToLink
import com.lawmobile.presentation.extensions.convertBitmap
import com.lawmobile.presentation.extensions.getCreationDate
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.safefleet.mobile.commons.helpers.inflate
import kotlinx.android.synthetic.main.thumbnail_list_recycler_item.view.*

class ThumbnailFileListAdapter(
    private val onImageClick: ((DomainInformationImage) -> Unit),
    private val onImageCheck: ((Boolean) -> Unit)?
) : RecyclerView.Adapter<ThumbnailFileListAdapter.ThumbnailListViewHolder>() {

    var showCheckBoxes = false
    var fileList = ArrayList<DomainInformationImage>()
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
            it.isAssociatedToVideo = false
        }
        onImageCheck?.invoke(false)
        fileList = tmpList
    }

    inner class ThumbnailListViewHolder(
        private val thumbnailView: View,
        private val onImageClick: ((DomainInformationImage) -> Unit),
        private val onImageCheck: ((Boolean) -> Unit)?
    ) :
        RecyclerView.ViewHolder(thumbnailView) {

        fun bind(imageFile: DomainInformationImage) {
            setDataToViews(imageFile)
            enableCheckBoxes(imageFile)
            setListener(imageFile)
        }

        private fun setDataToViews(imageFile: DomainInformationImage) {
            with(thumbnailView) {
                dateImageListItem.text =
                    imageFile.cameraConnectFile.getCreationDate()
                val image = imageFile.imageBytes.convertBitmap()
                photoImageListItem.setImageBitmap(image)
                checkboxImageListItem.isActivated = imageFile.isAssociatedToVideo
            }
        }

        private fun enableCheckBoxes(imageFile: DomainInformationImage) {
            with(thumbnailView.checkboxImageListItem) {
                isVisible = showCheckBoxes
                if (showCheckBoxes) {
                    isActivated = imageFile.isAssociatedToVideo
                    onChecked = { buttonView, isChecked ->
                        if (buttonView.isPressed) {
                            onCheckedImage(imageFile, isChecked)
                        }
                    }
                }
            }
        }

        private fun isAnyFileChecked() = fileList.any { it.isAssociatedToVideo }

        private fun setListener(imageFile: DomainInformationImage) {
            with(thumbnailView) {
                imageListLayout.setOnClickListenerCheckConnection {
                    if (showCheckBoxes) {
                        imageFile.isAssociatedToVideo = !imageFile.isAssociatedToVideo
                        updateLinkedImages(imageFile)
                        checkboxImageListItem.isActivated = imageFile.isAssociatedToVideo
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
            fileList[index].isAssociatedToVideo = isChecked
            onImageCheck?.invoke(isAnyFileChecked())
        }

        private fun updateLinkedImages(imageFile: DomainInformationImage) {
            val result =
                SnapshotsToLink.selectedImages.find { it == imageFile.cameraConnectFile.name }
            val resultDates =
                SnapshotsToLink.selectedImagesDate.find { it == imageFile.cameraConnectFile.date }
            if (result == null) {
                SnapshotsToLink.selectedImages.add(imageFile.cameraConnectFile.name)
                SnapshotsToLink.selectedImagesDate.add(imageFile.cameraConnectFile.date)
            } else {
                SnapshotsToLink.selectedImages.remove(result)
                SnapshotsToLink.selectedImagesDate.remove(resultDates)
            }
        }
    }
}