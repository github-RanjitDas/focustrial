package com.lawmobile.presentation.ui.fileList

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.getVideoStartTime
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.safefleet.mobile.commons.helpers.inflate
import kotlinx.android.synthetic.main.file_list_recycler_item.view.*

class FileListAdapter(
    private val onFileClick: (DomainInformationFile) -> Unit,
    private val onFileCheck: (Boolean) -> Unit
) : RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {

    private var viewGroup: ViewGroup? = null
    private var checked = false
    var fileList: List<DomainInformationFile> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListViewHolder {
        viewGroup = parent
        return FileListViewHolder(
            parent.inflate(R.layout.file_list_recycler_item),
            onFileClick,
            onFileCheck
        )
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(fileListViewHolder: FileListViewHolder, position: Int) {
        fileListViewHolder.bind(fileList[position])
    }

    fun checkAllItems() {
        val allChecked = fileList
        checked = !checked
        onFileCheck.invoke(checked)
        allChecked.forEach {
            it.isChecked = checked
        }
        fileList = allChecked
    }

    inner class FileListViewHolder(
        private val fileView: View,
        private val onFileClick: (DomainInformationFile) -> Unit,
        private val onFileCheck: (Boolean) -> Unit
    ) :
        RecyclerView.ViewHolder(fileView) {
        fun bind(remoteCameraFile: DomainInformationFile) {
            onFileCheck.invoke(isAnyFileChecked())
            fileView.run {
                remoteCameraFile.run {
                    dateFileListItem.text = cameraConnectFile.getVideoStartTime()
                    durationFileListItem.text = ""
                    eventFileListItem.text = cameraConnectVideoMetadata?.metadata?.event?.name ?: ""
                    itemFileList.setOnClickListenerCheckConnection {
                        onFileClick.invoke(this)
                    }
                    checkboxFileListItem.isChecked = remoteCameraFile.isChecked
                    checkboxFileListItem.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (buttonView.isPressed) {
                            val index =
                                fileList.indexOfFirst { it.cameraConnectFile.name == cameraConnectFile.name }
                            fileList[index].isChecked = isChecked
                            onFileCheck.invoke(isAnyFileChecked())
                        }
                    }
                }
            }
        }

        private fun isAnyFileChecked() = fileList.any { it.isChecked }
    }
}

