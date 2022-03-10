package com.lawmobile.body_cameras.utils

import com.lawmobile.body_cameras.entities.NotificationResponse
import com.safefleet.mobile.kotlin_commons.extensions.convertToObject
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

class NotificationCameraHelper(
    private var commandHelper: CommandHelper
) {

    private fun reviewIfBytesInSocketHasNotificationInformation(notificationCallback: ((NotificationResponse) -> Unit)) {
        val responseCMDSocket: Result<NotificationResponse> =
            commandHelper.readInformationInputStream().convertToObject()
        responseCMDSocket.doIfSuccess {
            if (it.msg_id.toInt() == MESSAGE_IDENTIFICATION_NOTIFICATION) {
                notificationCallback(it)
            }
        }
    }

    fun reviewIfSocketHasBytesAvailableForNotification(notificationCallback: ((NotificationResponse) -> Unit)) {
        Thread {
            while (commandHelper.isConnected()) {
                if (commandHelper.isInputStreamAvailable() &&
                    commandHelper.isSocketAvailable() &&
                    canReadNotification
                ) {
                    reviewIfBytesInSocketHasNotificationInformation(notificationCallback)
                }
                Thread.sleep(5000)
            }
        }.start()
    }

    companion object {
        var canReadNotification: Boolean = true
        private const val MESSAGE_IDENTIFICATION_NOTIFICATION = 7
    }
}
