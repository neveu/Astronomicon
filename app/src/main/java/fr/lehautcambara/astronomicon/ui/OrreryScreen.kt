package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.orrery.DisplayMode
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import fr.lehautcambara.astronomicon.ui.theme.AstronomiconTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OrreryScreen(bg: Int, orreryBackground: Int, uiState: StateFlow<OrreryUIState>) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    Box( modifier = Modifier
        .fillMaxSize()
        .paint(
            painterResource(id = bg),
            contentScale = ContentScale.FillBounds
        )
    )
    {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OrreryDate(uiState)
            OrreryBox(uiState, orreryBackground)
            if (orreryUIState.displayMode != DisplayMode.NatalChart) {
                LunarPhaseBox(
                    uiState, modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .padding(vertical = 32.dp)
                )
            } else {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 32.dp)
                    )
                {
                    val lat = String.format("%.2f", uiState.value.latitude)
                    val lon = String.format("%.2f", uiState.value.longitude)
                    Text("Latitude: $lat", fontSize = 22.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(32.dp))
                    Text("Longitude: $lon", fontSize = 22.sp, color = Color.White)

                }
            }
        }
        DatePickerModalInput(uiState = uiState)
    }
}

@Preview
@Composable
fun PreviewOrreryScreen() {
   AstronomiconTheme(darkTheme = false, dynamicColor = false) {
        OrreryScreen(R.drawable.milkyway, R.drawable.acsquare4, OrreryVM().uiState)

    }
}


