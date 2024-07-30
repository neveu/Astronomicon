package fr.lehautcambara.astronomicon.orrery

import androidx.lifecycle.ViewModel
import fr.lehautcambara.astronomicon.kbus.AspectDescriptionEvent
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.PlanetClickEvent
import fr.lehautcambara.astronomicon.kbus.RadialScrollEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.ZonedDateTime
import kotlin.math.roundToLong

class OrreryVM : ViewModel() {

    init {
        Kbus.register(this)
//        val zdt = ZonedDateTime.of(1959, 8, 1, 8, 37, 0, 0, ZoneId.of("America/Chicago"))
//        val jan31 = ZonedDateTime.of(2000, 1, 31, 12, 0, 0, 0, ZoneId.of("GMT"))
//        natalDate(zdt, latitude = 43.074761, longitude = -89.3837613)
    }
    private var zonedDateTime: ZonedDateTime = ZonedDateTime.now()
    private var _uiState = MutableStateFlow(OrreryUIState(zonedDateTime))
    val uiState: StateFlow<OrreryUIState> = _uiState.asStateFlow()
    private val displayModes: Array<DisplayMode> = DisplayMode.values()

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: RadialScrollEvent) {
        val scale = uiState.value.displayMode.scale(event.radialScroll())
        val scrollAmount = (scale).roundToLong()
        zonedDateTime = zonedDateTime.plusMinutes(scrollAmount)
        _uiState.update { uistate ->
            uistate.copy(zonedDateTime = zonedDateTime)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: PlanetClickEvent) {
        val index = (displayModes.indexOf(uiState.value.displayMode) + 1) % displayModes.size
        val newDisplayMode = displayModes[index]
        _uiState.update { uistate ->
            uistate.copy(displayMode = newDisplayMode)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: AspectDescriptionEvent) {
//        _uiState.update { uiState ->
//            uiState.copy(aspectDescription = event.description)
//        }
    }
}

