package com.safefleet.lawmobile.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.Suppress
import com.lawmobile.presentation.ui.login.LoginActivity
import com.safefleet.lawmobile.R
import com.safefleet.lawmobile.screens.*
import com.safefleet.lawmobile.testData.VideoPlaybackMetadata
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@Suppress
@RunWith(AndroidJUnit4::class)
class AssociateSnapshotsTest :
    EspressoStartActivityBaseTest<LoginActivity>(LoginActivity::class.java) {

    companion object {
        private val defaultMetadata = VideoPlaybackMetadata.DEFAULT_VIDEO_METADATA.value

        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
        private val videoPlaybackScreen = VideoPlaybackScreen()
        private val associateSnapshotsScreen = AssociateSnapshotsScreen()
        private val filterDialogScreen = FilterDialogScreen()
    }

    @Before
    fun login() = LoginScreen().login()

    private fun setSimpleListViews() {
        with(fileListScreen) {
            targetView = R.id.dateSimpleListItem
            targetCheckBox = R.id.checkboxSimpleListItem
        }
    }

    private fun setThumbnailListViews() {
        with(fileListScreen) {
            targetView = R.id.dateImageListItem
            targetCheckBox = R.id.checkboxImageListItem
        }
    }

    @Test
    fun linkThumbnailSnapshotsToVideo_FMA_1389() {
        setThumbnailListViews()
        liveViewScreen.openVideoList()
        with(fileListScreen) {
            clickOnItemInPosition(1)
            with(videoPlaybackScreen) {
                isAddSnapshotsButtonDisplayed()
                thereIsNoSnapshotAssociated()
                clickOnAddSnapshots()

                associateSnapshotsScreen.isAssociateScreenButtonsDisplayed()

                clickOnItemInPosition(0)
                clickOnItemInPosition(3)
                clickOnItemInPosition(5)

                associateSnapshotsScreen.clickOnAssociateSnapshots()
                associateSnapshotsScreen.isSnapshotsAddedSuccessDisplayed()
                thereAreSnapshotsAssociated()

                selectEvent(defaultMetadata)
                clickOnSave()
                //To be completed because an error when retrieving the mocked data
            }
        }
    }

    @Test
    fun linkSimpleSnapshotsToVideo_FMA_1389() {
        setSimpleListViews()
        liveViewScreen.openVideoList()
        with(fileListScreen) {
            clickOnItemInPosition(1)
            with(videoPlaybackScreen) {
                isAddSnapshotsButtonDisplayed()
                thereIsNoSnapshotAssociated()
                clickOnAddSnapshots()

                associateSnapshotsScreen.isAssociateScreenButtonsDisplayed()
                associateSnapshotsScreen.clickOnSimpleListButton()

                clickOnItemInPosition(0)
                clickOnItemInPosition(3)
                clickOnItemInPosition(5)

                associateSnapshotsScreen.clickOnAssociateSnapshots()
                associateSnapshotsScreen.isSnapshotsAddedSuccessDisplayed()
                thereAreSnapshotsAssociated()

                selectEvent(defaultMetadata)
                clickOnSave()
                //To be completed because an error when retrieving the mocked data
            }
        }
    }

    @Test
    fun linkSnapshotsToVideoCancel_FMA_1396() {
        setThumbnailListViews()
        liveViewScreen.openVideoList()
        with(fileListScreen) {
            clickOnItemInPosition(1)
            with(videoPlaybackScreen) {
                isAddSnapshotsButtonDisplayed()
                thereIsNoSnapshotAssociated()
                clickOnAddSnapshots()

                associateSnapshotsScreen.isAssociateScreenButtonsDisplayed()

                clickOnItemInPosition(0)
                clickOnItemInPosition(3)
                clickOnItemInPosition(5)

                associateSnapshotsScreen.clickOnClose()
                thereIsNoSnapshotAssociated()

                selectEvent(defaultMetadata)
                clickOnSave()

                clickOnItemInPosition(1)
                thereIsNoSnapshotAssociated()
            }
        }
    }

    @Test
    fun linkSimpleToVideoCancel_FMA_1396() {
        setSimpleListViews()
        liveViewScreen.openVideoList()
        with(fileListScreen) {
            clickOnItemInPosition(1)
            with(videoPlaybackScreen) {
                isAddSnapshotsButtonDisplayed()
                thereIsNoSnapshotAssociated()
                clickOnAddSnapshots()

                associateSnapshotsScreen.isAssociateScreenButtonsDisplayed()
                associateSnapshotsScreen.clickOnSimpleListButton()

                clickOnItemInPosition(0)
                clickOnItemInPosition(3)
                clickOnItemInPosition(5)

                associateSnapshotsScreen.clickOnClose()
                thereIsNoSnapshotAssociated()

                selectEvent(defaultMetadata)
                clickOnSave()

                clickOnItemInPosition(1)
                thereIsNoSnapshotAssociated()
            }
        }
    }

    @Test
    fun linkThumbnailSnapshotsToVideoNoSnapshots_FMA_1398() {
        mockUtils.clearSnapshotsOnX1()
        liveViewScreen.openVideoList()
        with(fileListScreen) {
            clickOnItemInPosition(1)
            with(videoPlaybackScreen) {
                isAddSnapshotsButtonDisplayed()
                thereIsNoSnapshotAssociated()
                clickOnAddSnapshots()

                isNoFilesFoundDisplayed()

                with(associateSnapshotsScreen) {
                    isAssociateScreenButtonsDisplayed()
                    clickOnAssociateSnapshots()
                    isNoSnapshotsSelectedDisplayed()
                }
            }
        }
    }

    @Test
    fun linkSimpleSnapshotsToVideoNoSnapshots_FMA_1398() {
        mockUtils.clearSnapshotsOnX1()
        liveViewScreen.openVideoList()
        with(fileListScreen) {

            clickOnItemInPosition(1)
            with(videoPlaybackScreen) {
                isAddSnapshotsButtonDisplayed()
                thereIsNoSnapshotAssociated()
                clickOnAddSnapshots()

                associateSnapshotsScreen.clickOnSimpleListButton()
                isNoFilesFoundDisplayed()

                with(associateSnapshotsScreen) {
                    isAssociateScreenButtonsDisplayed()
                    clickOnAssociateSnapshots()
                    isNoSnapshotsSelectedDisplayed()
                }
            }
        }
    }

    @Test
    fun linkThumbnailSnapshotsToVideoFilter_FMA_1400() {
        setThumbnailListViews()
        liveViewScreen.openVideoList()
        with(fileListScreen) {
            clickOnItemInPosition(1)
            with(videoPlaybackScreen) {
                isAddSnapshotsButtonDisplayed()
                thereIsNoSnapshotAssociated()
                clickOnAddSnapshots()

                with(associateSnapshotsScreen) {
                    isAssociateScreenButtonsDisplayed()
                    clickOnFilter()

                    with(filterDialogScreen) {
                        selectStartDate(2020, 6, 1)
                        clickOnOk()
                        applyFilter()
                    }

                    isFilterActive()
                }

                matchItemsCount(6)

                clickOnItemInPosition(0)
                clickOnItemInPosition(3)
                clickOnItemInPosition(5)

                associateSnapshotsScreen.clickOnAssociateSnapshots()
                associateSnapshotsScreen.isSnapshotsAddedSuccessDisplayed()
                thereAreSnapshotsAssociated()

                selectEvent(defaultMetadata)
                clickOnSave()
                //not completed
            }
        }
    }

    @Test
    fun linkSimpleSnapshotsToVideoFilter_FMA_1400() {
        setSimpleListViews()
        liveViewScreen.openVideoList()
        with(fileListScreen) {

            clickOnItemInPosition(1)
            with(videoPlaybackScreen) {
                isAddSnapshotsButtonDisplayed()
                thereIsNoSnapshotAssociated()
                clickOnAddSnapshots()

                with(associateSnapshotsScreen) {
                    isAssociateScreenButtonsDisplayed()
                    clickOnSimpleListButton()
                    clickOnFilter()

                    with(filterDialogScreen) {
                        selectStartDate(2020, 6, 1)
                        clickOnOk()
                        applyFilter()
                    }

                    isFilterActive()
                }

                matchItemsCount(6)

                clickOnItemInPosition(0)
                clickOnItemInPosition(3)
                clickOnItemInPosition(5)

                associateSnapshotsScreen.clickOnAssociateSnapshots()
                associateSnapshotsScreen.isSnapshotsAddedSuccessDisplayed()
                thereAreSnapshotsAssociated()

                selectEvent(defaultMetadata)
                clickOnSave()
                //not completed
            }
        }
    }
}