package fr.lehautcambara.astronomicon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.lehautcambara.astronomicon.orrery.OrreryVM
import fr.lehautcambara.astronomicon.ui.OrreryScreen
import fr.lehautcambara.astronomicon.ui.theme.AstronomiconTheme

class NavActivity : ComponentActivity() {
    private var orreryVM = OrreryVM()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AstronomiconTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    //color = MaterialTheme.colorScheme.background
                ) {
                    OrreryScreen(R.drawable.milkyway, R.drawable.acsquare4, orreryVM)
                }
            }
        }
    }

}


@Preview
@Composable
fun ScreenPreview() {
    AstronomiconTheme {
        OrreryScreen(R.drawable.milkyway, R.drawable.acsquare4, OrreryVM())
    }
}

