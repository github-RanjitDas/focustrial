package com.safefleet.lawmobile.screens

import androidx.test.espresso.action.ViewActions.swipeUp
import com.safefleet.lawmobile.R
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

class VideoPlaybackScreen : BaseScreen() {

    fun goToLinkSnapshots() {
        clickOn(R.id.buttonLinkSnapshots)
    }

    fun isVideoPlaybackDisplayed() {
        assertDisplayed(R.id.surfaceVideoPlayback)
        assertDisplayed(R.id.buttonPlay)
        assertDisplayed(R.id.textViewPlayerTime)
        assertDisplayed(R.id.buttonFullScreen)
        assertDisplayed(R.id.buttonAspect)
        assertDisplayed(R.id.textViewPlayerDuration)
        assertDisplayed(R.id.seekProgressVideo)
        assertDisplayed(R.id.videoNameTitle)
        assertDisplayed(R.id.videoNameValue)
        assertDisplayed(R.id.cancelButtonVideoPlayback)
        assertDisplayed(R.id.saveButtonVideoPlayback)
        assertDisplayed(R.id.buttonLinkSnapshots)
    }

    fun scrollDown() {
        swipeUp()
    }

    fun clickOnPlay() = clickOn(R.id.buttonPlay)

    fun clickOnSave() = clickOn(R.id.saveButtonVideoPlayback)

    fun clickOnCancel() = clickOn(R.id.cancelButtonVideoPlayback)

}