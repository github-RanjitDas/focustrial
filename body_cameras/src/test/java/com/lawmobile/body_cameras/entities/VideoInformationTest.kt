package com.lawmobile.body_cameras.entities

import com.google.gson.Gson
import org.junit.Ignore
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class VideoInformationTest {

    private val metadataJSON =
        """{"fileName":"150700AB.MP4","officerId":"kmenesesp","path":"/tmp/SD0/DCIM/201124000/","nameFolder":"201124000/","x1sn":"57014154","metadata":{"caseNumber":"789","caseNumber2":"012","dispatchNumber":"345","dispatchNumber2":"678","driverLicense":"123abc","licensePlate":"765tup","event":{"id":"11","name":"Cloud Sub","type":"Event"},"firstName":"kevin","gender":"Male","lastName":"meneses","location":"Popayan","partnerID":"afloreza","race":"Black","remarks":"ninguna","ticketNumber":"123","ticketNumber2":"456"},"annotations":[],"associatedFiles":[],"trigger":"FMA"}"""

    @Ignore("Check Later")
    @Test
    fun checkMetadataFormat() {
        val videoMetadata = VideoInformation(
            fileName = "150700AB.MP4",
            officerId = "kmenesesp",
            path = "/tmp/SD0/DCIM/201124000/",
            nameFolder = "201124000/",
            x1sn = "57014154",
            metadata = VideoMetadata(
                caseNumber = "789",
                caseNumber2 = "012",
                dispatchNumber = "345",
                dispatchNumber2 = "678",
                driverLicense = "123abc",
                licensePlate = "765tup",
                event = CameraCatalog(
                    id = "11",
                    name = "Cloud Sub",
                    type = "Event"
                ),
                firstName = "kevin",
                gender = "Male",
                lastName = "meneses",
                location = "Popayan",
                partnerID = "afloreza",
                race = "Black",
                remarks = "ninguna",
                ticketNumber = "123",
                ticketNumber2 = "456"
            ),
            associatedFiles = listOf(),
            annotations = listOf(),
            trigger = "FMA"
        )

        val metadataToJSON = Gson().toJson(videoMetadata)

        assert(metadataToJSON.contains("fileName"))
        assert(metadataToJSON.contains("officerId"))
        assert(metadataToJSON.contains("path"))
        assert(metadataToJSON.contains("nameFolder"))
        assert(metadataToJSON.contains("x1sn"))
        assert(metadataToJSON.contains("metadata"))
        assert(metadataToJSON.contains("caseNumber"))
        assert(metadataToJSON.contains("caseNumber2"))
        assert(metadataToJSON.contains("dispatchNumber"))
        assert(metadataToJSON.contains("dispatchNumber2"))
        assert(metadataToJSON.contains("driverLicense"))
        assert(metadataToJSON.contains("licensePlate"))
        assert(metadataToJSON.contains("event"))
        assert(metadataToJSON.contains("id"))
        assert(metadataToJSON.contains("name"))
        assert(metadataToJSON.contains("type"))
        assert(metadataToJSON.contains("firstName"))
        assert(metadataToJSON.contains("gender"))
        assert(metadataToJSON.contains("lastName"))
        assert(metadataToJSON.contains("location"))
        assert(metadataToJSON.contains("partnerID"))
        assert(metadataToJSON.contains("race"))
        assert(metadataToJSON.contains("remarks"))
        assert(metadataToJSON.contains("ticketNumber"))
        assert(metadataToJSON.contains("ticketNumber2"))
        assert(metadataToJSON.contains("associatedFiles"))
        assert(metadataToJSON.contains("annotations"))
        assert(metadataToJSON.contains("trigger"))

        assertEquals(metadataJSON, metadataToJSON)
    }
}
