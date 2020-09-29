package com.lawmobile.presentation.ui.simpleList

import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.safefleet.mobile.commons.helpers.convertDpToPixel
import com.safefleet.mobile.commons.helpers.inflate
import kotlinx.android.synthetic.main.simple_list_recycler_item.view.*

class SimpleFileListAdapter(
    private val onFileClick: (DomainInformationFile) -> Unit,
    private val onFileCheck: ((Boolean) -> Unit)?
) : RecyclerView.Adapter<SimpleFileListAdapter.SimpleListViewHolder>() {

    var showCheckBoxes = false
    private var isSortedAscendingByDateAndTime = true
    private var isSortedAscendingByEvent = false
    var fileList = mutableListOf<DomainInformationFile>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleListViewHolder {
        return SimpleListViewHolder(
            parent.inflate(R.layout.simple_list_recycler_item),
            onFileClick,
            onFileCheck
        )
    }

    override fun onBindViewHolder(holder: SimpleListViewHolder, position: Int) {
        holder.bind(fileList[position])
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    fun uncheckAllItems() {
        val tmpList = fileList
        tmpList.forEach {
            it.isSelected = false
        }
        onFileCheck?.invoke(false)
        fileList = tmpList
    }

    fun sortByDateAndTime() {
        if (isSortedAscendingByDateAndTime) {
            fileList = fileList.sortedBy { it.cameraConnectFile.getCreationDate() } as MutableList
            isSortedAscendingByDateAndTime = false
        } else {
            fileList = fileList.sortedByDescending { it.cameraConnectFile.getCreationDate() } as MutableList
            isSortedAscendingByDateAndTime = true
        }
    }

    fun sortByEvent() {
        if (isSortedAscendingByEvent) {
            fileList =
                fileList.sortedByDescending { it.cameraConnectVideoMetadata?.metadata?.event?.name } as MutableList
            isSortedAscendingByEvent = false
        } else {
            fileList = fileList.sortedBy { it.cameraConnectVideoMetadata?.metadata?.event?.name } as MutableList
            isSortedAscendingByEvent = true
        }
    }

    inner class SimpleListViewHolder(
        private val fileView: View,
        private val onFileClick: (DomainInformationFile) -> Unit,
        private val onFileCheck: ((Boolean) -> Unit)?
    ) : RecyclerView.ViewHolder(fileView) {
        fun bind(remoteCameraFile: DomainInformationFile) {
            onFileCheck?.invoke(isAnyFileChecked())
            setDataToViews(remoteCameraFile)
            enableCheckBoxes(remoteCameraFile)
            setListener(remoteCameraFile)
        }

        private fun setDataToViews(remoteCameraFile: DomainInformationFile) {
            with(fileView) {
                with(remoteCameraFile) {
                    dateSimpleListItem.text =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            Html.fromHtml(cameraConnectFile.getCreationDate(), 0)
                        else cameraConnectFile.getCreationDate()
                    eventSimpleListItem.text =
                        cameraConnectVideoMetadata?.metadata?.event?.name ?: ""
                }
            }
        }

        private fun enableCheckBoxes(remoteCameraFile: DomainInformationFile) {
            with(fileView.checkboxSimpleListItem) {
                isVisible = showCheckBoxes
                if (showCheckBoxes) {
                    isActivated = remoteCameraFile.isSelected
                    onChecked = { buttonView, isChecked ->
                        if (buttonView.isPressed) {
                            onCheckedFile(remoteCameraFile, isChecked)
                        }
                    }
                } else {
                    fileView.dateSimpleListItem.setPadding(
                        18f.convertDpToPixel(fileView.context),
                        0,
                        0,
                        0
                    )
                }
            }
        }

        private fun setListener(remoteCameraFile: DomainInformationFile) {
            with(fileView) {
                checkboxSimpleListItem.setOnClickListenerCheckConnection {
                    selectItemFromTheList(remoteCameraFile)
                }
                simpleListLayout.setOnClickListenerCheckConnection {
                    selectItemFromTheList(remoteCameraFile)
                }

            }
        }

        private fun selectItemFromTheList(remoteCameraFile: DomainInformationFile){
            with(fileView) {
                if (showCheckBoxes) {
                    checkboxSimpleListItem.isActivated = !checkboxSimpleListItem.isActivated
                    onCheckedFile(remoteCameraFile, checkboxSimpleListItem.isActivated)
                    SnapshotsAssociatedByUser.updateAssociatedSnapshots(remoteCameraFile.cameraConnectFile)
                } else {
                    onFileClick.invoke(remoteCameraFile)
                }
            }

        }

        private fun onCheckedFile(remoteCameraFile: DomainInformationFile, isChecked: Boolean) {
            val index =
                fileList.indexOfFirst { it.cameraConnectFile.name == remoteCameraFile.cameraConnectFile.name }
            fileList[index].isSelected = isChecked
            onFileCheck?.invoke(isAnyFileChecked())
        }

        private fun isAnyFileChecked() = fileList.any { it.isSelected }
    }
}

