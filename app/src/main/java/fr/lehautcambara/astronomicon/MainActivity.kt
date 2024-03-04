package fr.lehautcambara.astronomicon

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.addDays
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.RadialScrollEvent
import fr.lehautcambara.astronomicon.orrery.Orrery
import fr.lehautcambara.astronomicon.ui.theme.AstronomiconTheme
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DateFormat
import java.util.Date
import java.util.GregorianCalendar
import kotlin.math.*

class MainActivity : ComponentActivity() {
    private var orrery by mutableStateOf(Orrery())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AstronomiconTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    //color = MaterialTheme.colorScheme.background
                ) {
                    Screen(R.drawable.milkyway, R.drawable.acsquare4, orrery)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Kbus.register(this)
    }

    override fun onPause() {
        Kbus.unregister(this)
        super.onPause()
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: RadialScrollEvent) {
        val scrollAmount = event.radialScroll()
        val currentDate = orrery.dateTime
        currentDate.add(Calendar.HOUR, (scrollAmount / 200F).roundToInt())
        orrery = Orrery(currentDate)
    }
}



@Composable
fun Screen(bg: Int, orreryBackground: Int, orrery: Orrery) {
    val date = orrery.dateTime.time
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
            OrreryBox(orrery, orreryBackground)
        }
    }
}

@Composable
private fun OrreryDate(date: Date) {
    val UglyQuaFamily = FontFamily(
        Font(R.font.uglyqua, FontWeight.Normal),
        Font(R.font.uglyqua_italic, FontWeight.Normal, FontStyle.Italic))
    Text(
        text = DateFormat.getDateInstance().format(date),
        color = Color.White,
        fontFamily = UglyQuaFamily,
        fontSize = TextUnit(32.0F, TextUnitType.Sp),
    )
}

@Composable
fun OrreryBox(orrery: Orrery, orreryBackground: Int, orbitIncrement: Int = 55) {
    var size by remember { mutableStateOf(Size.Zero)}

    Box(modifier = Modifier
        .fillMaxWidth()
        //.padding(horizontal = Dp(16F))
        .paint(
            painterResource(id = orreryBackground),
            contentScale = ContentScale.FillWidth
        )
        .onGloballyPositioned { coordinates ->
            size = coordinates.size.toSize()
        }
        .pointerInput(Unit) {
            detectDragGestures { change: PointerInputChange, dragAmount: Offset ->
                Kbus.post(RadialScrollEvent(size, change.position, dragAmount))
            }
        }
    ) {
        val date = orrery.dateTime
        val modifier = Modifier.align(Alignment.Center)
        with(orrery) {
            DrawPlanetAndOrbit(r = orbitIncrement, mercury.eclipticCoords(date),  id = R.drawable.mercury, modifier = modifier.size(15.dp))
            DrawPlanetAndOrbit(r = 0, sun.eclipticCoords(date), id = R.drawable.sun2, modifier = modifier.size(20.dp))
            DrawPlanetAndOrbit(r = orbitIncrement*2, venus.eclipticCoords(date), id = R.drawable.venus40, modifier = modifier.size(18.dp))
            DrawPlanetAndOrbit(r = orbitIncrement*3, earth.eclipticCoords(date),  id = R.drawable.earthjpg40, modifier = modifier.size(18.dp))
            DrawPlanetAndOrbit(r = orbitIncrement*4, mars.eclipticCoords(date), id = R.drawable.mars, modifier = modifier.size(17.dp))
            DrawPlanetAndOrbit(r = orbitIncrement*5, jupiter.eclipticCoords(date), id = R.drawable.jupiter, modifier = modifier.size(20.dp))
            DrawPlanetAndOrbit(r = orbitIncrement*6, saturn.eclipticCoords(date), id = R.drawable.saturn30, modifier = modifier.size(35.dp))
        }
    }
}

fun angle(x: Double, y: Double): Double {
    return  atan2(y, x)
}

@Composable
fun DrawPlanetAndOrbit(r: Int, coords: Coords, id: Int, modifier: Modifier) {
    DrawPlanetAndOrbit(r = r, xecl = coords.x, yecl = coords.y, id = id, modifier = modifier)
}
@Composable
fun DrawPlanetAndOrbit(r: Int, xecl: Double, yecl: Double, id: Int, modifier: Modifier) {
    DrawOrbit(radius = r, modifier = modifier)
    DrawPlanet(r, xecl, yecl, id, modifier)
}

@Composable
fun DrawPlanet(r: Int, xecl: Double, yecl: Double, id: Int, modifier: Modifier) {
    val ang = angle(xecl, yecl)
    DrawPlanet(r, ang, id, modifier)
}

@Composable
fun DrawPlanet(r: Int, a: Double, id: Int, modifier: Modifier){
    val x = (r*cos(a)).roundToInt()
    val y = (r*sin(a)).roundToInt()
    DrawPlanet(x,y,id, modifier)
}
@Composable
fun DrawPlanet(x: Int, y: Int, id: Int, modifier: Modifier) {
    // shadow
    Image(painterResource(id = R.drawable.shadow), "shadow",
        modifier=modifier.absoluteOffset {IntOffset(x+20, -(y - 20) ) }

    )
    Image(painterResource(id = id), "Planet",
        modifier = modifier.absoluteOffset { IntOffset(x, -y) }
    )
}

@Composable
private fun DrawOrbit(radius: Int, color: Color = Color.Black, stroke: Float = 2F, modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        drawCircle(color = color, style = Stroke(width = stroke), radius = radius.toFloat())
    }
}


@Preview
@Composable
fun ScreenPreview() {
    AstronomiconTheme {
        Screen(R.drawable.milkyway, R.drawable.acsquare4, Orrery())
    }
}

