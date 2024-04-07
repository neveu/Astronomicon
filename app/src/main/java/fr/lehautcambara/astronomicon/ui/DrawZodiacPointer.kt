package fr.lehautcambara.astronomicon.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cosd
import sind
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DrawZodiacPointer(radius: Int, a: Double, color: Color = Color.Red, width: Float = 5F, modifier: Modifier) {
       Canvas(modifier = modifier) {
           val path = Path()
           path.moveTo(0F, 0F)
           val x = (radius * cos(a)).toFloat()
           val y = -radius * sin(a).toFloat()
           path.lineTo(x, y)
           path.close()
           drawPath(path, color, style = Stroke(width = width))
       }
}

@Preview
@Composable
private fun PreviewDrawZodiacPointer() {
    Box(modifier = Modifier
        .size(100.dp)
        .background(Color.White)
    ) {
        val modifier = Modifier.align(Alignment.Center)
        DrawZodiacPointer(radius = 100, a = 0.0, width =3F,  modifier = modifier)
        DrawZodiacPointer(radius = 100, a = 45.0, width =3F,  modifier = modifier)



    }
}
