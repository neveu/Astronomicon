package fr.lehautcambara.astronomicon

import android.icu.util.GregorianCalendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import fr.lehautcambara.astronomicon.ui.theme.AstronomiconTheme
import java.text.DateFormat

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
        fontSize = TextUnit(32.0f, TextUnitType.Sp),
    )
}

@Composable
fun OrreryBox(orreryBackground: Int) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(Dp(16f))
        .paint(
            painterResource(id = orreryBackground),
            contentScale = ContentScale.FillWidth
        )){
        Image(painterResource(id = R.drawable.sun1), "Sun", modifier = Modifier.align(Alignment.Center))
    }
}

@Preview
@Composable
fun ScreenPreview() {
    AstronomiconTheme {
        val now = GregorianCalendar().time
        val nowString = DateFormat.getDateInstance().format(now)
        Screen(R.drawable.milkyway, R.drawable.acsquare4, nowString)
    }
}

