/*
 * Copyright (c) 2025. Charles Frederick Neveu All Rights Reserved.
 */

package fr.lehautcambara.astronomicon.astrology

import java.time.ZonedDateTime


/*
    * @see #swe_houses(double, int, double, double, int, double[], double[])
    * @param tjd_ut The Julian Day number in UT
    * @param geolat The latitude on earth, for which the calculation has to be
    * done.
    * @param geolong The longitude on earth, for which the calculation has to be
    * done.
    * @param hsys The house system as a character given as an integer.
    * @param cusp The house cusps are returned here in cusp[1...12] for
    * the house 1 to 12.
    * @param ascmc The special points like ascendant etc. are returned here.
*/

    class Houses() {
        var ac: Double? = null
        var mc: Double? = null

    }


    fun ZonedDateTime.sweHouses( latitude: Double, longitude: Double, hsys: Int, cusp: Array<Double>, asmc: Array<Double>)
    {
        val houses = Houses()
        val julianCentury = convertToJ2000Century()
        houses.mc = MC(longitude)
    }
