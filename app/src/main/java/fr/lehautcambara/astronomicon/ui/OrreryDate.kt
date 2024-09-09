package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.DateInputEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import fr.lehautcambara.astronomicon.ui.theme.AstronomiconTheme
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OrreryDate(uiState: StateFlow<OrreryUIState>) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    Text(
        text = orreryUIState.toString(),
        color = Color.White,
        fontSize = TextUnit(22.0F, TextUnitType.Sp),
        modifier = Modifier.padding(16.dp).clickable {
            Kbus.post(DateInputEvent(true))
        }
    )
}

@Preview
@Composable
fun PreviewOrreryDate()
{
    AstronomiconTheme {
        OrreryDate(OrreryVM().uiState)
    }
}