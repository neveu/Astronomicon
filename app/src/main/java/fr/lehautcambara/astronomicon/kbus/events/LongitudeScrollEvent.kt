/*
 * Copyright (c) 2024. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.kbus.events

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import fr.lehautcambara.astronomicon.kbus.BusEvent

data class LongitudeScrollEvent(val size: Size, val change: Offset, val dragAmount: Offset): BusEvent() {
    private fun outerProduct(v1: Offset, v2: Offset): Float = (v2.y * v1.x) - (v2.x * v1.y)

    fun radialScroll(
    ): Float {
        val radialVector = change - Offset(size.width/2F, size.height/2F)
        return outerProduct(dragAmount, radialVector)
    }

}
