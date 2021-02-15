package com.lawmobile.presentation.widgets

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridLayout
import androidx.core.view.isVisible
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.FileListFilterDialogBinding
import com.lawmobile.presentation.extensions.ifIsNotEmptyLet
import com.lawmobile.presentation.extensions.showDateAndTimePickerDialog
import com.safefleet.mobile.safefleet_ui.widgets.SafeFleetFilterTag

class CustomFilterDialog constructor(
    private val tagsGridLayout: GridLayout,
    private var onApplyClick: (Boolean) -> Unit
) : Dialog(tagsGridLayout.context, true, null), View.OnClickListener {

    private lateinit var binding: FileListFilterDialogBinding

    var listToFilter: List<DomainInformationForList> = emptyList()
    var currentFilters = mutableListOf<String>()
    var filteredList: List<DomainInformationForList> = emptyList()

    private var startDate = context.getString(R.string.start_date_filter)
    private var endDate = context.getString(R.string.end_date_filter)
    private var event = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = findViewById<View>(android.R.id.content) as ViewGroup
        binding = FileListFilterDialogBinding.inflate(layoutInflater, view)

        binding.buttonApplyFilter.setOnClickListener(this)
        binding.buttonCancelFilter.setOnClickListener(this)
        binding.closeFilterView.setOnClickListener(this)
        binding.startDateTextView.setOnClickListener(this)
        binding.endDateTextView.setOnClickListener(this)
        binding.buttonClearStartDate.setOnClickListener(this)
        binding.buttonClearEndDate.setOnClickListener(this)

        setEventsSpinner()
        setDefaultFilters()
    }

    override fun show() {
        super.show()
        showClearButtons()
    }

    fun isEventSpinnerFilterVisible(isVisible: Boolean) {
        binding.eventsSpinnerFilter.isVisible = isVisible
    }

    private fun setEventsSpinner() {
        val events =
            mutableListOf(
                context.getString(R.string.select_event),
                context.getString(R.string.no_event)
            )
        events.addAll(CameraInfo.events.map { it.name })
        binding.eventsSpinnerFilter.adapter = ArrayAdapter(context, R.layout.spinner_item, events)
    }

    private fun setDefaultFilters() {
        binding.startDateTextView.text = startDate
        binding.endDateTextView.text = endDate
        binding.eventsSpinnerFilter.setSelection(event)
    }

    private fun showClearButtons() {
        binding.buttonClearStartDate.isVisible = try {
            currentFilters[START_DATE_POSITION].isNotEmpty()
        } catch (e: Exception) {
            false
        }
        binding.buttonClearEndDate.isVisible = try {
            currentFilters[END_DATE_POSITION].isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.buttonApplyFilter -> {
                setListOfFilters()
                applyFiltersToLists()
                dismiss()
            }
            binding.buttonCancelFilter, binding.closeFilterView -> cancelChangesAndRestore()
            binding.startDateTextView ->
                binding.startDateTextView.showDateAndTimePickerDialog(0, 0) {
                    binding.buttonClearStartDate.isVisible = true
                }
            binding.endDateTextView ->
                binding.endDateTextView.showDateAndTimePickerDialog(23, 59) {
                    binding.buttonClearEndDate.isVisible = true
                }
            binding.buttonClearStartDate -> clearStartDateFilter()
            binding.buttonClearEndDate -> clearEndDateFilter()
        }
    }

    private fun cancelChangesAndRestore() {
        when (currentFilters.getOrNull(EVENT_POSITION)) {
            "", null, context.getString(R.string.select_event) ->
                binding.eventsSpinnerFilter.setSelection(0)
            context.getString(R.string.no_event) ->
                binding.eventsSpinnerFilter.setSelection(1)
            else -> {
                val eventIndex = CameraInfo.events.indexOfFirst {
                    it.name == currentFilters[EVENT_POSITION]
                } + 2
                binding.eventsSpinnerFilter.setSelection(eventIndex)
            }
        }

        if (!currentFilters.getOrNull(START_DATE_POSITION).isNullOrEmpty()) {
            binding.startDateTextView.text = currentFilters[START_DATE_POSITION]
        } else {
            binding.startDateTextView.text = context.getString(R.string.start_date_filter)
        }

        if (!currentFilters.getOrNull(END_DATE_POSITION).isNullOrEmpty()) {
            binding.endDateTextView.text = currentFilters[END_DATE_POSITION]
        } else {
            binding.endDateTextView.text = context.getString(R.string.end_date_filter)
        }

        saveFiltersAsDefault()
        dismiss()
    }

    private fun clearStartDateFilter() {
        binding.startDateTextView.text = context.getString(R.string.start_date_filter)
        binding.buttonClearStartDate.isVisible = false
    }

    private fun clearEndDateFilter() {
        binding.endDateTextView.text = context.getString(R.string.end_date_filter)
        binding.buttonClearEndDate.isVisible = false
    }

    private fun saveFiltersAsDefault() {
        startDate = binding.startDateTextView.text.toString()
        endDate = binding.endDateTextView.text.toString()
        event = binding.eventsSpinnerFilter.selectedItemId.toInt()
    }

    private fun setListOfFilters() {
        currentFilters = mutableListOf(getStartDate(), getEndDate(), getEvent())
    }

    private fun getStartDate() =
        if (binding.startDateTextView.text != context.getString(R.string.start_date_filter)) binding.startDateTextView.text.toString() else ""

    private fun getEndDate() =
        if (binding.endDateTextView.text != context.getString(R.string.end_date_filter)) binding.endDateTextView.text.toString() else ""

    private fun getEvent() =
        if (binding.eventsSpinnerFilter.selectedItemId > 0) binding.eventsSpinnerFilter.selectedItem.toString() else ""

    fun applyFiltersToLists() {
        tagsGridLayout.removeAllViews()

        var dateTag = ""
        var filteringList = mutableListOf<DomainInformationForList>()
            .apply { addAll(listToFilter) }

        tagsGridLayout.let {
            currentFilters[START_DATE_POSITION].ifIsNotEmptyLet { startDate ->
                val dateWithoutHour = startDate.split(" ")[0]
                filteringList =
                    filteringList.filter { it.domainCameraFile.getCreationDate() >= startDate } as MutableList

                if (currentFilters[END_DATE_POSITION].isEmpty())
                    createTagInPosition(
                        it.childCount,
                        START_DATE_TAG + dateWithoutHour
                    )
                else dateTag = dateWithoutHour
            }

            currentFilters[END_DATE_POSITION].ifIsNotEmptyLet { endDate ->
                val dateWithoutHour = endDate.split(" ")[0]
                filteringList =
                    filteringList.filter { it.domainCameraFile.getCreationDate() <= endDate } as MutableList

                if (currentFilters[START_DATE_POSITION].isEmpty())
                    createTagInPosition(
                        it.childCount,
                        END_DATE_TAG + dateWithoutHour
                    )
                else {
                    dateTag += DATE_RANGE_TAG + dateWithoutHour
                    createTagInPosition(it.childCount, dateTag)
                }
            }

            currentFilters[EVENT_POSITION].ifIsNotEmptyLet { event ->
                filteringList =
                    filteringList.filter {
                    (it as DomainInformationFile).domainVideoMetadata?.metadata?.event?.name ==
                        if (event == NO_EVENT_TAG) null else event
                } as MutableList
                createTagInPosition(it.childCount, EVENT_TAG + event)
            }
        }

        filteredList = filteringList

        saveFiltersAsDefault()
        showClearButtons()
        onApplyClick.invoke(isAnyFilters())
    }

    private fun isAnyFilters() = currentFilters.any { it != "" }

    private fun createTagInPosition(position: Int, text: String) {
        tagsGridLayout.addView(
            SafeFleetFilterTag(context, null, 0).apply {
                tagText = text
                onClicked = {
                    clearTagFilter(text)
                    applyFiltersToLists()
                }
            },
            position
        )
    }

    private fun clearTagFilter(text: String) {
        when {
            text.contains(DATE_RANGE_TAG) -> {
                with(currentFilters) {
                    set(START_DATE_POSITION, "")
                    set(END_DATE_POSITION, "")
                }
                startDate = context.getString(R.string.start_date_filter)
                endDate = context.getString(R.string.end_date_filter)
            }
            text.contains(START_DATE_TAG) -> {
                currentFilters[START_DATE_POSITION] = ""
                startDate = context.getString(R.string.start_date_filter)
            }
            text.contains(END_DATE_TAG) -> {
                currentFilters[END_DATE_POSITION] = ""
                endDate = context.getString(R.string.end_date_filter)
            }
            text == currentFilters[EVENT_POSITION] -> {
                currentFilters[EVENT_POSITION] = ""
                event = 0
            }
        }
        setDefaultFilters()
    }

    companion object {
        private const val START_DATE_POSITION = 0
        private const val END_DATE_POSITION = 1
        private const val EVENT_POSITION = 2
        private const val START_DATE_TAG = "After: "
        private const val END_DATE_TAG = "Before: "
        private const val DATE_RANGE_TAG = " / "
        private const val EVENT_TAG = ""
        private const val NO_EVENT_TAG = "No event"
    }
}
