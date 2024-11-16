/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun EntryScreen(bg: Int, orreryBackground: Int, uiState: StateFlow<OrreryUIState>) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = bg),
                contentScale = ContentScale.FillBounds
            )
    ) {

    }
}