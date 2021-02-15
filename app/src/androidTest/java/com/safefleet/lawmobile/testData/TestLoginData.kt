package com.safefleet.lawmobile.testData

enum class TestLoginData(val value: String) {
    SERIAL_NUMBER("57014694"),
    INVALID_SERIAL_NUMBER("57012182"),
    OFFICER_PASSWORD("san 6279!"),
    OFFICER_NAME("Mr. FMA Tester"),
    SSID("X" + SERIAL_NUMBER.value),
    INVALID_SSID("X" + INVALID_SERIAL_NUMBER.value)
}
