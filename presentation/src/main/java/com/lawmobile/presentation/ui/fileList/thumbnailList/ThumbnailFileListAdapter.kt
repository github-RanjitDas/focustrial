package com.lawmobile.presentation.ui.fileList.thumbnailList

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.imageHasCorrectFormat
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment
import com.lawmobile.presentation.ui.fileList.thumbnailList.ThumbnailFileListFragment.Companion.PATH_ERROR_IN_PHOTO
import com.safefleet.mobile.android_commons.extensions.inflate
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetCheckBox2
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
        val indexOfFirst =
            fileList.indexOfFirst { it.domainCameraFile.name == domainInformationImage.domainCameraFile.name }
        if (indexOfFirst >= 0) {
            fileList[indexOfFirst] =
                domainInformationImage.apply {
                    isSelected = fileList[indexOfFirst].isSelected
                }
        } else {
            fileList.add(domainInformationImage)
        }

        notifyDataSetChanged()
    }

    fun isImageInAdapter(image: DomainInformationImage) =
        fileList.indexOfFirst { it.domainCameraFile.name == image.domainCameraFile.name } != -1

    inner class ThumbnailListViewHolder(
        private val thumbnailView: View,
        private val onImageClick: ((DomainInformationImage) -> Unit),
        private val onImageCheck: ((Boolean, Int) -> Unit)?
    ) : RecyclerView.ViewHolder(thumbnailView) {

        private lateinit var photoImageBackground: ImageView
        private lateinit var imageErrorThumbnail: ImageView
        private lateinit var photoImageListItem: ImageView
        private lateinit var checkboxImageListItem: SafeFleetCheckBox2
        private lateinit var dateImageListItem: TextView
        private lateinit var photoImageLoading: LottieAnimationView
        private lateinit var imageListLayout: CardView

        fun bind(imageFile: DomainInformationImage) {
            getViews()
            onImageCheck?.invoke(isAnyFileChecked(), selectedItemsSize())
            setDataToViews(imageFile)
            enableCheckBoxes(imageFile)
            setListener(imageFile)
        }

        private fun getViews() {
            photoImageBackground = thumbnailView.findViewById(R.id.photoImageBackground)
            imageErrorThumbnail = thumbnailView.findViewById(R.id.imageErrorThumbnail)
            photoImageListItem = thumbnailView.findViewById(R.id.photoImageListItem)
            checkboxImageListItem = thumbnailView.findViewById(R.id.checkboxImageListItem)
            dateImageListItem = thumbnailView.findViewById(R.id.dateImageListItem)
            photoImageLoading = thumbnailView.findViewById(R.id.photoImageLoading)
            imageListLayout = thumbnailView.findViewById(R.id.imageListLayout)
        }

        private fun setDataToViews(imageFile: DomainInformationImage) {
            dateImageListItem.text = imageFile.domainCameraFile.getCreationDate()
            manageImagePath(imageFile)
            checkboxImageListItem.isActivated = imageFile.isSelected
        }

        private fun manageImagePath(imageFile: DomainInformationImage) {
            with(thumbnailView) {
                photoImageListItem.setImageDrawable(null)
                imageFile.internalPath?.let { internalPath ->
                    if (!internalPath.imageHasCorrectFormat()) {
                        imageErrorThumbnail.isVisible = true
                        photoImageLoading.isVisible = false
                        photoImageListItem.isVisible = false
                        return
                    }

                    try {
                        photoImageLoading.isVisible = false
                        imageErrorThumbnail.isVisible = internalPath == PATH_ERROR_IN_PHOTO
                        if (internalPath != PATH_ERROR_IN_PHOTO) {
                            Glide.with(this).load(File(internalPath)).into(photoImageListItem)
                            photoImageListItem.isVisible = true
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        val domain =
                            fileList.first { item -> item.domainCameraFile.name == imageFile.domainCameraFile.name }
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
            with(checkboxImageListItem) {
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

        private fun onCheckedImage(thumbnailFile: DomainInformationImage, isChecked: Boolean) {
            val index =
                fileList.indexOfFirst { it.domainCameraFile.name == thumbnailFile.domainCameraFile.name }
            fileList[index].isSelected = isChecked
            if (FileListBaseFragment.checkableListInit) {
                SnapshotsAssociatedByUser.updateAssociatedSnapshots(thumbnailFile.domainCameraFile)
            }
            onImageCheck?.invoke(isAnyFileChecked(), selectedItemsSize())
        }

        private fun selectedItemsSize() = fileList.filter { it.isSelected }.size

        private fun isAnyFileChecked() = fileList.any { it.isSelected }
    }
}
