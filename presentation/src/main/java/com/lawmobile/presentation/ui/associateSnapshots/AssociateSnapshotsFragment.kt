package com.lawmobile.presentation.ui.associateSnapshots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.SnapshotsAssociatedByUser
import com.lawmobile.presentation.extensions.*
import com.lawmobile.presentation.ui.base.BaseFragment
import com.lawmobile.presentation.ui.simpleList.SimpleFileListFragment
import com.lawmobile.presentation.ui.thumbnailList.ThumbnailFileListFragment
import com.lawmobile.presentation.utils.Constants.FILE_LIST_TYPE
import com.lawmobile.presentation.utils.Constants.SIMPLE_FILE_LIST
import com.lawmobile.presentation.utils.Constants.SNAPSHOT_LIST
import com.lawmobile.presentation.utils.Constants.THUMBNAIL_FILE_LIST
import com.safefleet.mobile.avml.cameras.entities.PhotoAssociated
import kotlinx.android.synthetic.main.file_list_filter_dialog.*
import kotlinx.android.synthetic.main.fragment_associate_snapshots.*

class AssociateSnapshotsFragment : BaseFragment() {

    private var simpleFileListFragment = SimpleFileListFragment()
    private var thumbnailFileListFragment = ThumbnailFileListFragment()
    private lateinit var actualFragment: String
    private var snapshotsAssociatedFromMetadata: MutableList<PhotoAssociated>? = null
    var onSnapshotsAssociated: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_associate_snapshots, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        simpleFileListFragment.arguments =
            Bundle().apply { putString(FILE_LIST_TYPE, SNAPSHOT_LIST) }
        setListeners()
        setThumbnailListFragment()
    }

    fun replaceSnapshotsAssociatedFromMetadata(list: MutableList<PhotoAssociated>) {
        snapshotsAssociatedFromMetadata = mutableListOf()
        snapshotsAssociatedFromMetadata?.addAll(list)
    }

    private fun setListeners() {
        buttonFilterAssociateImages.setOnClickListenerCheckConnection {
            showFilterDialog()
        }
        buttonThumbnailListAssociate.setClickListenerCheckConnection {
            setThumbnailListFragment()
        }
        buttonSimpleListAssociate.setClickListenerCheckConnection {
            setSimpleFileListFragment()
        }
        buttonAssociateImages.setOnClickListenerCheckConnection {
            addSnapshotsToVideo()
        }
    }

    private fun addSnapshotsToVideo() {
        if (areSnapshotsSelected()) {
            onSnapshotsAssociated?.invoke()
        } else {
            layoutAssociateImagesToVideo.showErrorSnackBar(getString(R.string.no_new_photo_selected_message))
        }
    }

    private fun areSnapshotsSelected(): Boolean {
        return if (snapshotsAssociatedFromMetadata.isNullOrEmpty())
            SnapshotsAssociatedByUser.value.isNotEmpty()
        else snapshotsAssociatedFromMetadata != SnapshotsAssociatedByUser.value
    }

    private fun showFilterDialog() {
        var listToFilter: List<DomainInformationForList> = emptyList()

        when (actualFragment) {
            SIMPLE_FILE_LIST -> {
                listToFilter =
                    simpleFileListFragment.simpleFileListAdapter?.fileList as List<DomainInformationForList>
            }
            THUMBNAIL_FILE_LIST -> {
                listToFilter =
                    thumbnailFileListFragment.thumbnailFileListAdapter?.fileList as List<DomainInformationForList>
            }
        }

        if (!listToFilter.isNullOrEmpty()) {
            requireActivity().createFilterDialog(layoutAssociateFilterTags, listToFilter).apply {
                eventsSpinnerFilter.isVisible = false
                onApplyClick = ::handleOnApplyFilter
            }
        }
    }

    private fun handleOnApplyFilter(it: Boolean) {
        this@AssociateSnapshotsFragment.scrollFilterAssociateTags.isVisible = it
        when (actualFragment) {
            SIMPLE_FILE_LIST ->
                simpleFileListFragment.applyFiltersToList(buttonFilterAssociateImages, !it)
            THUMBNAIL_FILE_LIST ->
                thumbnailFileListFragment.applyFiltersToList(buttonFilterAssociateImages, !it)
        }
    }

    private fun setSimpleFileListFragment() {
        SimpleFileListFragment.checkableListInit = true
        actualFragment = SIMPLE_FILE_LIST
        simpleFileListFragment
        buttonSimpleListAssociate.isActivated = true
        buttonThumbnailListAssociate.isActivated = false
        childFragmentManager.attachFragment(
            R.id.fragmentAssociateListHolder,
            simpleFileListFragment,
            SIMPLE_FILE_LIST
        )
    }

    private fun setThumbnailListFragment() {
        ThumbnailFileListFragment.checkableListInit = true
        actualFragment = THUMBNAIL_FILE_LIST
        buttonThumbnailListAssociate.isActivated = true
        buttonSimpleListAssociate.isActivated = false
        childFragmentManager.attachFragment(
            R.id.fragmentAssociateListHolder,
            thumbnailFileListFragment,
            THUMBNAIL_FILE_LIST
        )
    }

    override fun onDestroy() {
        SimpleFileListFragment.destroyInstance()
        ThumbnailFileListFragment.destroyInstance()
        super.onDestroy()
    }

    companion object {
        val TAG = AssociateSnapshotsFragment::class.java.simpleName
        private var instance: AssociateSnapshotsFragment? = null
        fun getActualInstance(): AssociateSnapshotsFragment {
            val fragmentInstance = instance ?: AssociateSnapshotsFragment()
            instance = fragmentInstance
            return instance!!
        }

        fun destroyInstance() {
            instance = null
        }
    }
}