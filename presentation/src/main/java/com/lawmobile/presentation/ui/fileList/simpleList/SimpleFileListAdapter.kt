package com.lawmobile.presentation.ui.fileList.simpleList

import android.os.Build
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.FilesAssociatedByUser
import com.lawmobile.domain.extensions.getDateDependingOnNameLength
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.setCheckedListenerCheckConnection
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.safefleet.mobile.android_commons.extensions.convertDpToPixel
import com.safefleet.mobile.android_commons.extensions.inflate
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetCheckBox2

class SimpleFileListAdapter(
    private val onFileClick: (DomainInformationFile) -> Unit,
    private val onFileCheck: ((Int) -> Unit)?
) : RecyclerView.Adapter<SimpleFileListAdapter.SimpleListViewHolder>() {

    private var isSortedAscendingByDateAndTime = true
    private var isSortedAscendingByEvent = false

    var showCheckBoxes = false
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
        tmpList.forEach { it.isSelected = false }
        onFileCheck?.invoke(0)
        fileList = tmpList
    }

    fun sortByDateAndTime() {
        if (fileList.isNotEmpty()) {
            if (isSortedAscendingByDateAndTime) {
                fileList =
                    fileList.sortedBy { it.domainCameraFile.getDateDependingOnNameLength() } as MutableList
                isSortedAscendingByDateAndTime = false
            } else {
                fileList =
                    fileList.sortedByDescending { it.domainCameraFile.getDateDependingOnNameLength() } as MutableList
                isSortedAscendingByDateAndTime = true
            }
        }
    }

    fun sortByEvent() {
        if (fileList.isNotEmpty()) {
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
    }

    fun addOnlyNewItemsToList(newList: MutableList<DomainInformationFile>) {
        if (fileList.isEmpty()) fileList = newList
        else {
            val newElementsCount = newList.size - fileList.size
            if (newElementsCount > 0) {
                fileList.addAll(newList.takeLast(newElementsCount))
                notifyDataSetChanged()
            }
        }
    }

    fun updateItems(newList: MutableList<DomainInformationFile>) {
        if (fileList.isEmpty()) fileList = newList
        else {
            fileList.forEachIndexed { index, file ->
                try {
                    if (file != newList[index]) fileList[index] = newList[index]
                } catch (e: Exception) {
                    // empty block, no need to use the exception
                }
            }
        }
    }

    fun setSelectedFiles(selectedFiles: List<DomainCameraFile>) {
        val tmpList = fileList
        fileList.forEach {
            it.isSelected = selectedFiles.contains(it.domainCameraFile)
        }
        onFileCheck?.invoke(selectedFiles.size)
        fileList = tmpList
    }

    inner class SimpleListViewHolder(
        private val fileView: View,
        private val onFileClick: (DomainInformationFile) -> Unit,
        private val onFileCheck: ((Int) -> Unit)?
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
            setDataToViews(remoteCameraFile)
            enableCheckBoxes(remoteCameraFile)
            setListeners(remoteCameraFile)
        }

        private fun setDataToViews(remoteCameraFile: DomainInformationFile) {
            with(remoteCameraFile) {
                dateSimpleListItem.text =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        Html.fromHtml(domainCameraFile.getDateDependingOnNameLength(), 0)
                    else domainCameraFile.getDateDependingOnNameLength()
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

        private fun setListeners(remoteCameraFile: DomainInformationFile) {
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
            onCheckedFile(remoteCameraFile, checkboxSimpleListItem.isActivated)
        }

        private fun onCheckedFile(simpleFile: DomainInformationFile, isChecked: Boolean) {
            val index =
                fileList.indexOfFirst { it.domainCameraFile.name == simpleFile.domainCameraFile.name }
            fileList[index].isSelected = isChecked
            onFileCheck?.invoke(selectedItemsSize())
        }

        private fun selectedItemsSize() = fileList.filter { it.isSelected }.size
    }
}
