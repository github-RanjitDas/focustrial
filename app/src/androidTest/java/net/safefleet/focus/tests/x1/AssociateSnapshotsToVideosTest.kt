package net.safefleet.focus.tests.x1

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.Suppress
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.schibsted.spain.barista.rule.flaky.AllowFlaky
import net.safefleet.focus.R
import net.safefleet.focus.screens.AssociateSnapshotsScreen
import net.safefleet.focus.screens.FileListScreen
import net.safefleet.focus.screens.FilterDialogScreen
import net.safefleet.focus.screens.LiveViewScreen
import net.safefleet.focus.screens.LoginScreen
import net.safefleet.focus.screens.VideoPlaybackScreen
import net.safefleet.focus.testData.VideoPlaybackMetadata
import net.safefleet.focus.tests.EspressoStartActivityBaseTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@Suppress
@RunWith(AndroidJUnit4::class)
class AssociateSnapshotsToVideosTest :
    EspressoStartActivityBaseTest<LoginX2Activity>(LoginX2Activity::class.java) {

    companion object {
        private val defaultMetadata = VideoPlaybackMetadata.DEFAULT_VIDEO_METADATA.value
        private val fileListScreen = FileListScreen()
        private val liveViewScreen = LiveViewScreen()
        private val videoPlaybackScreen = VideoPlaybackScreen()
        private val associateSnapshotsScreen = AssociateSnapshotsScreen()
        private val filterDialogScreen = FilterDialogScreen()
    }

    @Before
    fun setUp() {
        mockUtils.setCameraType(CameraType.X2)
        LoginScreen().login()
    }

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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1389
     */
    @AllowFlaky(attempts = 1)
    @Test
    fun linkThumbnailSnapshotsToVideo() {
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
                // To be completed because an error when retrieving the mocked data
            }
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1389
     */
    @AllowFlaky(attempts = 1)
    @Test
    fun linkSimpleSnapshotsToVideo() {
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
                // To be completed because an error when retrieving the mocked data
            }
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1396
     */
    @AllowFlaky(attempts = 1)
    @Test
    fun linkSnapshotsToVideoCancel() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1396
     */
    @AllowFlaky(attempts = 1)
    @Test
    fun linkSimpleToVideoCancel() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1398
     */
    @AllowFlaky(attempts = 1)
    @Test
    fun linkThumbnailSnapshotsToVideoNoSnapshots() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1398
     */
    @AllowFlaky(attempts = 1)
    @Test
    fun linkSimpleSnapshotsToVideoNoSnapshots() {
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

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1400
     */
    @AllowFlaky(attempts = 1)
    @Test
    fun linkThumbnailSnapshotsToVideoFilter() {
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
                // not completed
            }
        }
    }

    /**
     * Test case: https://safefleet.atlassian.net/browse/FMA-1400
     */
    @AllowFlaky(attempts = 1)
    @Test
    fun linkSimpleSnapshotsToVideoFilter() {
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
                // not completed
            }
        }
    }
}
