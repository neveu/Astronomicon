/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.R
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.events.LongitudeScrollEvent
import fr.lehautcambara.astronomicon.orrery.OrreryUIState
import kotlinx.coroutines.flow.StateFlow

@Composable
private fun pixToDp(pixels: Double): Dp {
    val screenPixelDensity = LocalContext.current.resources.displayMetrics.density
    val dpValue = ((pixels * 2.0) / screenPixelDensity).dp
    return dpValue
}

@Composable
fun PolarMap(angle: Float = 0F) {
    var size: Size by remember { mutableStateOf(Size.Zero) }
    Box(modifier = Modifier
        .onGloballyPositioned { coordinates: LayoutCoordinates ->
            size = coordinates.size.toSize()
        }
        .pointerInput(Unit) {
            detectDragGestures { change: PointerInputChange, dragAmount: Offset ->
                Kbus.post(LongitudeScrollEvent(size, change.position, dragAmount))
            }
        }

    ) {
        val dpValue = pixToDp(360.0 * 0.95)
        Image(
            painterResource(id = R.drawable.azimuthal_equidistant_projection_sw_canvas),
            contentDescription = "",
            modifier = Modifier
                .size(dpValue)
                .align(Alignment.Center)
                .rotate(-angle)
        )

        Canvas(modifier = Modifier ) {
            drawLine(
                color = Color.Black,
                start = Offset(size.width/2.0f, size.height/2),
                end = Offset(size.width/2.0f, size.height),
                strokeWidth = 3F

            )
        }
    }
}

@Composable
fun PolarMap(uiState: StateFlow<OrreryUIState>) {
    val orreryUIState: OrreryUIState by uiState.collectAsState()
    PolarMap(orreryUIState.longitude.toFloat())
}

@Preview
@Composable
fun PreviewPolarMap() {
    PolarMap()
}