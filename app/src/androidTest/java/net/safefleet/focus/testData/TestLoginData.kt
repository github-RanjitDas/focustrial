package net.safefleet.focus.testData

enum class TestLoginData(val value: String) {
    SERIAL_NUMBER_X1("57014694"),
    SERIAL_NUMBER_X2("01120066"),
    INVALID_SERIAL_NUMBER("57012182"),
    OFFICER_PASSWORD("san 6279!"),
    OFFICER_NAME("Mr. FMA Tester"),
    SSID_X1("X" + SERIAL_NUMBER_X1.value),
    SSID_X2("X" + SERIAL_NUMBER_X2.value),
    INVALID_SSID("X" + INVALID_SERIAL_NUMBER.value)
}
