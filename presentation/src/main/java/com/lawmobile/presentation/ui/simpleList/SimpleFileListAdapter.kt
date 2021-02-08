package com.lawmobile.presentation.ui.simpleList

import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.setCheckedListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.ui.fileList.FileListBaseFragment
import com.safefleet.mobile.android_commons.extensions.convertDpToPixel
import com.safefleet.mobile.android_commons.extensions.inflate
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetCheckBox2

class SimpleFileListAdapter(
    private val onFileClick: (DomainInformationFile) -> Unit,
    private val onFileCheck: ((Boolean, Int) -> Unit)?
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
        onFileCheck?.invoke(false, 0)
        fileList = tmpList
    }

    fun sortByDateAndTime() {
        if (isSortedAscendingByDateAndTime) {
            fileList = fileList.sortedBy { it.domainCameraFile.getCreationDate() } as MutableList
            isSortedAscendingByDateAndTime = false
        } else {
            fileList =
                fileList.sortedByDescending { it.domainCameraFile.getCreationDate() } as MutableList
            isSortedAscendingByDateAndTime = true
        }
    }

    fun sortByEvent() {
        if (isSortedAscendingByEvent) {
            fileList =
                fileList.sortedByDescending { it.domainVideoMetadata?.metadata?.event?.name } as MutableList
            isSortedAscendingByEvent = false
        } else {
            fileList =
                fileList.sortedBy { it.domainVideoMetadata?.metadata?.event?.name } as MutableList
            isSortedAscendingByEvent = true
        }
    }

    inner class SimpleListViewHolder(
        private val fileView: View,
        private val onFileClick: (DomainInformationFile) -> Unit,
        private val onFileCheck: ((Boolean, Int) -> Unit)?
    ) : RecyclerView.ViewHolder(fileView) {

        private lateinit var dateSimpleListItem: TextView
        private lateinit var eventSimpleListItem: TextView
        private lateinit var checkboxSimpleListItem: SafeFleetCheckBox2
        private lateinit var simpleListLayout: ConstraintLayout

        private fun getViews() {
            dateSimpleListItem = fileView.findViewById(R.id.dateSimpleListItem)
            eventSimpleListItem = fileView.findViewById(R.id.eventSimpleListItem)
            checkboxSimpleListItem = fileView.findViewById(R.id.checkboxSimpleListItem)
            simpleListLayout = fileView.findViewById(R.id.simpleListLayout)
        }

        fun bind(remoteCameraFile: DomainInformationFile) {
            getViews()
            onFileCheck?.invoke(isAnyFileChecked(), selectedItemsSize())
            setDataToViews(remoteCameraFile)
            enableCheckBoxes(remoteCameraFile)
            setListener(remoteCameraFile)
        }

        private fun setDataToViews(remoteCameraFile: DomainInformationFile) {
            with(remoteCameraFile) {
                dateSimpleListItem.text =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(domainCameraFile.getCreationDate(), 0)
                    else domainCameraFile.getCreationDate()
                eventSimpleListItem.text =
                    domainVideoMetadata?.metadata?.event?.name ?: ""
            }
        }

        private fun enableCheckBoxes(remoteCameraFile: DomainInformationFile) {
            with(checkboxSimpleListItem) {
                isVisible = showCheckBoxes
                if (showCheckBoxes) {
                    isActivated = remoteCameraFile.isSelected
                    onChecked = { buttonView, isChecked ->
                        if (buttonView.isPressed) {
                            onCheckedFile(remoteCameraFile, isChecked)
                        }
                    }
                } else {
                    dateSimpleListItem.setPadding(
                        18f.convertDpToPixel(fileView.context),
                        0,
                        0,
                        0
                    )
                }
            }
        }

        private fun setListener(remoteCameraFile: DomainInformationFile) {
            checkboxSimpleListItem.setCheckedListenerCheckConnection {
                selectItemFromTheList(remoteCameraFile)
            }
            simpleListLayout.setOnClickListenerCheckConnection {
                if (showCheckBoxes) {
                        checkboxSimpleListItem.isActivated = !checkboxSimpleListItem.isActivated
                        selectItemFromTheList(remoteCameraFile)
                    } else {
                        onFileClick.invoke(remoteCameraFile)
                    }
            }
        }

        private fun selectItemFromTheList(remoteCameraFile: DomainInformationFile) {
            if (FileListBaseFragment.checkableListInit) {
                SnapshotsAssociatedByUser.updateAssociatedSnapshots(remoteCameraFile.domainCameraFile)
            }
            onCheckedFile(remoteCameraFile, checkboxSimpleListItem.isActivated)
        }

        private fun onCheckedFile(simpleFile: DomainInformationFile, isChecked: Boolean) {
            val index =
                fileList.indexOfFirst { it.domainCameraFile.name == simpleFile.domainCameraFile.name }
            fileList[index].isSelected = isChecked
            onFileCheck?.invoke(isAnyFileChecked(), selectedItemsSize())
        }

        private fun selectedItemsSize() = fileList.filter { it.isSelected }.size

        private fun isAnyFileChecked() = fileList.any { it.isSelected }
    }
}

