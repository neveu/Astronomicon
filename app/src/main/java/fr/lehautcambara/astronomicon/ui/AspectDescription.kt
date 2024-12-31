package fr.lehautcambara.astronomicon.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AspectDescription(uiState: StateFlow<OrreryUIState>) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    Text(text = orreryUIState.aspectDescription)
}