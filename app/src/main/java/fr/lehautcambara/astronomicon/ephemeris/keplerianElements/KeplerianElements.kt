package fr.lehautcambara.astronomicon.ephemeris.keplerianElements

class KeplerianElements(
    val body: String = "Sun",
    val a0: Double = 0.0,
    val e0: Double = 0.0,
    val I0: Double = 0.0,
    val L0: Double = 0.0,
    val wbar0: Double = 0.0,
    val omega0: Double = 0.0,
    val dadt: Double = 0.0, // semi-major axis (au) da/dt (au/century)
    val dedt: Double = 0.0, // eccentricity (unitless), /century
    val dIdt: Double = 0.0, //  inclination (degrees, degrees/century
    val dLdt: Double = 0.0, //  mean longitude (degrees, degrees/century
    val dwbardt: Double = 0.0, //  longitude of perihelion (degrees, degrees/century)
    val domegadt: Double = 0.0, // longitude of the ascending node (degrees, degrees/century)
)  {

    companion object {

        fun EmBary(): KeplerianElements {
            return KeplerianElements(
                "EmBary", 1.00000261, 0.01671123, -0.00001531, 100.46457166, 102.93768193, 0.0,
                0.00000562, -0.00004392, -0.01294668, 35999.37244981, 0.32327364, 0.0
            )
        }

        fun Mercury(): KeplerianElements {
            return KeplerianElements("Mercury",
                0.38709927, 0.20563593, 7.00497902, 252.25032350, 77.45779628, 48.33076593,
                0.00000037, 0.00001906, -0.00594749, 149472.67411175, 0.16047689, -0.12534081
            )
        }

        fun Venus(): KeplerianElements {
            return KeplerianElements("Venus",
                0.72333566, 0.00677672, 3.39467605, 181.97909950, 131.60246718, 76.67984255,
                0.00000390, -0.00004107, -0.00078890, 58517.81538729, 0.00268329, -0.27769418
            )
        }
        fun Mars(): KeplerianElements {
            return KeplerianElements("Mars",
                1.52371034, 0.09339410, 1.84969142, -4.55343205, -23.94362959, 49.55953891,
                0.00001847, 0.00007882, -0.00813131, 19140.30268499, 0.44441088, -0.29257343
            )
        }

        fun Jupiter(): KeplerianElements {
            return KeplerianElements("Jupiter",
                5.20288700, 0.04838624, 1.30439695, 34.39644051, 14.72847983, 100.47390909,
                -0.00011607, -0.00013253, -0.00183714, 3034.74612775, 0.21252668, 0.20469106
            )
        }

        fun Saturn(): KeplerianElements {
            return KeplerianElements("Saturn",
                9.53667594, 0.05386179, 2.48599187, 49.95424423, 92.59887831, 113.66242448,
                -0.00125060, -0.00050991, 0.00193609, 1222.49362201, -0.41897216, -0.28867794
            )
        }

        fun Sun(): KeplerianElements {
            return KeplerianElements("Sun",0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }


    }
}