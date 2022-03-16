package com.lawmobile.presentation.ui.fileList.x2

import android.os.Bundle
import com.lawmobile.presentation.R
import com.lawmobile.presentation.entities.MenuInformation
import com.lawmobile.presentation.extensions.activityLaunch
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.extensions.closeMenuButton
import com.lawmobile.presentation.extensions.openMenuButton
import com.lawmobile.presentation.ui.base.appBar.x2.AppBarX2Fragment
import com.lawmobile.presentation.ui.base.menu.MenuFragment
import com.lawmobile.presentation.ui.base.settingsBar.SettingsBarFragment
import com.lawmobile.presentation.ui.fileList.FileListBaseActivity
import com.lawmobile.presentation.ui.fileList.filterSection.x2.FilterSectionX2Fragment
import com.lawmobile.presentation.ui.fileList.shared.FileSelection
import com.lawmobile.presentation.ui.fileList.shared.FilterSection
import com.lawmobile.presentation.ui.fileList.shared.ListTypeButtons
import com.lawmobile.presentation.utils.Constants
import com.lawmobile.presentation.utils.FeatureSupportHelper

class FileListX2Activity : FileListBaseActivity() {

    override val parentTag: String get() = this::class.java.simpleName

    private val menuFragment = MenuFragment()
    private lateinit var menuInformation: MenuInformation
    private lateinit var appBarFragment: AppBarX2Fragment
    private lateinit var filterSectionFragment: FilterSectionX2Fragment

    private val settingsBarFragment = SettingsBarFragment()
    override val listTypeButtons: ListTypeButtons get() = filterSectionFragment
    override val fileSelection: FileSelection get() = filterSectionFragment
    override val filterSection: FilterSection get() = filterSectionFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndAttachFragments()
        setListeners()
        menuInformation =
            MenuInformation(this, menuFragment, binding.layoutCustomMenu.shadowOpenMenuView)
    }

    override fun onResume() {
        super.onResume()
        getFragmentInformation()
    }

    private fun getFragmentInformation() {
        activityLaunch {
            appBarFragment.getUnreadNotificationCount()
            if (FeatureSupportHelper.supportBodyWornSettings) settingsBarFragment.getBodyCameraSettings()
        }
    }

    private fun setAndAttachFragments() {
        setFragmentsDependingOnListType()
        attachAppBarFragment()
        attachFilterSectionFragment()
        attachStatusBarSettingsFragment()
        attachMenuFragment()
    }

    private fun setListeners() {
        appBarFragment.onBackPressed = ::onBackPressed
        menuButtonsListener()
        setFragmentListeners()
    }

    private fun menuButtonsListener() {
        menuFragment.onCloseMenuButton = {
            binding.layoutCustomMenu.menuContainer.closeMenuButton(menuInformation)
        }

        appBarFragment.onTapMenuButton = {
            binding.layoutCustomMenu.menuContainer.openMenuButton(menuInformation)
        }
    }

    private fun setFragmentsDependingOnListType() {
        when (listType) {
            Constants.SNAPSHOT_LIST -> {
                appBarFragment =
                    AppBarX2Fragment.createInstance(false, getString(R.string.snapshots_title))
                filterSectionFragment = FilterSectionX2Fragment.createInstance(true)
            }
            Constants.VIDEO_LIST -> {
                appBarFragment =
                    AppBarX2Fragment.createInstance(false, getString(R.string.videos_title))
                filterSectionFragment = FilterSectionX2Fragment.createInstance(false)
            }
            Constants.AUDIO_LIST -> {
                appBarFragment =
                    AppBarX2Fragment.createInstance(false, getString(R.string.audios_title))
                filterSectionFragment = FilterSectionX2Fragment.createInstance(false)
            }
        }
    }

    private fun attachMenuFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.menuContainer,
            fragment = menuFragment,
            tag = MenuFragment.TAG
        )
    }

    override fun attachAppBarFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.appBarContainer,
            fragment = appBarFragment,
            tag = AppBarX2Fragment.TAG
        )
    }

    override fun attachFilterSectionFragment() {
        supportFragmentManager.attachFragment(
            containerId = R.id.selectedSectionItems,
            fragment = filterSectionFragment,
            tag = FilterSectionX2Fragment.TAG
        )
    }

    private fun attachStatusBarSettingsFragment() {
        if (FeatureSupportHelper.supportBodyWornSettings) {
            supportFragmentManager.attachFragment(
                containerId = R.id.statusBarFragment,
                fragment = settingsBarFragment,
                tag = SettingsBarFragment.TAG
            )
        }
    }
}
