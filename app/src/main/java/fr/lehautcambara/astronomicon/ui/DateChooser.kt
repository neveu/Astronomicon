/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import java.time.ZoneId
import java.time.ZonedDateTime

@Composable
fun DateChooser(zdt: ZonedDateTime, radiusDp: Dp = 400.dp,) {
    var layoutWidth by remember { mutableStateOf(1080F) } // Canvas coords
    var size: Size by remember { mutableStateOf(Size.Zero) }

    Box(modifier = Modifier
        .width(radiusDp)
        .height(radiusDp)
        .background(Color.Black)
        .onGloballyPositioned { coordinates: LayoutCoordinates ->
            layoutWidth = coordinates.boundsInRoot().width
            size = coordinates.size.toSize()
        }
    ) {
        val r0 =  layoutWidth/2
        val r1 = 0.75F * r0
        val r2 = 0.5F * r0
        val r3 = 0.25F * r0

        Canvas(modifier = Modifier.align(Alignment.Center)) {
            drawCircle(color = Color.White, style = Stroke(width = 5F), radius = r0)
            drawCircle(color = Color.White, style = Stroke(width = 5F), radius = r1)
            drawCircle(color = Color.White, style = Stroke(width = 5F), radius = r2)
            drawCircle(color = Color.White, style = Stroke(width = 5F), radius = r3)

        }

    }
}

@Preview
@Composable
fun PreviewDateChooser() {
    val zdtj2000 = ZonedDateTime.of(2000,9, 3, 12, 0, 0, 0, ZoneId.of("GMT"))
    DateChooser(zdtj2000)
}
