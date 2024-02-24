package fr.lehautcambara.astronomicon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import fr.lehautcambara.astronomicon.ui.theme.AstronomiconTheme
import java.text.DateFormat
import java.util.GregorianCalendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AstronomiconTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Composable
fun Screen(bg: Int, orreryBackground: Int, date: String) {
    Box( modifier = Modifier
        .fillMaxSize()
        .paint(
            painterResource(id = bg),
            contentScale = ContentScale.FillBounds
        )) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            OrreryDate(date)
            OrreryBox(orreryBackground)
        }
    }
}

@Composable
private fun OrreryDate(date: String) {
    Text(
        text = date,
        color = Color.White,
        fontSize = TextUnit(32.0F, TextUnitType.Sp),
    )
}

@Composable
fun OrreryBox(orreryBackground: Int, orbitIncrement: Float = 45F) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = Dp(16F))
        .paint(
            painterResource(id = orreryBackground),
            contentScale = ContentScale.FillWidth
        )) {

        for(i in 1..6) DrawOrbit(radius = orbitIncrement*i,
            modifier= Modifier
                .fillMaxWidth()
                .align(Alignment.Center))

        Image(painterResource(id = R.drawable.sun_anim40), "Sun", modifier = Modifier.align(Alignment.Center))
        DrawPlanet(x = 0, y = 45, id = R.drawable.earthjpg40, modifier = Modifier.align(Alignment.Center))

        // drawPlanet(200F, 200F, R.drawable.earthjpg40, modifier = Modifier.absoluteOffset(x=Dp(0F), y=Dp(0F)))
    }
}

@Composable
fun DrawPlanet(x: Int, y: Int, id: Int, modifier: Modifier) {
    Image(painterResource(id = R.drawable.earthjpg40), "Earth",
        modifier = modifier
            .absoluteOffset { IntOffset(x, -y) })
}


@Composable
private fun DrawOrbit(radius: Float, color: Color = Color.Black, stroke: Float = 2F, modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        drawCircle(color = color, style = Stroke(width = stroke), radius = radius)
    }
}


@Preview
@Composable
fun ScreenPreview() {
    AstronomiconTheme {
        val nowString = DateFormat.getDateInstance().format(GregorianCalendar().time)
        Screen(R.drawable.milkyway, R.drawable.acsquare4, nowString)
    }
}

