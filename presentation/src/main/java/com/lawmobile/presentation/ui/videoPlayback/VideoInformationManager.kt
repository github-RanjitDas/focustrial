package com.lawmobile.presentation.ui.videoPlayback

import android.content.Context
import android.text.InputFilter
import android.widget.ArrayAdapter
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.FilesAssociatedByUser
import com.lawmobile.domain.entities.MetadataEvent
import com.lawmobile.domain.entities.VideoDetailMetaDataCached
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.VideoMetadataFormBinding
import com.lawmobile.presentation.extensions.onItemSelected

class VideoInformationManager {
    private lateinit var binding: VideoMetadataFormBinding
    private lateinit var cameraFile: DomainCameraFile
    private lateinit var context: Context

    private lateinit var eventList: MutableList<String>
    private lateinit var genderList: MutableList<String>
    private lateinit var raceList: MutableList<String>

    private var eventInput: Int = 0
    private var genderInput: Int = 0
    private var raceInput: Int = 0
    private lateinit var restoreVideoMetaDataFromCache: RestoreVideoMetaDataFromCache

    fun setRestoreVideoMetaDataCallback(restoreVideoMetaDataFromCache: RestoreVideoMetaDataFromCache) {
        this.restoreVideoMetaDataFromCache = restoreVideoMetaDataFromCache
    }

    fun setup(binding: VideoMetadataFormBinding, cameraFile: DomainCameraFile) {
        this.binding = binding
        this.cameraFile = cameraFile
        context = binding.root.context
        addEditTextFilter()
        setSpinnerListeners()
    }

    fun setInformation(videoInformation: DomainVideoMetadata?) = with(binding) {
        videoInformation?.metadata?.run {
            setEvent(indexOfFirst(eventList, event?.name))
            ticket1Value.setText(ticketNumber)
            ticket2Value.setText(ticketNumber2)
            case1Value.setText(caseNumber)
            case2Value.setText(caseNumber2)
            dispatch1Value.setText(dispatchNumber)
            dispatch2Value.setText(dispatchNumber2)
            partnerIdValue.setText(partnerID)
            locationValue.setText(location)
            firstNameValue.setText(firstName)
            lastNameValue.setText(lastName)
            setGender(indexOfFirst(genderList, gender))
            setRace(indexOfFirst(raceList, race))
            driverLicenseValue.setText(driverLicense)
            licensePlateValue.setText(licensePlate)
            notesValue.setText(remarks)
        }

        videoInformation?.associatedFiles?.let {
            FilesAssociatedByUser.setTemporalValue(it as MutableList)
            FilesAssociatedByUser.setFinalValue(it)
        }

        restoreVideoMetaDataFromCache.onRestoreVideoMetaData()
    }

    fun saveVideoDetailMetaData(progress: Int) {
        CameraInfo.videoDetailMetaDataCached = VideoDetailMetaDataCached(
            eventInput,
            binding.partnerIdValue.text.toString(),
            binding.ticket1Value.text.toString(),
            binding.ticket2Value.text.toString(),
            binding.case1Value.text.toString(),
            binding.case2Value.text.toString(),
            binding.dispatch1Value.text.toString(),
            binding.dispatch2Value.text.toString(),
            binding.locationValue.text.toString(),
            binding.firstNameValue.text.toString(),
            binding.lastNameValue.text.toString(),
            genderInput,
            raceInput,
            binding.driverLicenseValue.text.toString(),
            binding.licensePlateValue.text.toString(),
            binding.notesValue.text.toString(),
            progress
        )
    }

    fun restoreVideoDetailMetaDataFromCache() {
        binding.eventValue.setSelection(CameraInfo.videoDetailMetaDataCached.eventPosition)
        binding.partnerIdValue.setText(CameraInfo.videoDetailMetaDataCached.partnerIdValue)
        binding.ticket1Value.setText(CameraInfo.videoDetailMetaDataCached.ticket1Value)
        binding.ticket2Value.setText(CameraInfo.videoDetailMetaDataCached.ticket2Value)
        binding.case1Value.setText(CameraInfo.videoDetailMetaDataCached.case1Value)
        binding.case2Value.setText(CameraInfo.videoDetailMetaDataCached.case2Value)
        binding.dispatch1Value.setText(CameraInfo.videoDetailMetaDataCached.dispatch1Value)
        binding.dispatch2Value.setText(CameraInfo.videoDetailMetaDataCached.dispatch2Value)
        binding.locationValue.setText(CameraInfo.videoDetailMetaDataCached.locationValue)
        binding.firstNameValue.setText(CameraInfo.videoDetailMetaDataCached.firstNameValue)
        binding.lastNameValue.setText(CameraInfo.videoDetailMetaDataCached.lastNameValue)
        binding.genderValue.setSelection(CameraInfo.videoDetailMetaDataCached.genderPosition)
        binding.raceValue.setSelection(CameraInfo.videoDetailMetaDataCached.racePosition)
        binding.driverLicenseValue.setText(CameraInfo.videoDetailMetaDataCached.driverLicenseValue)
        binding.licensePlateValue.setText(CameraInfo.videoDetailMetaDataCached.licensePlateValue)
        binding.notesValue.setText(CameraInfo.videoDetailMetaDataCached.notesValue)
    }

