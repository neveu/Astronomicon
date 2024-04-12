package fr.lehautcambara.astronomicon.kbus

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class RadialScrollEvent(val size: Size, val change: Offset, val dragAmount: Offset): BusEvent(){
    private fun outerProduct(v1: Offset, v2: Offset): Float = (v2.y * v1.x) - (v2.x * v1.y)

    fun radialScroll(
    ): Float {
        val radialVector = change - Offset(size.width/2F, size.height/2F)
        return outerProduct(dragAmount, radialVector)
    }

}