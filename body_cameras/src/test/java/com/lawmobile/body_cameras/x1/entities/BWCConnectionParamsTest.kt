package com.lawmobile.body_cameras.x1.entities

import com.lawmobile.body_cameras.entities.BWCConnectionParams
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class BWCConnectionParamsTest {
    @Test
    fun saveParamsOfConnectionCamera() {
        val hostname = "123"
        val ipAddress = "123"

        BWCConnectionParams.saveConnectionParams(hostname, ipAddress)

        Assert.assertEquals(BWCConnectionParams.hostnameToConnect, hostname)
        Assert.assertEquals(BWCConnectionParams.ipAddressClient, ipAddress)
    }
}
