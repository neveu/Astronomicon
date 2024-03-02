package fr.lehautcambara.astronomicon.ephemeris

import cosd
import fr.lehautcambara.astronomicon.ephemeris.keplerianElements.KeplerianElements
import sind
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.IEEErem
import kotlin.math.abs
import kotlin.math.sqrt


data class Coords(val x: Double, val y: Double, val z: Double)
class SolarEphemeris(private val keplerianElements: KeplerianElements): Ephemeris() {
    private var a: Double = 0.0
    private var e: Double =  0.0
    private var I : Double = 0.0
    private var L : Double = 0.0
    private var wbar : Double = 0.0
    private var omega : Double = 0.0
    private var w : Double = 0.0
    private var M: Double = 0.0
    private var E: Double = 0.0
    private var t: Double = 0.0


    private fun calculateOrbit(dateTime: Calendar): Ephemeris {
        t = dateTime.convertToJulianCentury()
        with(keplerianElements) {
            a = ft(a0, dadt, t)
            e = ft(e0, dedt, t)
            I = ft(I0, dIdt, t)
            L = ft(L0, dLdt, t)
            wbar = ft(wbar0, dwbardt, t)
            omega = ft(omega0, domegadt, t)
            w = wbar - omega
            // mean anomaly
            M = plusOrMinus180(L - wbar)
            val estar = 57.29578 * e
            val E0: Double = M + estar * sind(M)
            val tol = 1e-3
            var dE = 1.0
            var En = E0

            while (abs(dE) > tol) {
                val dM: Double = M - (En - estar * sind(En))
                dE = dM / (1 - e * cosd(En))
                En = En + dE
            }
            E = En
        }
        return this
    }
    private fun xp(): Double {
        return a * (cosd(E) - e)
    }

    private fun yp(): Double {
        return a * sqrt(1 - e * e) * sind(E)
    }

     fun xecl(): Double {
        return ((cosd(w) * cosd(omega) - sind(w) * sind(omega) * cosd(I)) * xp()
                + (-sind(w) * cosd(omega) - cosd(w) * sind(omega) * cosd(I)) * yp())
    }

     fun yecl(): Double {
        return ((cosd(w) * sind(omega) + sind(w) * cosd(
            omega
        ) * cosd(I)) * xp()
                + (-sind(w) * sind(omega) + cosd(w) * cosd(
            omega
        ) * cosd(I)) * yp())
    }

     fun zecl(): Double {
        return sind(w) * sind(I) * xp() + cosd(w) * sind(I) * yp()
    }

    private fun xeq(): Double {
        return xecl()
    }

    private fun yeq(): Double {
        return cosd(obliquityJ2000) * yecl() - sind(obliquityJ2000) * zecl()
    }

    private fun zeq(): Double {
        return sind(obliquityJ2000) * yecl() + cosd(obliquityJ2000) * zecl()
    }


    companion object {
        private val obliquityJ2000 = 23.43928
        fun ft(x0: Double, dxdt: Double, t: Double): Double = x0 + (t * dxdt)
        fun plusOrMinus180(v: Double): Double = v.IEEErem(360.0)

    }

    override fun eclipticCoords(dateTime: Calendar): Coords {
        calculateOrbit(dateTime)
        return Coords(xecl(), yecl(), zecl())
    }

    override fun equatorialCoords(dateTime: Calendar): Coords {
        calculateOrbit(dateTime)
        return Coords(xeq(), yeq(), zeq())
    }
}



fun Calendar.convertToJulianCentury(): Double {
    val j2000 = GregorianCalendar(2000, Calendar.JANUARY, 1, 0, 0, 0)
    val Tmsec: Double = (this.timeInMillis - j2000.timeInMillis).toDouble()
    val Tsec = Tmsec / 1000.0
    val Tday = Tsec / (60 * 60 * 24)
    val Tyear = Tday / 365.242
    return Tyear / 100.0 // time in julian centuries
}

fun Calendar.addDays(days: Int): Calendar {
    this.add(GregorianCalendar.DATE, days)
    return this
}
abstract class Ephemeris {
    abstract fun eclipticCoords(dateTime: Calendar): Coords

    abstract fun equatorialCoords(dateTime: Calendar): Coords



}