/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.background
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.DateInputEvent
import fr.lehautcambara.astronomicon.kbus.events.DateSelectedEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import kotlinx.coroutines.flow.StateFlow
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(uiState: StateFlow<OrreryUIState>) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    if (orreryUIState.showDateInput) {
        DatePickerModalInput(
            date = orreryUIState.zonedDateTime,
            onDateSelected = { longdate: Long? ->
                Kbus.post(DateSelectedEvent(longdate))
            },
            onDismiss = {
                Kbus.post(DateInputEvent(false))

            }
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    date: ZonedDateTime,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date.toEpochSecond() * 1000,
        initialDisplayMode = DisplayMode.Picker
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                titleContentColor = Color.Black,
                headlineContentColor = Color.Black,
                weekdayContentColor = Color.Black,
                subheadContentColor = Color.Black,
                navigationContentColor = Color.Black,
                yearContentColor =Color(0xFFdd8d29),
                currentYearContentColor = Color.Black,
                selectedYearContentColor = Color.Black,
                dayContentColor = Color.Black,
                todayContentColor = Color.Black,
                selectedDayContainerColor = Color(0xFFdd8d29),
                selectedYearContainerColor = Color(0xFFdd8d29),
            ),
            modifier = Modifier.background(color = Color(0xFFE5BA90))
        )
    }
}

@Preview
@Composable
fun PreviewDatePickerModalInput() {
    DatePickerModalInput(date = ZonedDateTime.now(),
        onDateSelected = {},
        onDismiss = {}
        )
}