    fun getEditedInformation(currentInformation: DomainVideoMetadata?): DomainVideoMetadata =
        binding.run {
            DomainVideoMetadata(
                fileName = cameraFile.name,
                metadata = DomainMetadata(
                    event = getSelectedMetadataEvent(),
                    partnerID = partnerIdValue.text.toString(),
                    ticketNumber = ticket1Value.text.toString(),
                    ticketNumber2 = ticket2Value.text.toString(),
                    caseNumber = case1Value.text.toString(),
                    caseNumber2 = case2Value.text.toString(),
                    dispatchNumber = dispatch1Value.text.toString(),
                    dispatchNumber2 = dispatch2Value.text.toString(),
                    location = locationValue.text.toString(),
                    remarks = notesValue.text.toString(),
                    firstName = firstNameValue.text.toString(),
                    lastName = lastNameValue.text.toString(),
                    gender = getSelectedGender(),
                    race = getSelectedRace(),
                    driverLicense = driverLicenseValue.text.toString(),
                    licensePlate = licensePlateValue.text.toString()
                ),
                nameFolder = cameraFile.nameFolder,
                officerId = CameraInfo.officerId,
                path = currentInformation?.path ?: cameraFile.path,
                associatedFiles = FilesAssociatedByUser.value,
                annotations = currentInformation?.annotations,
                serialNumber = CameraInfo.serialNumber,
                endTime = currentInformation?.endTime,
                gmtOffset = currentInformation?.gmtOffset,
                hash = currentInformation?.hash,
                preEvent = currentInformation?.preEvent,
                startTime = currentInformation?.startTime,
                videoSpecs = currentInformation?.videoSpecs,
                trigger = currentInformation?.trigger
            )
        }

    fun isEventSelected() = eventInput != 0

    fun setSpinners() = with(binding) {
        eventList =
            mutableListOf(context.getString(R.string.select)).apply { addAll(getMetadataEventsList()) }
        raceList = context.resources.getStringArray(R.array.race_spinner).toMutableList()
        genderList = context.resources.getStringArray(R.array.gender_spinner).toMutableList()

        eventValue.adapter = ArrayAdapter(context, R.layout.spinner_item, eventList)
        raceValue.adapter = ArrayAdapter(context, R.layout.spinner_item, raceList)
        genderValue.adapter = ArrayAdapter(context, R.layout.spinner_item, genderList)
    }

    private fun addEditTextFilter() = with(binding) {
        ticket1Value.filters = getFiltersWithLength(20)
        ticket2Value.filters = getFiltersWithLength(20)
        case1Value.filters = getFiltersWithLength(50)
        case2Value.filters = getFiltersWithLength(50)
        dispatch1Value.filters = getFiltersWithLength(30)
        dispatch2Value.filters = getFiltersWithLength(30)
        locationValue.filters = getFiltersWithLength(30)
        notesValue.filters = getFiltersWithLength(100)
        firstNameValue.filters = getFiltersWithLength(30)
        lastNameValue.filters = getFiltersWithLength(30)
        driverLicenseValue.filters = getFiltersWithLength(30)
        licensePlateValue.filters = getFiltersWithLength(30)
    }

    private fun getFiltersWithLength(length: Int): Array<InputFilter> {
        val lengthFilter = InputFilter.LengthFilter(length)
        val charactersFilter = InputFilter { source, _, _, _, _, _ ->
            if (source != null && containsNotAllowedCharacters(source)) ""
            else null
        }

        return arrayOf(lengthFilter, charactersFilter)
    }

    private fun containsNotAllowedCharacters(source: CharSequence): Boolean {
        return COMMA.contains("" + source) || AMPERSAND.contains("" + source) || QUOTES.contains("" + source)
    }

    private fun setSpinnerListeners() = with(binding) {
        eventValue.onItemSelected(::setEvent)
        genderValue.onItemSelected(::setGender)
        raceValue.onItemSelected(::setRace)
    }

    private fun setEvent(position: Int) {
        eventInput = position
        binding.eventValue.setSelection(position)
    }

    private fun setGender(position: Int) {
        genderInput = position
        binding.genderValue.setSelection(position)
    }

    private fun setRace(position: Int) {
        raceInput = position
        binding.raceValue.setSelection(position)
    }

    private fun indexOfFirst(list: MutableList<String>, value: String?): Int {
        val index = list.indexOfFirst { it == value }
        return if (index == -1) 0 else index
    }

    private fun getMetadataEventsList(): List<String> = CameraInfo.metadataEvents.map { it.name }

    private fun getSelectedMetadataEvent(): MetadataEvent =
        CameraInfo.metadataEvents.find { it.name == eventList.getOrNull(eventInput) }
            ?: MetadataEvent("", "", "")

    private fun getSelectedGender(): String =
        if (genderInput != 0) genderList.getOrNull(genderInput) ?: "" else ""

    private fun getSelectedRace(): String =
        if (raceInput != 0) raceList.getOrNull(raceInput) ?: "" else ""

    companion object {
        private const val COMMA = ","
        private const val AMPERSAND = "&"
        private const val QUOTES = "\""
    }
}
