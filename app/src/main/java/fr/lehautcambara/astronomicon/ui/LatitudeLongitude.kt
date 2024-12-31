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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.LocationEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

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
private fun GeoCoord(angle: Double, label: String, modifier: Modifier ) {
    var geocoord: String by remember{ mutableStateOf("0.0") }
    geocoord = String.format(Locale.getDefault(), "%+.2f", angle)
    TextField(
        value = geocoord,
        label = { Text(text = label) },
        onValueChange = {
            geocoord = it
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                // update uiState

                Kbus.post(LocationEvent(longitude = geocoord.toDouble()))
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
        modifier = modifier.wrapContentWidth()

    )
}


@Composable
private fun Longitude(angle: Double, modifier: Modifier) {
   GeoCoord(angle = angle, label = "Longitude", modifier)
}


@Composable
private fun Latitude(angle: Double, modifier: Modifier) {
    GeoCoord(angle = angle, label = "Latitude", modifier)

}

@Preview
@Composable
fun PreviewLatitudeLongitude() {
    LatitudeLongitude(OrreryVM().uiState )
}