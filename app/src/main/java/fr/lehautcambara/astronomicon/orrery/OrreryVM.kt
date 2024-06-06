package fr.lehautcambara.astronomicon.orrery

import androidx.lifecycle.ViewModel
import angled
import fr.lehautcambara.astronomicon.astrology.natalDate
import fr.lehautcambara.astronomicon.ephemeris.Coords
import fr.lehautcambara.astronomicon.ephemeris.Ephemeris
import fr.lehautcambara.astronomicon.kbus.Kbus
import fr.lehautcambara.astronomicon.kbus.PlanetClickEvent
import fr.lehautcambara.astronomicon.kbus.RadialScrollEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToLong

class OrreryVM : ViewModel() {

    init {
        Kbus.register(this)
        natalDate(2000, 1, 1, 12, 0, ZoneId.of("GMT"))
        natalDate(1959, 8, 25, 8, 37, ZoneId.of("America/Chicago"))
    }
    private var zonedDateTime: ZonedDateTime = ZonedDateTime.now()
    private var _uiState = MutableStateFlow(OrreryUIState(zonedDateTime))
    val uiState: StateFlow<OrreryUIState> = _uiState.asStateFlow()
    private val displayModes: Array<DisplayMode> = DisplayMode.values()

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onEvent(event: RadialScrollEvent) {
        val scrollAmount = (event.radialScroll()/200F).roundToLong()
        zonedDateTime = zonedDateTime.plusHours(scrollAmount)
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

    private fun fromTo(from: Ephemeris?, to: Ephemeris?): Coords? {
        val currentDate = _uiState.value.julianCentury
        return fromTo(from?.eclipticCoords(currentDate), to?.eclipticCoords(currentDate))
    }

    private fun fromTo(from: Coords?, to: Coords?): Coords? {
        if (from == null) return to
        return if (to == null) -from
        else to - from
    }

    private fun angle(from: Ephemeris?, to: Ephemeris?): Double {
        fromTo(from, to)?.apply {
            return angled(x, y)
        }
        return 0.0
    }

    private fun angle(from: Coords?, to: Coords?) : Double {
        fromTo(from, to)?.apply {
            return angled(x, y)
        }
        return 0.0
    }


}

