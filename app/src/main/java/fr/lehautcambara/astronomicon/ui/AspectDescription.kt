package fr.lehautcambara.astronomicon.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import fr.lehautcambara.astronomicon.orrery.OrreryUIState

@Composable
fun AspectDescription(uiState: OrreryUIState) {
    Text(text = uiState.aspectDescription)
}