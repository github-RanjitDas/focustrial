package com.lawmobile.presentation.widgets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.GridLayout
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationForList
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.ifIsNotEmptyLet
import com.lawmobile.presentation.extensions.showDateAndTimePickerDialog
import com.safefleet.mobile.commons.widgets.SafeFleetFilterTag
import kotlinx.android.synthetic.main.file_list_filter_dialog.*

class CustomFilterDialog constructor(
    context: Context,
    cancelable: Boolean,
    private val tagsGridLayout: GridLayout,
    private val listToFilter: List<DomainInformationForList>
) : Dialog(context, cancelable, null), View.OnClickListener {

    var onApplyClick: ((Boolean) -> Unit)? = null
    private var currentFilters = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.file_list_filter_dialog)

        buttonApplyFilter.setOnClickListener(this)
        buttonCancelFilter.setOnClickListener(this)
        closeFilterView.setOnClickListener(this)
        startDateTextView.setOnClickListener(this)
        endDateTextView.setOnClickListener(this)

        setEventsSpinner()
        setDefaultFilters()
    }

    private fun setEventsSpinner() {
        val events =
            mutableListOf(context.getString(R.string.select), context.getString(R.string.no_event))
        events.addAll(CameraInfo.events.map { it.name })
        eventsSpinnerFilter.adapter = ArrayAdapter(context, R.layout.spinner_item, events)
    }

    private fun setDefaultFilters() {
        startDateTextView.text = startDate
        endDateTextView.text = endDate
        eventsSpinnerFilter.setSelection(event)
    }

    override fun onClick(v: View?) {
        when (v) {
            buttonApplyFilter -> {
                saveFiltersAsDefault()
                currentFilters = getListOfFilters()
                applyFiltersToLists(currentFilters)
                dismiss()
            }
            buttonCancelFilter, closeFilterView -> dismiss()
            startDateTextView -> context.showDateAndTimePickerDialog(startDateTextView, 0, 0)
            endDateTextView -> context.showDateAndTimePickerDialog(endDateTextView, 23, 59)
        }
    }

    private fun saveFiltersAsDefault() {
        startDate = startDateTextView.text.toString()
        endDate = endDateTextView.text.toString()
        event = eventsSpinnerFilter.selectedItemId.toInt()
    }

    private fun getListOfFilters(): ArrayList<String> =
        arrayListOf(getStartDate(), getEndDate(), getEvent())

    private fun getStartDate() =
        if (startDateTextView.text != context.getString(R.string.start_date_filter)) startDateTextView.text.toString() else ""

    private fun getEndDate() =
        if (endDateTextView.text != context.getString(R.string.end_date_filter)) endDateTextView.text.toString() else ""

    private fun getEvent() =
        if (eventsSpinnerFilter.selectedItemId > 0) eventsSpinnerFilter.selectedItem.toString() else ""

    private fun applyFiltersToLists(filters: List<String>) {
        var dateTag = ""
        tagsGridLayout.removeAllViews()

        var filteringList = listToFilter

        filters[START_DATE_POSITION].ifIsNotEmptyLet { startDate ->
            val dateWithoutHour = startDate.split(" ")[0]
            filteringList =
                filteringList.filter { it.cameraConnectFile.getCreationDate() >= startDate }

            if (filters[END_DATE_POSITION].isEmpty())
                createTagInPosition(
                    tagsGridLayout.childCount,
                    START_DATE_TAG + dateWithoutHour
                )
            else dateTag = dateWithoutHour
        }

        filters[END_DATE_POSITION].ifIsNotEmptyLet { endDate ->
            val dateWithoutHour = endDate.split(" ")[0]
            filteringList =
                filteringList.filter { it.cameraConnectFile.getCreationDate() <= endDate }

            if (filters[START_DATE_POSITION].isEmpty())
                createTagInPosition(
                    tagsGridLayout.childCount,
                    END_DATE_TAG + dateWithoutHour
                )
            else {
                dateTag += DATE_RANGE_TAG + dateWithoutHour
                createTagInPosition(tagsGridLayout.childCount, dateTag)
            }
        }

        if (filteringList.firstOrNull() is DomainInformationFile) {
            filters[EVENT_POSITION].ifIsNotEmptyLet { event ->
                filteringList =
                    filteringList.filter {
                        (it as DomainInformationFile).cameraConnectVideoMetadata?.metadata?.event?.name ==
                                if (event == NO_EVENT_TAG) null else event
                    }
                createTagInPosition(tagsGridLayout.childCount, EVENT_TAG + event)
            }
        }

        filteredList = filteringList

        val isAnyFilter = filters.any { it != "" }
        onApplyClick?.invoke(isAnyFilter)
    }

    private fun createTagInPosition(position: Int, text: String) {
        tagsGridLayout.addView(
            SafeFleetFilterTag(context, null, 0).apply {
                tagText = text
                onClicked = {
                    clearTagFilter(text)
                    applyFiltersToLists(currentFilters)
                }
            }, position
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
    }

    companion object {
        var filteredList: List<DomainInformationForList> = emptyList()

        var startDate = "Start date"
        var endDate = "End date"
        var event = 0

        const val START_DATE_POSITION = 0
        const val END_DATE_POSITION = 1
        const val EVENT_POSITION = 2
        const val START_DATE_TAG = "After: "
        const val END_DATE_TAG = "Before: "
        const val DATE_RANGE_TAG = " / "
        const val EVENT_TAG = ""
        const val NO_EVENT_TAG = "No event"

        fun resetCompanion() {
            startDate = "Start date"
            endDate = "End date"
            event = 0
        }
    }
}