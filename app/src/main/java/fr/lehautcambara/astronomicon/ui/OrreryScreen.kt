package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.orrery.DisplayMode
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import fr.lehautcambara.astronomicon.ui.theme.AstronomiconTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OrreryScreen(uiState: StateFlow<OrreryUIState>) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    val bg = orreryUIState.background
    Box( modifier = Modifier
        .fillMaxSize()
        .paint(
            painterResource(id = bg),
            contentScale = ContentScale.FillBounds
        )
    )
    {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            OrreryDate(uiState)
            OrreryBox(uiState, R.drawable.acsquare4)
            if (orreryUIState.displayMode != DisplayMode.NatalChart) {
                LunarPhaseBox(
                    uiState,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(vertical = 32.dp)
                )
            } else {
                LatitudeLongitude(uiState)
                PolarMap(orreryUIState.longitude.toFloat())
            }
        }
    }
    DatePickerModalInput(uiState = uiState)
}



@Preview
@Composable
fun PreviewOrreryScreen() {
    AstronomiconTheme(darkTheme = false, dynamicColor = false) {
        OrreryScreen(OrreryVM().uiState)
    }
}


