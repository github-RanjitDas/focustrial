package com.lawmobile.presentation.ui.fileList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_snapshot_list.*

class SnapshotListFragment : BaseFragment() {

    var setFileListAdapter: ((FileListAdapter) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_snapshot_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setFileListAdapter = {
            snapshotListRecycler?.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = it
            }
        }
    }

    companion object {
        var instance: SnapshotListFragment? = null
        fun getActualInstance(): SnapshotListFragment {
            val instanceFragment = instance ?: SnapshotListFragment()
            instance = instanceFragment
            return instance!!
        }
    }
}
