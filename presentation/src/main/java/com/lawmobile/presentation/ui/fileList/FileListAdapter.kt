package com.lawmobile.presentation.ui.fileList

import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.recyclerview.widget.RecyclerView
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.inflate
import kotlinx.android.synthetic.main.file_list_item.view.*

class FileListAdapter(
    private val listFile: List<CameraConnectFile>,
    private val onFileClick: (CameraConnectFile) -> Unit
) : RecyclerView.Adapter<FileListAdapter.FileListViewHolder>() {

    private var viewGroup: ViewGroup? = null
    private var checked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileListViewHolder {
        viewGroup = parent
        return FileListViewHolder(parent.inflate(R.layout.file_list_item), onFileClick)
    }

    override fun getItemCount(): Int {
        return listFile.size
    }

    override fun onBindViewHolder(fileListViewHolder: FileListViewHolder, position: Int) {
        fileListViewHolder.bind(listFile[position])
    }

    fun checkAllItems() {
        checked = !checked
        viewGroup?.forEach {
            it.checkboxFileListItem.isChecked = checked
        }
    }

    inner class FileListViewHolder(
        private val fileView: View,
        private val onFileClick: (CameraConnectFile) -> Unit
    ) :
        RecyclerView.ViewHolder(fileView) {

        fun bind(cameraConnectFile: CameraConnectFile) {
            fileView.run {
                cameraConnectFile.run {
                    dateFileListItem.text = date
                    durationFileListItem.text = ""
                    eventFileListItem.text = ""
                    itemFileList.setOnClickListenerCheckConnection {
                        onFileClick.invoke(this)
                    }
                }
            }
        }
    }
}

