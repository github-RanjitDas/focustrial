package com.lawmobile.presentation.ui.fileList

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.lawmobile.presentation.R
import com.lawmobile.presentation.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_video_list.*

class VideoListFragment : BaseFragment() {

    var setFileListAdapter: ((FileListAdapter) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_list, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setFileListAdapter = {
            videoListRecycler?.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = it
            }
        }
    }

    companion object {
        var instance: VideoListFragment? = null
        fun getActualInstance(): VideoListFragment {
            val instanceFragment = instance ?: VideoListFragment()
            instance = instanceFragment
            return instance!!
        }
    }
}
