package com.lawmobile.presentation.ui.fileList.x1

import android.os.Bundle
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.ui.base.appBar.x1.AppBarX1Fragment
import com.lawmobile.presentation.ui.fileList.FileListBaseActivity
import com.lawmobile.presentation.ui.fileList.filterSection.x1.FilterSectionX1Fragment
import com.lawmobile.presentation.ui.fileList.shared.FileSelection
import com.lawmobile.presentation.ui.fileList.shared.FilterSection
import com.lawmobile.presentation.ui.fileList.shared.ListTypeButtons
import com.lawmobile.presentation.utils.Constants

class FileListX1Activity : FileListBaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private lateinit var appBarFragment: AppBarX1Fragment
    private lateinit var filterSectionFragment: FilterSectionX1Fragment

    override val listTypeButtons: ListTypeButtons get() = appBarFragment
    override val fileSelection: FileSelection get() = filterSectionFragment
    override val filterSection: FilterSection get() = filterSectionFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndAttachFragments()
        setListeners()
    }

    private fun setAndAttachFragments() {
        setAppBarFragment()
        setFilterSectionFragment()
        attachAppBarFragment()
        attachFilterSectionFragment()
    }

    private fun setListeners() {
        appBarFragment.onBackPressed = ::onBackPressed
        setFragmentListeners()
    }

    private fun setFilterSectionFragment() {
        filterSectionFragment = FilterSectionX1Fragment.createInstance(
            getString(
                when (listType) {
                    Constants.SNAPSHOT_LIST -> R.string.select_snapshots_to_associate
                    else -> R.string.select_videos_to_associate
                }
            )
        )
    }

    private fun setAppBarFragment() {
        appBarFragment = when (listType) {
            Constants.SNAPSHOT_LIST ->
                AppBarX1Fragment.createInstance(getString(R.string.snapshots_title), true)
            else -> AppBarX1Fragment.createInstance(getString(R.string.videos_title))
        }
    }

    override fun attachAppBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.appBarContainer,
            fragment = appBarFragment,
            tag = AppBarX1Fragment.TAG
        )
    }

    override fun attachFilterSectionFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.selectedSectionItems,
            fragment = filterSectionFragment,
            tag = FilterSectionX1Fragment.TAG
        )
    }
}
