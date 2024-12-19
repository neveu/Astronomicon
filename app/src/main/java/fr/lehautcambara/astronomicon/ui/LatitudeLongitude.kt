/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.LocationEvent
import fr.lehautcambara.astronomicon.orrery.OrreryVM

@Composable
fun LatitudeLongitude(latitude: Double, longitude: Double) {
    Row(horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            //.padding(horizontal = 32.dp)
    )
    {
        Longitude(longitude)
        //Spacer(modifier = Modifier.width(32.dp))
        Latitude(latitude)

    }
}

@Composable
private fun Longitude(angle: Double) {
    var longitude: String by remember{ mutableStateOf("0.0") }
    longitude = String.format("%+.2f", angle)
    TextField(
        value = longitude,
        label = { Text("Longitude") },
        onValueChange = {
            longitude = it
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                // update uiState

                Kbus.post(LocationEvent(longitude = longitude.toDouble()))
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
        modifier = Modifier

    )
}


@Composable
private fun Latitude(angle: Double) {
    TextField(
        value = String.format("%.2f", angle),
        label = { Text("Latitude") },
        onValueChange = {
            Kbus.post(LocationEvent(latitude = angle))
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
        modifier = Modifier
            .widthIn(1.dp, Dp.Infinity)
    )
}

@Preview
@Composable
fun PreviewLatitudeLongitude() {
    with(OrreryVM().uiState.value) {
        LatitudeLongitude(latitude, longitude )
    }
}