package com.lawmobile.presentation.utils

import com.lawmobile.domain.utils.ConnectionHelper
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CameraHelperTest {

    private val wifiHelper: WifiHelper = mockk()
    private val connectionHelper: ConnectionHelper = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val cameraHelper = CameraHelper(connectionHelper, wifiHelper, dispatcher)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testSetInstance() {
        every { connectionHelper.reviewIfArriveNotificationInCMDSocket() } just Runs
        CameraHelper.setInstance(cameraHelper)
        Assert.assertEquals(CameraHelper.getInstance(), cameraHelper)
        verify { connectionHelper.reviewIfArriveNotificationInCMDSocket() }
    }

    @Test
    fun testCheckWithAlertIfTheCameraIsConnectedSuccess() {
        every { wifiHelper.getGatewayAddress() } returns ""
        every { connectionHelper.isCameraConnected(any()) } returns true
        Assert.assertTrue(cameraHelper.checkWithAlertIfTheCameraIsConnected())
    }

    @Test
    fun testCheckWithAlertIfTheCameraIsConnectedFailed() {
        every { wifiHelper.getGatewayAddress() } returns ""
        every { connectionHelper.isCameraConnected(any()) } returns false
        Assert.assertFalse(cameraHelper.checkWithAlertIfTheCameraIsConnected())
    }

    @Test
    fun testDisconnectCameraFlow() = runBlockingTest {
        coEvery { connectionHelper.disconnectCamera() } returns Result.Success(Unit)
        cameraHelper.disconnectCamera()
        coVerify { connectionHelper.disconnectCamera() }
    }
}
