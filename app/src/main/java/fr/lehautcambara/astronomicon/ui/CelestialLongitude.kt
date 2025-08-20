/*
 * Copyright (c) 2025. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Dms
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import kotlinx.coroutines.flow.StateFlow

@Composable
fun CelestialLongitude(uiState: StateFlow<OrreryUIState>,) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    Box(modifier = Modifier) {
        Column() {
            with(orreryUIState) {
                Longitude(sun)
                Longitude(moon)
                Longitude(mercury)
                Longitude(venus)
                Longitude(mars)
                Longitude(jupiter)
                Longitude(saturn)
            }
        }
    }
}

@Composable
private fun OrreryUIState.Longitude(body: Coords) {
    OrreryUIState.fromTo(earth, body)?.toPolar()?.a?.let { a ->
        //Text("""${body.name} ${String.format("%3.2f", angle360(a))}""", color = Color.White)
        Text("""${body.name} ${Dms.fromDecimalDegrees(a).toString()}""", color = androidx.compose.ui.graphics.Color.White)
    }
}

@Preview
@Composable
fun PreviewCelestialLongitude() {
    CelestialLongitude(OrreryVM().uiState)
}