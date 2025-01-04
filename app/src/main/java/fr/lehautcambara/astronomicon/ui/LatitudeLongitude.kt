/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.LocationEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale.getDefault

@Composable
fun LatitudeLongitude(uiState: StateFlow<OrreryUIState>) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    )
    {
        with(orreryUIState) {
            Longitude(longitude, modifier = Modifier.weight(1f))
            Latitude(latitude, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun NumberField(number: Double?, label: String, modifier: Modifier, format: (Double)->String, onNumberChange: (Double?) -> Unit,
) {
    var a: Double? by remember{ mutableStateOf(number)}
    var text: String by remember(number){
        a = number
        mutableStateOf(a?.let{ num: Double ->
            format(num)
        } ?: "")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val textField = FocusRequester()
    TextField(
        value = text,
        label = { Text(text = label) },
        onValueChange = {
            if (it.toDoubleOrNull() != null) {
                text = it
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                text.toDoubleOrNull()?.let {n ->
                    onNumberChange(n)
                }
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            unfocusedLabelColor = Color.White,
            focusedLabelColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier
            .wrapContentWidth()
            .focusRequester(textField)
    )
}

private fun coordFormat(num: Double, plus: String, minus: String): String {
    val direction = if (num < 0) minus else plus
    return String.format(getDefault(), "%.2f%s", Math.abs(num), direction)
}

private fun longitudeFormat(num: Double): String {
    return coordFormat(num, "E", "W")
}

private fun latitudeFormat(num: Double): String {
    return coordFormat(num, "N", "S")
}

@Composable
private fun Longitude(angle: Double, modifier: Modifier) {
   NumberField(number = angle, label = "Longitude",  modifier, format = ::longitudeFormat){ n ->
       Kbus.post(LocationEvent(longitude = n))
   }
}


@Composable
private fun Latitude(angle: Double, modifier: Modifier) {
    NumberField(number = angle, label = "Latitude", modifier, format = ::latitudeFormat){n ->
        Kbus.post(LocationEvent(latitude = n))
    }
}


@Preview
@Composable
fun PreviewLatitudeLongitude() {
    LatitudeLongitude(OrreryVM().uiState )
}