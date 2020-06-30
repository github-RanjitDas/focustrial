package com.lawmobile.presentation.ui.linkSnapshotsToVideo

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsToLink
import com.lawmobile.presentation.extensions.convertBitmap
import com.safefleet.mobile.commons.helpers.inflate
import kotlinx.android.synthetic.main.snapshot_link_recycler_item.view.*

class LinkSnapshotsAdapter :
    RecyclerView.Adapter<LinkSnapshotsAdapter.LinkSnapshotViewHolder>() {

    private var viewGroup: ViewGroup? = null
    var imageList = ArrayList<DomainInformationImage>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkSnapshotViewHolder {
        viewGroup = parent
        return LinkSnapshotViewHolder(
            parent.inflate(R.layout.snapshot_link_recycler_item)
        )
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(linkSnapshotViewHolder: LinkSnapshotViewHolder, position: Int) {
        linkSnapshotViewHolder.bind(position)
    }

    inner class LinkSnapshotViewHolder(
        private val snapshotView: View
    ) :
        RecyclerView.ViewHolder(snapshotView) {
        fun bind(position: Int) {
            snapshotView.run {
                snapshotLinkDateText.text =
                    imageList[position].cameraConnectFile.date.split(" ").first()
                snapshotLinkNameText.text = imageList[position].cameraConnectFile.name
                val image = imageList[position].imageBytes.convertBitmap()
                snapshotLinkImageView.setImageBitmap(image)
                setSnapshotSelected(imageList[position].isAssociatedToVideo)
                snapshotItemLayout.setOnClickListener {
                    imageList[position].isAssociatedToVideo =
                        !imageList[position].isAssociatedToVideo
                    setSnapshotSelected(imageList[position].isAssociatedToVideo)
                    val result =
                        SnapshotsToLink.selectedImages.find { it == imageList[position].cameraConnectFile.name }
                    val resultDates =
                        SnapshotsToLink.selectedImagesDate.find { it == imageList[position].cameraConnectFile.date }
                    if (result == null) {
                        SnapshotsToLink.selectedImages.add(imageList[position].cameraConnectFile.name)
                        SnapshotsToLink.selectedImagesDate.add(imageList[position].cameraConnectFile.date)
                    } else {
                        SnapshotsToLink.selectedImages.remove(result)
                        SnapshotsToLink.selectedImagesDate.remove(resultDates)
                    }
                }
            }
        }

        private fun setSnapshotSelected(isSelected: Boolean) {
            snapshotView.run {
                if (isSelected)
                    snapshotItemLayout.background =
                        resources.getDrawable(R.drawable.snapshot_link_item_background, null)
                else snapshotItemLayout.background = null
            }
        }
    }
}