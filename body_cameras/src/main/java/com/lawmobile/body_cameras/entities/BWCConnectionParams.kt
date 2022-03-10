package com.lawmobile.body_cameras.entities

object BWCConnectionParams {
    var hostnameToConnect: String = ""
    var ipAddressClient: String = ""
    var sessionToken: Int = 0

    fun saveConnectionParams(hostnameToConnect: String, ipAddressClient: String) {
        BWCConnectionParams.hostnameToConnect = hostnameToConnect
        BWCConnectionParams.ipAddressClient = ipAddressClient
    }
}
